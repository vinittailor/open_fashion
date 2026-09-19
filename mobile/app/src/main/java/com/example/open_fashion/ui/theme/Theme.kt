package com.example.open_fashion.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- Luxury Light Color Scheme (60/30/10 Rule) ---
private val LightColorScheme = lightColorScheme(
    primary = PrimaryCharcoal,
    onPrimary = SurfaceLight,
    primaryContainer = PrimaryCharcoalLight,
    onPrimaryContainer = SurfaceLight,
    secondary = AccentGold,
    onSecondary = PrimaryCharcoal,
    secondaryContainer = AccentGoldLight,
    onSecondaryContainer = PrimaryCharcoal,
    tertiary = AccentGoldDark,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = StatusError,
    onError = SurfaceLight
)

// --- Luxury Dark Color Scheme (OLED Black + Champagne Accents) ---
private val DarkColorScheme = darkColorScheme(
    primary = AccentGold,
    onPrimary = PrimaryCharcoal,
    primaryContainer = PrimaryCharcoalLight,
    onPrimaryContainer = TextPrimaryDark,
    secondary = AccentGoldLight,
    onSecondary = PrimaryCharcoal,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = AccentGoldDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    error = StatusError,
    onError = TextPrimaryDark
)

@Composable
fun Open_fashionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce our branded luxury scheme rather than arbitrary dynamic system wallpaper colors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}