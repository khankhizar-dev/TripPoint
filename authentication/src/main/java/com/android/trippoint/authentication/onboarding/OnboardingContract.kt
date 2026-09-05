package com.android.trippoint.authentication.onboarding

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class OnboardingContract {
    sealed class Intent : UiIntent {
        object OnboardingCompleted : Intent()
    }

    data class State(
        val isCompleted: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateToLogin : Effect()
    }
}
