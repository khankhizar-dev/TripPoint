package com.android.trippoint.authentication.onboarding

import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.database.preferences.PreferencesManager

class OnboardingViewModel(
    private val authRepository: com.android.trippoint.authentication.domain.repository.AuthRepository
) : BaseViewModel<OnboardingContract.State, OnboardingContract.Intent, OnboardingContract.Effect>(
    initialState = OnboardingContract.State()
) {
    override fun onIntent(intent: OnboardingContract.Intent) {
        when (intent) {
            OnboardingContract.Intent.OnboardingCompleted -> {
                authRepository.setOnboardingCompleted(true)
                sendEffect(OnboardingContract.Effect.NavigateToLogin)
            }
        }
    }
}
