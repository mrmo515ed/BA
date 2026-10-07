package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AnimeCrimson,
    onPrimary = AnimeTextPrimary,
    primaryContainer = AnimeCrimsonDark,
    onPrimaryContainer = AnimeTextPrimary,
    secondary = AnimeViolet,
    onSecondary = AnimeTextPrimary,
    secondaryContainer = AnimeVioletLight,
    onSecondaryContainer = AnimeTextPrimary,
    tertiary = AnimeCyan,
    onTertiary = AnimeBlackBg,
    background = AnimeBlackBg,
    onBackground = AnimeTextPrimary,
    surface = AnimeDarkSurface,
    onSurface = AnimeTextPrimary,
    surfaceVariant = AnimeCardSurface,
    onSurfaceVariant = AnimeTextSecondary,
    outline = AnimeBorder,
    outlineVariant = AnimeCardSurfaceHover
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Black Anime is a dark aesthetic app by design
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = AnimeBlackBg.toArgb()
                window.navigationBarColor = AnimeBlackBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
