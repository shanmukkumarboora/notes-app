package com.example.notenow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Black,
    secondary = Lavender,
    tertiary = Lavender,
    background = Black,
    surface = Black,
    onPrimary = White,
    onSecondary = Black,
    onTertiary = Black,
    onBackground = White,
    onSurface = White
)

@Composable
fun NotenowTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}