package com.android.trippoint.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.android.trippoint.core.designsystem.R

/**
 * TripPoint Patterns & Backgrounds System (Section 26.8).
 * Provides references to texture overlays and branded patterns.
 */
@Immutable
data class TripPointPatterns(
    val map: Int = R.drawable.ic_splash_illustrator, // Use logo/splash as fallback pattern
    val doodle: Int = R.drawable.ic_splash_illustrator,
    val topography: Int = R.drawable.ic_splash_illustrator
)

val LocalPatterns = staticCompositionLocalOf { TripPointPatterns() }
