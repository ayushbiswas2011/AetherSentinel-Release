package com.aethersentinel.showcase.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethersentinel.core.LedgerState
import com.aethersentinel.showcase.data.AetherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LedgerUiState(
    val connected: Boolean = true,
    val queueDepth: Int = 0,
    val reconciledTotal: Int = 0,
    val failedTotal: Int = 0,
    val lastChurnId: String? = null
)

class LedgerViewModel(
    private val repository: AetherRepository,
    private val terminal: TerminalLogViewModel
) : ViewModel() {

    private val _state = MutableStateFlow(LedgerUiState())
    val state: StateFlow<LedgerUiState> = _state

    init {
        refreshQueueDepth()
        startChurnLoop()
    }

    private fun refreshQueueDepth() {
        val depth = repository.activeQueueDepth()
        val all = repository.allLedgerEntries()
        _state.update {
            it.copy(
                queueDepth = depth,
                reconciledTotal = all.count { e -> e.state == LedgerState.RECONCILED },
                failedTotal = all.count { e -> e.state == LedgerState.FAILED }
            )
        }
    }

    private fun startChurnLoop() {
        terminal.push("[OkAether Ledger] reconciliation worker scheduled — interval 2.6s", LogLevel.INFO)
        viewModelScope.launch {
            while (true) {
                delay(2_600L)
                val churned = repository.churnLedger()
                refreshQueueDepth()
                _state.update { it.copy(lastChurnId = churned.id) }
                val level = when (churned.state) {
                    LedgerState.RECONCILED -> LogLevel.SUCCESS
                    LedgerState.FAILED, LedgerState.SUSPENDED -> LogLevel.WARNING
                    else -> LogLevel.INFO
                }
                terminal.push(
                    "[OkAether Ledger] ${churned.id} → ${churned.state} · queue depth=${_state.value.queueDepth}",
                    level
                )
            }
        }
    }
}
