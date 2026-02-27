package com.beside.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 温馨的粉色系配色 💕
val PinkPrimary = Color(0xFFE91E63)
val PinkLight = Color(0xFFF8BBD0)
val PinkDark = Color(0xFFC2185B)
val WarmBackground = Color(0xFFFFF8F9)
val WarmSurface = Color(0xFFFFFBFC)
val HeartRed = Color(0xFFE53935)
val SoftPurple = Color(0xFF9C27B0)
val CreamWhite = Color(0xFFFFF3E0)

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PinkLight,
    onPrimaryContainer = PinkDark,
    secondary = SoftPurple,
    background = WarmBackground,
    surface = WarmSurface,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

private val DarkColorScheme = darkColorScheme(
    primary = PinkLight,
    onPrimary = PinkDark,
    primaryContainer = PinkDark,
    onPrimaryContainer = PinkLight,
    secondary = Color(0xFFCE93D8),
    background = Color(0xFF1A1A2E),
    surface = Color(0xFF16213E),
)

@Composable
fun BeSideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
