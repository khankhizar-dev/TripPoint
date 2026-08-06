package com.android.trippoint.authentication.splash

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.database.preferences.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val preferencesManager: PreferencesManager
) : BaseViewModel<SplashContract.State, SplashContract.Intent, SplashContract.Effect>(
    initialState = SplashContract.State()
) {
    init {
        onIntent(SplashContract.Intent.CheckAuth)
    }

    override fun onIntent(intent: SplashContract.Intent) {
        when (intent) {
            is SplashContract.Intent.CheckAuth -> checkAuth()
        }
    }

    private fun checkAuth() {
        viewModelScope.launch {
            setState { copy(splashStep = SplashContract.SplashStep.Initializing) }
            delay(1000)
            
            setState { copy(splashStep = SplashContract.SplashStep.CheckingVersion) }
            delay(1000)
            
            setState { copy(splashStep = SplashContract.SplashStep.SyncingData) }
            delay(1000)

            val isOnboardingCompleted = preferencesManager.isOnboardingCompleted()
            val isUserLoggedIn = preferencesManager.getAuthToken() != null
            val isProfileCompleted = preferencesManager.isProfileSetupCompleted()
            val arePermissionsRequested = preferencesManager.arePermissionsRequested()

            when {
                !isOnboardingCompleted -> sendEffect(SplashContract.Effect.NavigateToWelcome)
                !isUserLoggedIn -> sendEffect(SplashContract.Effect.NavigateToLogin)
                !isProfileCompleted -> sendEffect(SplashContract.Effect.NavigateToProfileSetup)
                !arePermissionsRequested -> sendEffect(SplashContract.Effect.NavigateToPermissions)
                else -> sendEffect(SplashContract.Effect.NavigateToHome)
            }
        }
    }
}
