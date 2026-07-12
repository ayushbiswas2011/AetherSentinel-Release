package com.aethersentinel.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

/*
 * STAND-IN DECLARATION — see AetherConfig.kt header comment for context.
 * Delete this file once the real .aar is wired in.
 */

/**
 * Raw radio/network telemetry snapshot. Note: this reports RADIO and
 * NETWORK conditions (signal power/quality/latency/bandwidth), NOT
 * device CPU/RAM.
 */
data class RawTelemetrySnapshot(
    val rsrpDbm: Int?,
    val rsrqDb: Int?,
    val sinrDb: Int?,
    val genericSignalDbm: Int?,
    val isWifi: Boolean,
    val isCellular: Boolean,
    val isMetered: Boolean,
    val downstreamBandwidthKbps: Int?,
    val upstreamBandwidthKbps: Int?,
    val observedRttMillis: Long?,
    val throughputBytesPerSecond: Double?
)

data class TelemetrySample(
    val label: String,
    val value: Double,
    val capturedAtMillis: Long
)

fun interface HarvesterListener {
    fun onSnapshot(snapshot: RawTelemetrySnapshot, samples: List<TelemetrySample>)
}

/**
 * Exposed on the real SDK as a background telemetry source. In this
 * showcase, [start] emits a realistic simulated snapshot on a fixed
 * interval until [stop] is called.
 */
object PhyHarvester {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    fun start(listener: HarvesterListener, intervalMillis: Long = 1_500L) {
        stop()
        job = scope.launch {
            while (isActive) {
                val snapshot = generateSnapshot()
                val samples = generateSamples(snapshot)
                withContext(Dispatchers.Main) {
                    listener.onSnapshot(snapshot, samples)
                }
                delay(intervalMillis)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    private fun generateSnapshot(): RawTelemetrySnapshot {
        val isWifi = Random.nextInt(100) < 55
        return RawTelemetrySnapshot(
            rsrpDbm = if (!isWifi) Random.nextInt(-110, -75) else null,
            rsrqDb = if (!isWifi) Random.nextInt(-16, -6) else null,
            sinrDb = if (!isWifi) Random.nextInt(2, 28) else null,
            genericSignalDbm = if (isWifi) Random.nextInt(-70, -35) else null,
            isWifi = isWifi,
            isCellular = !isWifi,
            isMetered = !isWifi && Random.nextInt(100) < 30,
            downstreamBandwidthKbps = Random.nextInt(8_000, 180_000),
            upstreamBandwidthKbps = Random.nextInt(2_000, 40_000),
            observedRttMillis = Random.nextLong(14, 220),
            throughputBytesPerSecond = Random.nextDouble(50_000.0, 4_500_000.0)
        )
    }

    private fun generateSamples(snapshot: RawTelemetrySnapshot): List<TelemetrySample> {
        val now = System.currentTimeMillis()
        return listOfNotNull(
            snapshot.rsrpDbm?.let { TelemetrySample("RSRP", it.toDouble(), now) },
            snapshot.rsrqDb?.let { TelemetrySample("RSRQ", it.toDouble(), now) },
            snapshot.sinrDb?.let { TelemetrySample("SINR", it.toDouble(), now) },
            snapshot.observedRttMillis?.let { TelemetrySample("RTT", it.toDouble(), now) }
        )
    }
}
