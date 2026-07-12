package com.aethersentinel.showcase.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pulsing "heartbeat" node used for the ledger connection status and
 * other persistent live-connection indicators.
 */
@Composable
fun PulseIndicator(
    color: Color,
    modifier: Modifier = Modifier,
    dotSize: Dp = 12.dp
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse-scale"
    )
    val alpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse-alpha"
    )

    Box(modifier = modifier.size(dotSize * 3), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .scale(scale)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha))
        )
        Box(
            modifier = Modifier
                .size(dotSize)
                .clip(CircleShape)
                .background(color)
        )
    }
}
