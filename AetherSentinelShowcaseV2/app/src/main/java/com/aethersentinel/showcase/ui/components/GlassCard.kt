package com.aethersentinel.showcase.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aethersentinel.showcase.ui.theme.BorderSlate
import com.aethersentinel.showcase.ui.theme.CardSlate
import com.aethersentinel.showcase.ui.theme.PanelSlate

/**
 * A translucent, gradient-bordered "glass" panel used throughout the app
 * for feature cards, metric tiles, and code blocks. `accent` tints the
 * border glow to tie a card to its feature color (cyan for security,
 * blue for network, emerald for ledger, etc).
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accent: Color = BorderSlate,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(CardSlate, PanelSlate)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(accent.copy(alpha = 0.6f), BorderSlate, BorderSlate)
                ),
                shape = shape
            )
            .padding(contentPadding),
        content = content
    )
}
