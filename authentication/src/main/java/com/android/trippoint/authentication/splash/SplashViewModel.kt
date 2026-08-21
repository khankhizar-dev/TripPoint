package com.android.trippoint.authentication.splash

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val getAuthStateUseCase: com.android.trippoint.authentication.domain.usecase.GetAuthStateUseCase
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

            when (getAuthStateUseCase()) {
                com.android.trippoint.authentication.domain.usecase.AuthState.ONBOARDING_REQUIRED -> 
                    sendEffect(SplashContract.Effect.NavigateToWelcome)
                com.android.trippoint.authentication.domain.usecase.AuthState.LOGIN_REQUIRED -> 
                    sendEffect(SplashContract.Effect.NavigateToLogin)
                com.android.trippoint.authentication.domain.usecase.AuthState.PROFILE_SETUP_REQUIRED -> 
                    sendEffect(SplashContract.Effect.NavigateToProfileSetup)
                com.android.trippoint.authentication.domain.usecase.AuthState.PERMISSIONS_REQUIRED -> 
                    sendEffect(SplashContract.Effect.NavigateToPermissions)
                com.android.trippoint.authentication.domain.usecase.AuthState.AUTHENTICATED -> 
                    sendEffect(SplashContract.Effect.NavigateToHome)
            }
        }
    }
}
