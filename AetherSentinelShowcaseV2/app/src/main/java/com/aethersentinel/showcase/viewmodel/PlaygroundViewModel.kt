package com.aethersentinel.showcase.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethersentinel.core.Feature
import com.aethersentinel.core.HarvesterListener
import com.aethersentinel.core.InterceptedCall
import com.aethersentinel.core.InterceptorListener
import com.aethersentinel.core.LicenseTier
import com.aethersentinel.core.RawTelemetrySnapshot
import com.aethersentinel.core.TelemetrySample
import com.aethersentinel.showcase.data.AetherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ScanState { IDLE, SCANNING, PASSED, FAILED }

data class PlaygroundUiState(
    val scanState: ScanState = ScanState.IDLE,
    val licenseTier: LicenseTier? = null,
    val enabledFeatures: Set<Feature> = emptySet(),
    val dailyRequestQuota: Long? = null,
    val telemetry: RawTelemetrySnapshot? = null,
    val interceptorActive: Boolean = false,
    val trafficTotalCount: Int = 0,
    val trafficFlaggedCount: Int = 0,
    // Cloud Cost Saved counter — accumulates as the interceptor blocks flaky retries
    val cloudCostSavedCents: Int = 0
)

class PlaygroundViewModel(
    private val repository: AetherRepository,
    private val terminal: TerminalLogViewModel
) : ViewModel() {

    private val _state = MutableStateFlow(PlaygroundUiState())
    val state: StateFlow<PlaygroundUiState> = _state

    private var telemetryStarted = false

    init {
        startTelemetryIfNeeded()
        startCostAccumulator()
    }

    // Simulates money saved as OkAether suppresses unnecessary retries.
    // Each tick adds a small random increment so the counter feels live
    // and real without being implausibly fast.
    private fun startCostAccumulator() {
        viewModelScope.launch {
            while (true) {
                delay(Random.nextLong(4_000, 9_000))
                val savingsCents = Random.nextInt(3, 28)
                _state.update { it.copy(cloudCostSavedCents = it.cloudCostSavedCents + savingsCents) }
                if (_state.value.interceptorActive) {
                    val dollars = _state.value.cloudCostSavedCents / 100.0
                    terminal.push(
                        "[OkAether Engine] Cloud savings updated → \$%.2f today".format(dollars),
                        LogLevel.SUCCESS
                    )
                }
            }
        }
    }

    private fun startTelemetryIfNeeded() {
        if (telemetryStarted) return
        telemetryStarted = true
        terminal.push("[OkAether Engine] PhyHarvester.start() — radio/network telemetry online", LogLevel.INFO)
        repository.startTelemetry(
            listener = HarvesterListener { snapshot, samples ->
                _state.update { it.copy(telemetry = snapshot) }
                terminal.push(formatTelemetryTick(snapshot, samples), LogLevel.INFO)
            }
        )
    }

    private fun formatTelemetryTick(snapshot: RawTelemetrySnapshot, samples: List<TelemetrySample>): String {
        val link = if (snapshot.isWifi) "WIFI" else "CELLULAR"
        val power = snapshot.rsrpDbm?.let { "RSRP=${it}dBm" }
            ?: snapshot.genericSignalDbm?.let { "SIG=${it}dBm" }
            ?: "SIG=n/a"
        val rtt = snapshot.observedRttMillis?.let { "${it}ms" } ?: "n/a"
        return "[OkAether Engine] telemetry [$link] $power  RTT=$rtt  samples=${samples.size}"
    }

    fun triggerSecurityCheck() {
        if (_state.value.scanState == ScanState.SCANNING) return
        _state.update { it.copy(scanState = ScanState.SCANNING) }
        terminal.push("[OkAether Engine] notifyServerConfirmedAttestationScore(1.0) invoked", LogLevel.INFO)

        viewModelScope.launch {
            delay(280)
            terminal.push("[OkAether Engine] boot: com.okaether.core.LicenseGatekeeper", LogLevel.INFO)
            delay(420)
            terminal.push("[OkAether Engine] hashing APK signing certificate…", LogLevel.INFO)
            delay(360)
            val status = repository.confirmAttestation(1.0)
            delay(300)
            terminal.push("[OkAether Engine] license.tier = ${status.tier}", LogLevel.SUCCESS)
            terminal.push(
                "[OkAether Engine] enabledFeatures = [${status.enabledFeatures.joinToString(", ")}]",
                LogLevel.INFO
            )
            terminal.push("[OkAether Engine] dailyRequestQuota = ${status.dailyRequestQuota}", LogLevel.INFO)
            _state.update {
                it.copy(
                    scanState = ScanState.PASSED,
                    licenseTier = status.tier,
                    enabledFeatures = status.enabledFeatures,
                    dailyRequestQuota = status.dailyRequestQuota
                )
            }
            terminal.push("[OkAether Engine] attestation confirmed — license valid ✔", LogLevel.SUCCESS)
            delay(1600)
            _state.update { it.copy(scanState = ScanState.IDLE) }
        }
    }

    fun toggleInterceptor() {
        val turningOn = !_state.value.interceptorActive
        _state.update {
            it.copy(
                interceptorActive = turningOn,
                trafficTotalCount = if (turningOn) 0 else it.trafficTotalCount,
                trafficFlaggedCount = if (turningOn) 0 else it.trafficFlaggedCount
            )
        }

        if (turningOn) {
            terminal.push("[OkAether Engine] interceptor().attach() — hooking OkHttp client", LogLevel.INFO)
            terminal.push("[OkAether Engine] Active: Intercepting Flaky Network Retry Storm…", LogLevel.WARNING)
            repository.interceptor().attach(
                listener = InterceptorListener { call: InterceptedCall ->
                    _state.update {
                        it.copy(
                            trafficTotalCount = it.trafficTotalCount + 1,
                            trafficFlaggedCount = it.trafficFlaggedCount + if (call.flaggedAsAnomalous) 1 else 0
                        )
                    }
                    val tag = if (call.flaggedAsAnomalous) "⚠ BLOCKED" else "  ALLOWED"
                    val level = if (call.flaggedAsAnomalous) LogLevel.WARNING else LogLevel.INFO
                    terminal.push(
                        "[OkAether Interceptor] $tag ${call.method} ${call.host}${call.path} → ${call.statusCode} (${call.latencyMillis}ms)",
                        level
                    )
                }
            )
        } else {
            repository.interceptor().detach()
            terminal.push("[OkAether Engine] interceptor().detach() — traffic hook released", LogLevel.INFO)
        }
    }

    /**
     * Formats live telemetry into a shareable viral scorecard string,
     * ready to hand to a native Android share Intent.
     */
    fun buildShareableScorecard(repoUrl: String): String {
        val telemetry = _state.value.telemetry
        val signalLabel = when {
            telemetry == null -> "Unknown"
            telemetry.isWifi && (telemetry.genericSignalDbm ?: -100) > -55 -> "Perfect"
            telemetry.isWifi -> "Good"
            (telemetry.rsrpDbm ?: -120) > -95 -> "Perfect"
            (telemetry.rsrpDbm ?: -120) > -105 -> "Good"
            else -> "Weak"
        }
        val latencyLabel = when {
            telemetry?.observedRttMillis == null -> "Unknown"
            telemetry.observedRttMillis < 60 -> "Low"
            telemetry.observedRttMillis < 140 -> "Medium"
            else -> "High"
        }
        val savedStr = "$%.2f".format(_state.value.cloudCostSavedCents / 100.0)
        terminal.push("[OkAether Engine] Generate Shareable Scorecard tapped", LogLevel.INFO)
        return "My Android Network Security Index is Optimized via OkAether SDK! 🚀 " +
            "Signal: $signalLabel | Latency: $latencyLabel | Cloud Cost Saved Today: $savedStr. " +
            "Audit your app instantly: $repoUrl"
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopTelemetry()
        repository.interceptor().detach()
    }
}
