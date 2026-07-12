package com.aethersentinel.showcase.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.aethersentinel.showcase.ui.theme.EmeraldGreen
import com.aethersentinel.showcase.ui.theme.NeonCyan
import com.aethersentinel.showcase.ui.theme.TerminalBg
import com.aethersentinel.showcase.ui.theme.TextMuted
import com.aethersentinel.showcase.ui.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CodeBlock(
    label: String,
    code: String,
    modifier: Modifier = Modifier,
    onCopied: (() -> Unit)? = null
) {
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var showConfirm by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(TerminalBg)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
                IconButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(code))
                        showConfirm = true
                        onCopied?.invoke()
                        scope.launch {
                            delay(1600)
                            showConfirm = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Copy code",
                        tint = NeonCyan
                    )
                }
            }
            Text(
                text = code,
                color = TextPrimary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        AnimatedVisibility(
            visible = showConfirm,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp),
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 })
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldGreen.copy(alpha = 0.16f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = EmeraldGreen
                )
                Text(
                    text = "Copied to clipboard",
                    color = EmeraldGreen,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
