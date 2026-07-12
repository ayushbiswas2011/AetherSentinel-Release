package com.aethersentinel.showcase.ui.screens

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aethersentinel.showcase.GITHUB_REPO_URL
import com.aethersentinel.showcase.ui.components.GlassCard
import com.aethersentinel.showcase.ui.components.NeonCircularGauge
import com.aethersentinel.showcase.ui.components.NeonLinearGauge
import com.aethersentinel.showcase.ui.components.RadarScanView
import com.aethersentinel.showcase.ui.theme.BorderSlate
import com.aethersentinel.showcase.ui.theme.CanvasSlate
import com.aethersentinel.showcase.ui.theme.CardSlate
import com.aethersentinel.showcase.ui.theme.DangerRed
import com.aethersentinel.showcase.ui.theme.ElectricBlue
import com.aethersentinel.showcase.ui.theme.EmeraldGreen
import com.aethersentinel.showcase.ui.theme.NeonCyan
import com.aethersentinel.showcase.ui.theme.TextMuted
import com.aethersentinel.showcase.ui.theme.TextPrimary
import com.aethersentinel.showcase.ui.theme.TextSecondary
import com.aethersentinel.showcase.ui.theme.VioletGlow
import com.aethersentinel.showcase.ui.theme.WarningAmber
import com.aethersentinel.showcase.viewmodel.PlaygroundViewModel
import com.aethersentinel.showcase.viewmodel.ScanState

@Composable
fun PlaygroundScreen(
    viewModel: PlaygroundViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "OkAether Playground",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                text = "Live-fire the OkAether SDK. Every tap calls a real SDK entry point.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ── Cloud Cost Saved Counter ──────────────────────────────────────
        item {
            CloudCostSavedCard(savedCents = state.cloudCostSavedCents)
        }

        // ── Security Check / License Attestation ──────────────────────────
        item {
            GlassCard(accent = NeonCyan) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Radar, contentDescription = null, tint = NeonCyan)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Trigger Security Check",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Calls OkAether.notifyServerConfirmedAttestationScore(1.0) then reads back licenseGatekeeper.currentStatus().",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RadarScanView(scanState = state.scanState)
                    Column(horizontalAlignment = Alignment.End) {
                        val statusText = when (state.scanState) {
                            ScanState.IDLE -> "Idle"
                            ScanState.SCANNING -> "Scanning…"
                            ScanState.PASSED -> "Passed ✔"
                            ScanState.FAILED -> "Failed ✘"
                        }
                        val statusColor = when (state.scanState) {
                            ScanState.PASSED -> EmeraldGreen
                            ScanState.FAILED -> DangerRed
                            else -> TextMuted
                        }
                        Text(statusText, color = statusColor, fontWeight = FontWeight.SemiBold)
                        state.licenseTier?.let {
                            Text("Tier: $it", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                        state.dailyRequestQuota?.let {
                            Text(
                                "Quota: $it/day",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.triggerSecurityCheck() },
                            enabled = state.scanState != ScanState.SCANNING,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(if (state.scanState == ScanState.SCANNING) "Scanning…" else "Run Check")
                        }
                    }
                }
                if (state.enabledFeatures.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "enabledFeatures: ${state.enabledFeatures.joinToString(", ") { it.name }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = VioletGlow
                    )
                }
            }
        }

        // ── Live Radio & Network Telemetry Gauges ─────────────────────────
        item {
            val telemetry = state.telemetry
            GlassCard(accent = ElectricBlue) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (telemetry?.isWifi == true) Icons.Filled.Wifi else Icons.Filled.CellTower,
                        contentDescription = null,
                        tint = ElectricBlue
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Live Radio & Network Telemetry",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    if (telemetry == null) "Waiting for first OkAether PhyHarvester snapshot…"
                    else "${if (telemetry.isWifi) "Wi-Fi" else "Cellular"} · ${if (telemetry.isMetered) "Metered" else "Unmetered"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(16.dp))

                if (telemetry != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (!telemetry.isWifi) {
                            NeonCircularGauge(
                                label = "RSRP",
                                valueText = telemetry.rsrpDbm?.let { "${it}" } ?: "–",
                                fraction = normalizeDbm(telemetry.rsrpDbm, -140, -44),
                                color = NeonCyan
                            )
                            NeonCircularGauge(
                                label = "RSRQ",
                                valueText = telemetry.rsrqDb?.let { "${it}" } ?: "–",
                                fraction = normalizeDbm(telemetry.rsrqDb, -20, -3),
                                color = ElectricBlue
                            )
                            NeonCircularGauge(
                                label = "SINR",
                                valueText = telemetry.sinrDb?.let { "${it}" } ?: "–",
                                fraction = normalizeDbm(telemetry.sinrDb, -20, 30),
                                color = EmeraldGreen
                            )
                        } else {
                            NeonCircularGauge(
                                label = "SIGNAL",
                                valueText = telemetry.genericSignalDbm?.let { "${it}" } ?: "–",
                                fraction = normalizeDbm(telemetry.genericSignalDbm, -100, -30),
                                color = NeonCyan
                            )
                            NeonCircularGauge(
                                label = "DOWN",
                                valueText = telemetry.downstreamBandwidthKbps?.let { "${it / 1000}M" } ?: "–",
                                fraction = ((telemetry.downstreamBandwidthKbps ?: 0) / 180_000f).coerceIn(0f, 1f),
                                color = ElectricBlue
                            )
                            NeonCircularGauge(
                                label = "UP",
                                valueText = telemetry.upstreamBandwidthKbps?.let { "${it / 1000}M" } ?: "–",
                                fraction = ((telemetry.upstreamBandwidthKbps ?: 0) / 40_000f).coerceIn(0f, 1f),
                                color = EmeraldGreen
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        NeonLinearGauge(
                            label = "Latency (RTT)",
                            valueText = telemetry.observedRttMillis?.let { "${it}ms" } ?: "–",
                            fraction = 1f - ((telemetry.observedRttMillis ?: 0L) / 300f).coerceIn(0f, 1f),
                            color = if ((telemetry.observedRttMillis ?: 0L) < 90) EmeraldGreen else DangerRed
                        )
                        NeonLinearGauge(
                            label = "Throughput",
                            valueText = telemetry.throughputBytesPerSecond?.let {
                                "${"%.1f".format(it / 1_048_576.0)} MB/s"
                            } ?: "–",
                            fraction = ((telemetry.throughputBytesPerSecond ?: 0.0) / 4_500_000.0)
                                .toFloat().coerceIn(0f, 1f),
                            color = ElectricBlue
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                OutlinedButton(
                    onClick = {
                        val message = viewModel.buildShareableScorecard(GITHUB_REPO_URL)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, message)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share your OkAether scorecard"))
                    },
                    colors = OutlinedButtonDefaults.outlinedButtonColors(contentColor = EmeraldGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.IosShare, contentDescription = null, tint = EmeraldGreen)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Shareable Scorecard")
                }
            }
        }

        // ── Network Interceptor Toggle ────────────────────────────────────
        item {
            GlassCard(accent = ElectricBlue) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (state.interceptorActive) Icons.Filled.Shield else Icons.Filled.WifiOff,
                            contentDescription = null,
                            tint = if (state.interceptorActive) NeonCyan else TextMuted
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Network Interceptor",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Switch(
                        checked = state.interceptorActive,
                        onCheckedChange = { viewModel.toggleInterceptor() },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = NeonCyan,
                            checkedThumbColor = Color.Black
                        )
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "OkAether.interceptor().attach() hooks outgoing traffic and suppresses flaky retry storms in real time.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    MetricPill(
                        label = "Requests",
                        value = state.trafficTotalCount.toString(),
                        color = ElectricBlue
                    )
                    MetricPill(
                        label = "Blocked",
                        value = state.trafficFlaggedCount.toString(),
                        color = DangerRed
                    )
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ── Cloud Cost Saved Counter Card ─────────────────────────────────────────────
@Composable
private fun CloudCostSavedCard(savedCents: Int) {
    val dollars = savedCents / 100.0
    val dollarsStr = "$%.2f".format(dollars)

    // Pulse the glow slightly more with each update
    val glowAlpha by animateFloatAsState(
        targetValue = if (savedCents > 0) 0.35f else 0.15f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "glow"
    )
    val accentColor = EmeraldGreen
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0D1F18),
                        Color(0xFF0A1A14),
                        Color(0xFF091410)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.7f),
                        accentColor.copy(alpha = 0.2f),
                        BorderSlate
                    )
                ),
                shape = shape
            )
            .padding(20.dp)
    ) {
        // Subtle background glow blob
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldGreen.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(60.dp)
                )
        )

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.AttachMoney,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Cloud Cost Saved Today",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Saved $dollarsStr in Cloud Bills",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = EmeraldGreen
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "OkAether is actively suppressing flaky API retry storms that inflate your server costs.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

private fun normalizeDbm(value: Int?, min: Int, max: Int): Float {
    if (value == null) return 0f
    return ((value - min).toFloat() / (max - min).toFloat()).coerceIn(0f, 1f)
}

@Composable
private fun MetricPill(label: String, value: String, color: Color) {
    Column {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}
