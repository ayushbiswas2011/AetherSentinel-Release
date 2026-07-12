package com.aethersentinel.core

/*
 * STAND-IN DECLARATION
 * ---------------------------------------------------------------------
 * This file mirrors the public API of the real `okaether-core.aar`
 * artifact so this showcase project compiles and demonstrates full,
 * accurate integration before that dependency is wired in. When you add
 * the real SDK (Maven Central coordinate or local .aar — see
 * app/build.gradle.kts and app/libs/README.md), delete this file.
 */

/**
 * Runtime configuration passed to [OkAether.bootstrap].
 */
data class AetherConfig(
    val enableMotionEntropy: Boolean = true,
    val enablePlatformAttestation: Boolean = true,
    val syncIntervalMillis: Long = 8_000L,
    val verboseLogging: Boolean = false
) {
    companion object {
        val DEFAULT = AetherConfig()
    }
}
