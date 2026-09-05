package com.android.trippoint.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.android.trippoint.core.designsystem.R

/**
 * TripPoint Brand Assets (Section 23.10).
 * References to logos, illustration styles, and photographic guidelines.
 */
@Immutable
data class TripPointBrand(
    val logo: Int = R.drawable.ic_app_logo,
    val logoSquare: Int = R.drawable.ic_app_logo, // Placeholder for specific variations
    val logoRound: Int = R.drawable.ic_app_logo,
    val illustrationCleanFriendly: Boolean = true,
    val photographyVibrant: Boolean = true
)

val LocalBrand = staticCompositionLocalOf { TripPointBrand() }
