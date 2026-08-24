package com.sentinel.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SentinelRed,
    onPrimary = Color.White,
    primaryContainer = SentinelRedDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF8E8E93),
    onSecondary = Color.White,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = SeverityHigh
)

private val LightColorScheme = lightColorScheme(
    primary = SentinelRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFFB71C1C),
    secondary = Color(0xFF8E8E93),
    onSecondary = Color.Black,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1E1E1E),
    surface = Color.White,
    onSurface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF757575),
    outline = Color(0xFFBDBDBD),
    error = SeverityHigh
)

@Composable
fun SentinelTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SentinelTypography,
        content = content
    )
}
