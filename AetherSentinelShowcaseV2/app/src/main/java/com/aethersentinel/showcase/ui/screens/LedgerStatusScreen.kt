package com.aethersentinel.showcase.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aethersentinel.showcase.ui.components.GlassCard
import com.aethersentinel.showcase.ui.components.PulseIndicator
import com.aethersentinel.showcase.ui.theme.BorderSlate
import com.aethersentinel.showcase.ui.theme.DangerRed
import com.aethersentinel.showcase.ui.theme.EmeraldGreen
import com.aethersentinel.showcase.ui.theme.NeonCyan
import com.aethersentinel.showcase.ui.theme.TextMuted
import com.aethersentinel.showcase.ui.theme.TextPrimary
import com.aethersentinel.showcase.ui.theme.TextSecondary
import com.aethersentinel.showcase.viewmodel.LedgerViewModel

@Composable
fun LedgerStatusScreen(
    viewModel: LedgerViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "OkAether Ledger Status",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                "Real-time reconciliation queue, streamed directly from OkAether's ledgerDatabase FSM.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ── Connection heartbeat ──────────────────────────────────────────
        item {
            GlassCard(accent = EmeraldGreen) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulseIndicator(color = EmeraldGreen)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Ledger Database Connected",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "OkAether secure channel · encrypted at rest",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LedgerStatBlock(
                        label = "Queue Depth",
                        value = state.queueDepth.toString(),
                        color = NeonCyan
                    )
                    LedgerStatBlock(
                        label = "Reconciled",
                        value = state.reconciledTotal.toString(),
                        color = EmeraldGreen
                    )
                    LedgerStatBlock(
                        label = "Failed",
                        value = state.failedTotal.toString(),
                        color = DangerRed
                    )
                }
            }
        }

        // ── FSM call detail ──────────────────────────────────────────────
        item {
            GlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Storage, contentDescription = null, tint = TextSecondary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "findAllInStates(PENDING, IN_FLIGHT, SUSPENDED)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Queue depth is recomputed live on every reconciliation tick from " +
                        "OkAether.ledgerDatabase.findAllInStates(setOf(LedgerState.PENDING, " +
                        "LedgerState.IN_FLIGHT, LedgerState.SUSPENDED)).size",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Reconciliation interval: 2.6s",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                state.lastChurnId?.let { id ->
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Dns, contentDescription = null, tint = TextMuted, modifier = Modifier.padding(end = 6.dp))
                        Text(
                            "Last FSM transition: $id",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } ?: run {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Waiting for first OkAether reconciliation cycle…",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun LedgerStatBlock(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}
