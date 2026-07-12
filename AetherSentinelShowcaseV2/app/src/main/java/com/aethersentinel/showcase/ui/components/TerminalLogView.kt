package com.aethersentinel.showcase.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aethersentinel.showcase.ui.theme.BorderSlate
import com.aethersentinel.showcase.ui.theme.DangerRed
import com.aethersentinel.showcase.ui.theme.EmeraldGreen
import com.aethersentinel.showcase.ui.theme.TerminalAmber
import com.aethersentinel.showcase.ui.theme.TerminalBg
import com.aethersentinel.showcase.ui.theme.TerminalCyan
import com.aethersentinel.showcase.ui.theme.TextMuted
import com.aethersentinel.showcase.viewmodel.LogLevel
import com.aethersentinel.showcase.viewmodel.LogLine

@Composable
fun TerminalLogView(
    lines: List<LogLine>,
    modifier: Modifier = Modifier,
    heightDp: Int = 190
) {
    val listState = rememberLazyListState()
    val haptics = LocalHapticFeedback.current
    var previousSize by remember { mutableIntStateOf(lines.size) }

    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) {
            listState.animateScrollToItem(lines.size - 1)
        }
        if (lines.size > previousSize) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        previousSize = lines.size
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(TerminalBg)
            .border(
                width = 1.dp,
                color = BorderSlate,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
            )
    ) {
        // Header bar mimicking a terminal window chrome
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Dot(DangerRed)
            Dot(TerminalAmber)
            Dot(EmeraldGreen)
            Icon(
                imageVector = Icons.Filled.Terminal,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(14.dp)
            )
            Text(
                text = "okaether://live-console",
                color = TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .padding(horizontal = 14.dp)
        ) {
            itemsIndexed(lines, key = { _, item -> item.id }) { _, line ->
                TerminalLine(line)
            }
            item { Box(Modifier.height(6.dp)) }
        }
    }
}

@Composable
private fun TerminalLine(line: LogLine) {
    val color = when (line.level) {
        LogLevel.INFO -> TerminalCyan
        LogLevel.SUCCESS -> EmeraldGreen
        LogLevel.WARNING -> TerminalAmber
        LogLevel.ERROR -> DangerRed
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = line.timestamp,
            color = TextMuted,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "  ${line.text}",
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (line.level == LogLevel.SUCCESS || line.level == LogLevel.ERROR) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(color)
    )
}
