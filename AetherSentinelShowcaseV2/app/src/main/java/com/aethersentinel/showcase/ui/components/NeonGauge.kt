package com.aethersentinel.showcase.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aethersentinel.showcase.ui.theme.BorderSlate
import com.aethersentinel.showcase.ui.theme.TextMuted
import com.aethersentinel.showcase.ui.theme.TextPrimary
import com.aethersentinel.showcase.ui.theme.TextSecondary

/**
 * Neon circular progress ring with a numeric readout in the center.
 * [fraction] must already be normalized to 0f..1f by the caller.
 */
@Composable
fun NeonCircularGauge(
    label: String,
    valueText: String,
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "gauge"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
            Canvas(modifier = Modifier.size(size)) {
                val strokeWidth = size.toPx() * 0.09f
                drawArc(
                    color = BorderSlate,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedFraction,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Text(
                text = valueText,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            color = TextMuted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

/**
 * Neon horizontal progress bar with a label/value header row.
 * [fraction] must already be normalized to 0f..1f by the caller.
 */
@Composable
fun NeonLinearGauge(
    label: String,
    valueText: String,
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "linear-gauge"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(valueText, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BorderSlate)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedFraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}
