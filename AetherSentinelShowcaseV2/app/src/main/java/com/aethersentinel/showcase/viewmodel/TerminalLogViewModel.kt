package com.aethersentinel.showcase.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Locale

@Immutable
data class LogLine(
    val id: Long,
    val timestamp: String,
    val text: String,
    val level: LogLevel
)

enum class LogLevel { INFO, SUCCESS, WARNING, ERROR }

/**
 * Shared across every tab so every OkAether SDK interaction — regardless
 * of which screen triggered it — streams into a single live console at
 * the base of the layout.
 */
class TerminalLogViewModel : ViewModel() {

    private val timeFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private var counter = 0L

    private val _lines = MutableStateFlow<List<LogLine>>(
        listOf(bootLine("[OkAether Engine] okaether-core attached — live console online"))
    )
    val lines: StateFlow<List<LogLine>> = _lines

    private fun bootLine(text: String) = LogLine(
        id = counter++,
        timestamp = timeFormatter.format(System.currentTimeMillis()),
        text = text,
        level = LogLevel.INFO
    )

    fun push(text: String, level: LogLevel = LogLevel.INFO) {
        _lines.update { current ->
            val next = current + LogLine(
                id = counter++,
                timestamp = timeFormatter.format(System.currentTimeMillis()),
                text = text,
                level = level
            )
            // Keep the log bounded so a long demo session never OOMs.
            if (next.size > 400) next.takeLast(400) else next
        }
    }

    fun clear() {
        _lines.update { listOf(bootLine("[OkAether Engine] terminal cleared")) }
    }
}
