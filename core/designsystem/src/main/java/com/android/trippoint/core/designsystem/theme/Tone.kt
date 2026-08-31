package com.android.trippoint.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * TripPoint Emotions & Tone System (Section 26.9).
 * Defines the semantic tone and emotional states for the brand.
 */
@Immutable
data class TripPointTone(
    val currentTone: BrandTone = BrandTone.Friendly,
    val primaryEmotion: UserEmotion = UserEmotion.Calm
)

enum class BrandTone {
    Friendly,
    Trustworthy,
    Inspiring,
    Optimistic,
    Warm,
    Adventurous
}

enum class UserEmotion {
    Excited,
    Calm,
    Confident,
    Curious,
    Celebratory
}

val LocalTone = staticCompositionLocalOf { TripPointTone() }
