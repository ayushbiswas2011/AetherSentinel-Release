package com.aethersentinel.showcase.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.aethersentinel.showcase.GRADLE_MAVEN_SNIPPET
import com.aethersentinel.showcase.GRADLE_MIRROR_SNIPPET
import com.aethersentinel.showcase.INIT_SNIPPET
import com.aethersentinel.showcase.ui.components.CodeBlock
import com.aethersentinel.showcase.ui.components.GlassCard
import com.aethersentinel.showcase.ui.theme.EmeraldGreen
import com.aethersentinel.showcase.ui.theme.NeonCyan
import com.aethersentinel.showcase.ui.theme.TextPrimary
import com.aethersentinel.showcase.ui.theme.TextSecondary
import com.aethersentinel.showcase.viewmodel.ImplementationTestState
import com.aethersentinel.showcase.viewmodel.SandboxViewModel

@Composable
fun DeveloperSandboxScreen(
    viewModel: SandboxViewModel,
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
                "Zero-Friction Sandbox",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                "From zero to fully-armed OkAether protection in under 30 seconds. Two ways to install.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Option A — Maven Central
        item {
            GlassCard(accent = NeonCyan) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Cloud, contentDescription = null, tint = NeonCyan)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Option A — Maven Central (Recommended)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(12.dp))
                CodeBlock(
                    label = "app/build.gradle.kts",
                    code = GRADLE_MAVEN_SNIPPET,
                    onCopied = { viewModel.copySnippet("Maven Central dependency") }
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Latest: io.github.ayushbiswas2011:OkAether:1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonCyan
                )
            }
        }

        // Option B — 1-Click init.gradle Mirror
        item {
            GlassCard(accent = EmeraldGreen) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Link, contentDescription = null, tint = EmeraldGreen)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Option B — 1-Click Dynamic Mirror",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Always resolves the latest OkAether release automatically.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                CodeBlock(
                    label = "build.gradle",
                    code = GRADLE_MIRROR_SNIPPET,
                    onCopied = { viewModel.copySnippet("init.gradle 1-click mirror") }
                )
            }
        }

        // Initialize once
        item {
            GlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Bolt, contentDescription = null, tint = NeonCyan)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Initialize once — that's it",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(12.dp))
                CodeBlock(
                    label = "MyApp.kt",
                    code = INIT_SNIPPET,
                    onCopied = { viewModel.copySnippet("OkAether.bootstrap() init block") }
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "That's the entire integration surface. OkAether now silently intercepts flaky retries, " +
                        "attests device integrity, and reconciles your ledger — all in the background.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Test Implementation toggle
        item {
            GlassCard(accent = EmeraldGreen) {
                Text(
                    "Test Implementation",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Simulates a fresh dependency resolution and OkAether.bootstrap() call end-to-end.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { viewModel.testImplementation() },
                    enabled = state.testState != ImplementationTestState.VERIFYING,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (state.testState) {
                        ImplementationTestState.IDLE -> Text("Test Implementation in 30s")
                        ImplementationTestState.VERIFYING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Verifying OkAether…")
                        }
                        ImplementationTestState.SUCCESS -> {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.Black)
                            Spacer(Modifier.width(8.dp))
                            Text("OkAether Verified!")
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}
