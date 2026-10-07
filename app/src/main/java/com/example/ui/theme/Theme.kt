package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemePreset(val label: String, val primary: Color, val secondary: Color) {
    NEON_PURPLE("البنفسجي النيون", AnimeNeonPurple, AnimeCyan),
    CYBER_CYAN("السايبر سماوي", AnimeCyan, AnimeNeonPurple),
    SAKURA("ساكورا الربيع", SakuraPink, AnimeNeonPurple),
    RAMADAN("رمضان الذهبي", RamadanGold, AnimeSuccessGreen),
    CRIMSON_WAR("اللهب القرمزي", AnimeCrimson, AnimeGold)
}

@Composable
fun MyApplicationTheme(
    themePreset: AppThemePreset = AppThemePreset.NEON_PURPLE,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = themePreset.primary,
        onPrimary = Color.White,
        primaryContainer = themePreset.primary.copy(alpha = 0.3f),
        onPrimaryContainer = Color.White,
        secondary = themePreset.secondary,
        onSecondary = Color.Black,
        secondaryContainer = themePreset.secondary.copy(alpha = 0.25f),
        onSecondaryContainer = Color.White,
        tertiary = AnimeGold,
        onTertiary = Color.Black,
        background = AnimeBlackBg,
        onBackground = AnimeTextPrimary,
        surface = AnimeDarkSurface,
        onSurface = AnimeTextPrimary,
        surfaceVariant = AnimeCardSurface,
        onSurfaceVariant = AnimeTextSecondary,
        outline = AnimeBorder,
        outlineVariant = AnimeCardSurfaceHover
    )

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
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
