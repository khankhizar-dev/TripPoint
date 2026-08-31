package com.android.trippoint.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * TripPoint Motion System (Updated to Module 28).
 */
@Immutable
data class TripPointMotion(
    val durationInstant: Int = 0,
    val durationFast: Int = 150,
    val durationNormal: Int = 250,
    val durationSlow: Int = 350,
    val durationExtraSlow: Int = 500,
    
    val easingStandard: Easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f),
    val easingEmphasized: Easing = CubicBezierEasing(0.25f, 1f, 0.5f, 1f),
    val easingDecelerate: Easing = CubicBezierEasing(0f, 0f, 0.2f, 1f),
    val easingLinear: Easing = LinearEasing,

    // Legacy durations
    val durationShort: Int = durationFast,
    val durationMedium: Int = durationNormal,
    val durationLong: Int = durationSlow
) {
    fun <T> fastTween() = tween<T>(durationMillis = durationFast, easing = easingStandard)
    fun <T> normalTween() = tween<T>(durationMillis = durationNormal, easing = easingStandard)
    fun <T> slowTween() = tween<T>(durationMillis = durationSlow, easing = easingEmphasized)
}

val LocalMotion = staticCompositionLocalOf { TripPointMotion() }
