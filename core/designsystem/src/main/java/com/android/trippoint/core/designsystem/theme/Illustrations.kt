package com.android.trippoint.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.android.trippoint.core.designsystem.R

/**
 * TripPoint Illustration System (Section 26.4, 26.5, 26.6).
 * Centralizes all branded illustrations and AI character states.
 */
@Immutable
data class TripPointIllustrations(
    // Onboarding (Section 26.3)
    val onboardingPlan: Int = R.drawable.illustration_welcome,
    val onboardingDiscover: Int = R.drawable.illustration_travel,
    val onboardingTogether: Int = R.drawable.illustration_everything,
    val onboardingOffline: Int = R.drawable.illustration_ready,

    // Empty States (Section 26.4)
    val noTrips: Int = R.drawable.illustration_empty_trip,
    val noBookings: Int = R.drawable.illustration_plan,
    val noInternet: Int = R.drawable.illustration_no_network,
    val noResults: Int = R.drawable.illustration_error,
    val noNotifications: Int = R.drawable.illustration_profile,
    val empty: Int = R.drawable.illustration_everything,

    // Categories (Section 26.5)
    val catFlights: Int = R.drawable.illustration_travel,
    val catHotels: Int = R.drawable.illustration_trip,
    val catActivities: Int = R.drawable.illustration_plan,
    
    // Emotions & Tone (Section 26.9)
    val emoExcited: Int = R.drawable.illustration_everything,
    val emoCalm: Int = R.drawable.illustration_profile,
    val emoConfident: Int = R.drawable.illustration_ready,
    val emoCurious: Int = R.drawable.illustration_travel,
    val emoCelebratory: Int = R.drawable.illustration_success,
    
    // AI Assistant Expressions (Section 26.6)
    val aiHappy: Int = R.drawable.illustration_success,
    val aiNeutral: Int = R.drawable.ic_app_logo,
    val aiThinking: Int = R.drawable.ic_app_logo,
    val aiSurprised: Int = R.drawable.illustration_error,
    val aiSad: Int = R.drawable.illustration_offline,
    val aiExcited: Int = R.drawable.illustration_everything,
    
    // AI Assistant Functional States (Section 26.6)
    val aiIdle: Int = R.drawable.ic_app_logo,
    val aiExplaining: Int = R.drawable.illustration_welcome,
    val aiSuccess: Int = R.drawable.illustration_success,
    val aiCelebration: Int = R.drawable.illustration_success
)

val LocalIllustrations = staticCompositionLocalOf { TripPointIllustrations() }
