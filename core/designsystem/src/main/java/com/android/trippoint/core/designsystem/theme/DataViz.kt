package com.android.trippoint.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * TripPoint Data Visualization Palette (Section 28.10).
 */
@Immutable
data class TripPointDataViz(
    val color1: Color = Color(0xFF7C5CFF),
    val color2: Color = Color(0xFF0EA5E9),
    val color3: Color = Color(0xFF22C55E),
    val color4: Color = Color(0xFFF59E0B),
    val color5: Color = Color(0xFFEF4444),
    val color6: Color = Color(0xFF8B5CF6)
)

val LocalDataViz = staticCompositionLocalOf { TripPointDataViz() }
