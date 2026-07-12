package com.aethersentinel.core

import android.content.Context

/*
 * STAND-IN DECLARATION — see AetherConfig.kt header comment for context.
 * Delete this file once the real .aar is wired in.
 */

/**
 * Entrypoint singleton for the OkAether Core SDK.
 */
object OkAether {

    val licenseGatekeeper: LicenseGatekeeper = LicenseGatekeeper
    val ledgerDatabase: LedgerDatabase = LedgerDatabase

    private var bootstrapped = false
    private var appInForeground = true
    private var lastAttestationScore: Double? = null
    private val sharedInterceptor = AetherSentinelInterceptor()

    fun bootstrap(
        context: Context,
        licenseKey: String? = null,
        config: AetherConfig = AetherConfig.DEFAULT
    ) {
        // Real SDK: validates licenseKey, applies config, and starts its
        // background workers here. This stand-in just flips a flag so
        // downstream UI can confirm bootstrap succeeded.
        bootstrapped = true
    }

    fun isBootstrapped(): Boolean = bootstrapped

    fun interceptor(): AetherSentinelInterceptor = sharedInterceptor

    fun setAppInForeground(inForeground: Boolean) {
        appInForeground = inForeground
    }

    fun isAppInForeground(): Boolean = appInForeground

    fun notifyServerConfirmedAttestationScore(score: Double) {
        lastAttestationScore = score.coerceIn(0.0, 1.0)
    }

    fun lastKnownAttestationScore(): Double? = lastAttestationScore
}
