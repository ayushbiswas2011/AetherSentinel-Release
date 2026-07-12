package com.aethersentinel.showcase.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aethersentinel.showcase.data.AetherRepository

/**
 * Minimal manual DI factory — no Hilt/Koin dependency needed for a
 * showcase app this size. Wires the shared repository/terminal/event-bus
 * ViewModels into every screen-level ViewModel that needs them.
 */
class AetherViewModelFactory(
    private val repository: AetherRepository,
    private val terminal: TerminalLogViewModel,
    private val events: AppEventsViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            PlaygroundViewModel::class.java ->
                PlaygroundViewModel(repository, terminal) as T
            SandboxViewModel::class.java ->
                SandboxViewModel(terminal, events) as T
            LedgerViewModel::class.java ->
                LedgerViewModel(repository, terminal) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
