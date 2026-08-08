package com.disciplineos.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF050505)
val NearBlack = Color(0xFF0E0E0E)
val SurfaceGray = Color(0xFF151515)
val SurfaceElevated = Color(0xFF1A1A1A)
val BorderGray = Color(0xFF2A2A2A)
val Accent = Color(0xFFE8E2D6)
val AccentDim = Color(0x33E8E2D6)
val TextPrimary = Color(0xFFF7F7F5)
val TextSecondary = Color(0xFFA8A8A0)
val TextMuted = Color(0xFF666660)

val ScoreGreen = Color(0xFF3DDC84)
val ScoreYellow = Color(0xFFF0C419)
val ScoreRed = Color(0xFFFF5A4D)

val AtmosphereTop = Color(0xFF12100E)
val AtmosphereBottom = Color(0xFF050505)

fun scoreColor(score: Int): Color = when {
    score >= 80 -> ScoreGreen
    score >= 50 -> ScoreYellow
    else -> ScoreRed
}

fun screenAtmosphere(): Brush = Brush.verticalGradient(
    colors = listOf(AtmosphereTop, Black, AtmosphereBottom),
)
