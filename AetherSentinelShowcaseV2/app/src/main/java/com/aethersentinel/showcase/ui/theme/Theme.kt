package com.aethersentinel.showcase.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val OkAetherDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = VoidBlack,
    secondary = EmeraldGreen,
    onSecondary = VoidBlack,
    tertiary = ElectricBlue,
    onTertiary = VoidBlack,
    background = CanvasSlate,
    onBackground = TextPrimary,
    surface = PanelSlate,
    onSurface = TextPrimary,
    surfaceVariant = CardSlate,
    onSurfaceVariant = TextSecondary,
    outline = BorderSlate,
    error = DangerRed,
    onError = TextPrimary
)

/**
 * Root Compose theme for the OkAether Showcase app.
 * Always uses the dark cyberpunk palette regardless of system preference —
 * this is a deliberate design choice that reinforces the brand identity.
 */
@Composable
fun OkAetherShowcaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = OkAetherDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CanvasSlate.toArgb()
            window.navigationBarColor = CanvasSlate.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AetherTypography,
        content = content
    )
}
