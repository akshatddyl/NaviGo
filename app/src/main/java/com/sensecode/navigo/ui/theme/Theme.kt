package com.sensecode.navigo.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * High-contrast dark theme optimized for visually impaired users.
 * Background #121212, primary yellow #FFD600 for maximum contrast.
 */
private val HighContrastDarkScheme = darkColorScheme(
    primary = Color(0xFFFFD600),           // High-contrast yellow
    onPrimary = Color(0xFF1A1A00),
    primaryContainer = Color(0xFF3D3400),
    onPrimaryContainer = Color(0xFFFFE94D),
    secondary = Color(0xFF80CBC4),          // Teal accent
    onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFF005048),
    onSecondaryContainer = Color(0xFFA7F3EC),
    tertiary = Color(0xFFFFB74D),           // Orange accent
    onTertiary = Color(0xFF462B00),
    tertiaryContainer = Color(0xFF643F00),
    onTertiaryContainer = Color(0xFFFFDDB3),
    background = Color(0xFF121212),         // True dark background
    onBackground = Color(0xFFFFFFFF),       // Pure white text
    surface = Color(0xFF1E1E1E),            // Slightly lighter surface
    onSurface = Color(0xFFFFFFFF),          // Pure white text
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFE0E0E0),   // Light gray for secondary text
    error = Color(0xFFFF6B6B),              // Bright red error
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    outline = Color(0xFF888888),
    outlineVariant = Color(0xFF444444)
)

@Composable
fun NaviGoTheme(
    content: @Composable () -> Unit
) {
    // Always use dark high-contrast theme for accessibility
    val colorScheme = HighContrastDarkScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NaviGoTypography,
        content = content
    )
}
