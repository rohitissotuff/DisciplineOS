package com.disciplineos.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Black,
    secondary = TextSecondary,
    onSecondary = Black,
    background = Black,
    onBackground = TextPrimary,
    surface = NearBlack,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = TextSecondary,
    outline = BorderGray,
    error = ScoreRed,
)

@Composable
fun DisciplineOsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        typography = DisciplineTypography,
        content = content,
    )
}
