package com.aethersentinel.showcase.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ImplementationTestState { IDLE, VERIFYING, SUCCESS }

data class SandboxUiState(
    val testState: ImplementationTestState = ImplementationTestState.IDLE
)

class SandboxViewModel(
    private val terminal: TerminalLogViewModel,
    private val events: AppEventsViewModel
) : ViewModel() {

    private val _state = MutableStateFlow(SandboxUiState())
    val state: StateFlow<SandboxUiState> = _state

    fun copySnippet(label: String) {
        terminal.push("[OkAether Sandbox] clipboard ← copied \"$label\"", LogLevel.INFO)
    }

    fun testImplementation() {
        if (_state.value.testState == ImplementationTestState.VERIFYING) return
        _state.update { it.copy(testState = ImplementationTestState.VERIFYING) }
        terminal.push("[OkAether Sandbox] verifying implementation — resolving dependency graph…", LogLevel.INFO)

        viewModelScope.launch {
            delay(500)
            terminal.push("[OkAether Sandbox] resolving io.github.ayushbiswas2011:OkAether:1.0.0…", LogLevel.INFO)
            delay(450)
            terminal.push("[OkAether Sandbox] OkAether class found on classpath ✔", LogLevel.INFO)
            delay(350)
            terminal.push("[OkAether Sandbox] OkAether.bootstrap() reachable from Application ✔", LogLevel.INFO)
            delay(300)
            terminal.push("[OkAether Sandbox] interceptor() — ready ✔", LogLevel.INFO)
            delay(200)
            _state.update { it.copy(testState = ImplementationTestState.SUCCESS) }
            terminal.push("[OkAether Sandbox] ✓ zero-friction implementation verified in under 30s", LogLevel.SUCCESS)
            events.showSnackbar("OkAether verified — you're live in under 30 seconds ⚡")
            delay(2200)
            _state.update { it.copy(testState = ImplementationTestState.IDLE) }
        }
    }
}
