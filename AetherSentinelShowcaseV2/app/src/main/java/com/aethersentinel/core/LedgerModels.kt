package com.aethersentinel.core

import kotlin.random.Random

/*
 * STAND-IN DECLARATION — see AetherConfig.kt header comment for context.
 * Delete this file once the real .aar is wired in.
 */

enum class LedgerState { PENDING, IN_FLIGHT, SUSPENDED, ACKNOWLEDGED, RECONCILED, FAILED }

data class LedgerEntry(
    val id: String,
    val state: LedgerState,
    val createdAtMillis: Long,
    val payloadSizeBytes: Int
)

/**
 * Exposed on the real SDK as `OkAether.ledgerDatabase`. Backs the
 * append-only reconciliation queue that the SDK's background worker
 * drains against the server ledger.
 */
object LedgerDatabase {

    private val entries = mutableListOf<LedgerEntry>()
    private val lock = Any()
    private var counter = 0

    init {
        synchronized(lock) {
            repeat(6) { entries += randomEntry() }
        }
    }

    fun findAllInStates(states: Set<LedgerState>): List<LedgerEntry> = synchronized(lock) {
        entries.filter { it.state in states }.toList()
    }

    /**
     * Simulates ongoing queue churn (new entries arriving, in-flight
     * entries reconciling, etc). The real SDK does this internally via
     * its background sync worker; this showcase drives it on a timer
     * from [com.aethersentinel.showcase.viewmodel.LedgerViewModel].
     */
    fun churn(): LedgerEntry = synchronized(lock) {
        // Advance a random existing entry toward a terminal state.
        val advanceable = entries.filter {
            it.state == LedgerState.PENDING || it.state == LedgerState.IN_FLIGHT || it.state == LedgerState.SUSPENDED
        }
        if (advanceable.isNotEmpty() && Random.nextInt(100) < 70) {
            val target = advanceable.random()
            val index = entries.indexOf(target)
            val nextState = when (target.state) {
                LedgerState.PENDING -> LedgerState.IN_FLIGHT
                LedgerState.IN_FLIGHT -> if (Random.nextInt(100) < 88) LedgerState.ACKNOWLEDGED else LedgerState.SUSPENDED
                LedgerState.SUSPENDED -> LedgerState.IN_FLIGHT
                else -> target.state
            }
            entries[index] = target.copy(state = nextState)
        }
        // Occasionally mark an ACKNOWLEDGED entry as fully RECONCILED.
        entries.indexOfFirst { it.state == LedgerState.ACKNOWLEDGED }.takeIf { it >= 0 && Random.nextInt(100) < 40 }
            ?.let { idx -> entries[idx] = entries[idx].copy(state = LedgerState.RECONCILED) }

        val newEntry = randomEntry()
        entries += newEntry
        if (entries.size > 40) entries.removeAt(0)
        newEntry
    }

    private fun randomEntry(): LedgerEntry {
        counter += 1
        return LedgerEntry(
            id = "ldg_${counter.toString().padStart(5, '0')}",
            state = listOf(LedgerState.PENDING, LedgerState.IN_FLIGHT).random(),
            createdAtMillis = System.currentTimeMillis(),
            payloadSizeBytes = Random.nextInt(180, 4_200)
        )
    }
}
