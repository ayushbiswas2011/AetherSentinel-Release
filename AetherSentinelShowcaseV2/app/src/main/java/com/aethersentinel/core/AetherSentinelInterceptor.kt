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

data class InterceptedCall(
    val method: String,
    val host: String,
    val path: String,
    val statusCode: Int,
    val latencyMillis: Int,
    val flaggedAsAnomalous: Boolean
)

fun interface InterceptorListener {
    fun onCall(call: InterceptedCall)
}

/**
 * Returned by `OkAether.interceptor()`. Wraps the app's outgoing
 * traffic to detect ghost-retry storms and scraping patterns.
 */
class AetherSentinelInterceptor internal constructor() {

    var isAttached: Boolean = false
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    private val hosts = listOf(
        "api.partner-ads.net", "telemetry.mobiscan.io", "cdn.assetflow.com",
        "auth.internal-svc.com", "metrics.okaether.dev", "graph.socialtrack.io"
    )
    private val paths = listOf(
        "/v1/ping", "/collect", "/session/refresh", "/ads/impression",
        "/v2/retry-queue", "/telemetry/batch", "/sync/delta"
    )
    private val methods = listOf("GET", "POST", "PUT")

    fun attach(listener: InterceptorListener) {
        detach()
        isAttached = true
        job = scope.launch {
            while (isActive) {
                delay(Random.nextLong(350, 900))
                val call = generateCall()
                withContext(Dispatchers.Main) { listener.onCall(call) }
            }
        }
    }

    fun detach() {
        isAttached = false
        job?.cancel()
        job = null
    }

    private fun generateCall(): InterceptedCall {
        val flagged = Random.nextInt(100) < 22
        return InterceptedCall(
            method = methods.random(),
            host = hosts.random(),
            path = paths.random(),
            statusCode = if (flagged) listOf(429, 403, 503).random() else listOf(200, 200, 201, 204).random(),
            latencyMillis = Random.nextInt(18, 340),
            flaggedAsAnomalous = flagged
        )
    }
}
