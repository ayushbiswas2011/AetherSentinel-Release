package com.aethersentinel.showcase.data

import android.content.Context
import com.aethersentinel.core.AetherConfig
import com.aethersentinel.core.OkAether
import com.aethersentinel.core.AetherSentinelInterceptor
import com.aethersentinel.core.HarvesterListener
import com.aethersentinel.core.LedgerEntry
import com.aethersentinel.core.LedgerState
import com.aethersentinel.core.LicenseStatus
import com.aethersentinel.core.PhyHarvester

/**
 * Thin repository sitting between the ViewModels and the OkAether
 * Core SDK entrypoint. Keeps every direct SDK call in one place so the
 * ViewModels stay focused on UI state.
 */
class AetherRepository(private val appContext: Context) {

    fun bootstrap(licenseKey: String? = null, config: AetherConfig = AetherConfig.DEFAULT) {
        OkAether.bootstrap(appContext, licenseKey, config)
    }

    fun setForeground(inForeground: Boolean) {
        OkAether.setAppInForeground(inForeground)
    }

    fun confirmAttestation(score: Double): LicenseStatus {
        OkAether.notifyServerConfirmedAttestationScore(score)
        return OkAether.licenseGatekeeper.currentStatus()
    }

    fun currentLicenseStatus(): LicenseStatus = OkAether.licenseGatekeeper.currentStatus()

    fun interceptor(): AetherSentinelInterceptor = OkAether.interceptor()

    fun startTelemetry(listener: HarvesterListener, intervalMillis: Long = 1_500L) {
        PhyHarvester.start(listener, intervalMillis)
    }

    fun stopTelemetry() {
        PhyHarvester.stop()
    }

    fun activeQueueDepth(): Int = OkAether.ledgerDatabase.findAllInStates(
        setOf(LedgerState.PENDING, LedgerState.IN_FLIGHT, LedgerState.SUSPENDED)
    ).size

    fun allLedgerEntries(): List<LedgerEntry> = OkAether.ledgerDatabase.findAllInStates(
        LedgerState.entries.toSet()
    )

    fun churnLedger(): LedgerEntry = OkAether.ledgerDatabase.churn()
}
