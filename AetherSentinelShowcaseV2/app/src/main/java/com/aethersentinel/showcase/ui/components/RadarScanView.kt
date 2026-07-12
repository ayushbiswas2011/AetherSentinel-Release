package com.aethersentinel.showcase.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aethersentinel.showcase.ui.theme.DangerRed
import com.aethersentinel.showcase.ui.theme.EmeraldGreen
import com.aethersentinel.showcase.ui.theme.NeonCyan
import com.aethersentinel.showcase.viewmodel.ScanState
import kotlin.math.min

@Composable
fun RadarScanView(
    scanState: ScanState,
    modifier: Modifier = Modifier,
    size: Dp = 132.dp
) {
    val transition = rememberInfiniteTransition(label = "radar")
    val sweepAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val radius = min(this.size.width, this.size.height) / 2f
            // concentric rings
            for (i in 1..3) {
                drawCircle(
                    color = NeonCyan.copy(alpha = 0.18f),
                    radius = radius * i / 3f,
                    style = Stroke(width = 1.5f)
                )
            }
            if (scanState == ScanState.SCANNING) {
                rotate(degrees = sweepAngle) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                NeonCyan.copy(alpha = 0.05f),
                                NeonCyan.copy(alpha = 0.55f)
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 90f,
                        useCenter = true,
                        topLeft = Offset(this.size.width / 2f - radius, this.size.height / 2f - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                }
            }
        }

        val (icon, tint) = when (scanState) {
            ScanState.IDLE -> Icons.Filled.Security to NeonCyan.copy(alpha = 0.7f)
            ScanState.SCANNING -> Icons.Filled.Security to NeonCyan
            ScanState.PASSED -> Icons.Filled.CheckCircle to EmeraldGreen
            ScanState.FAILED -> Icons.Filled.Error to DangerRed
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size / 2.6f)
        )
    }
}
