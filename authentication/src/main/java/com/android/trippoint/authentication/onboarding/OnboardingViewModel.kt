package com.android.trippoint.authentication.onboarding

import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.database.preferences.PreferencesManager

class OnboardingViewModel(
    private val preferencesManager: PreferencesManager
) : BaseViewModel<OnboardingContract.State, OnboardingContract.Intent, OnboardingContract.Effect>(
    initialState = OnboardingContract.State()
) {
    override fun onIntent(intent: OnboardingContract.Intent) {
        when (intent) {
            OnboardingContract.Intent.OnboardingCompleted -> {
                preferencesManager.setOnboardingCompleted(true)
                sendEffect(OnboardingContract.Effect.NavigateToLogin)
            }
        }
    }
}
