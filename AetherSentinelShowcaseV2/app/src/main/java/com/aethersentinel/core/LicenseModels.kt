package com.aethersentinel.core

/*
 * STAND-IN DECLARATION — see AetherConfig.kt header comment for context.
 * Delete this file once the real .aar is wired in.
 */

enum class LicenseTier { FREE, STARTER, GROWTH, SCALE, ENTERPRISE }

enum class Feature {
    BASIC_INTERCEPTION,
    MOTION_ENTROPY,
    PLATFORM_ATTESTATION,
    FULL_SOULPRINT,
    LEDGER_RECONCILIATION,
    ORACLE_ADAPTIVE_THETA,
    CUSTOM_ORACLE_PARAMS,
    CUSTOM_SOULPRINT_WEIGHTS
}

data class LicenseStatus(
    val tier: LicenseTier,
    val enabledFeatures: Set<Feature>,
    val dailyRequestQuota: Long,
    val validatedAtMillis: Long,
    val expiresAtMillis: Long
)

/**
 * Exposed on the real SDK as `OkAether.licenseGatekeeper`.
 */
object LicenseGatekeeper {

    private var cachedStatus: LicenseStatus? = null

    fun currentStatus(): LicenseStatus {
        val now = System.currentTimeMillis()
        val status = cachedStatus ?: LicenseStatus(
            tier = LicenseTier.GROWTH,
            enabledFeatures = setOf(
                Feature.BASIC_INTERCEPTION,
                Feature.MOTION_ENTROPY,
                Feature.PLATFORM_ATTESTATION,
                Feature.LEDGER_RECONCILIATION,
                Feature.ORACLE_ADAPTIVE_THETA
            ),
            dailyRequestQuota = 250_000L,
            validatedAtMillis = now,
            expiresAtMillis = now + (30L * 24 * 60 * 60 * 1000)
        )
        cachedStatus = status
        return status
    }
}
