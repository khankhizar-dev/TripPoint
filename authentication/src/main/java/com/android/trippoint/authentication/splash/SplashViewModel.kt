package com.android.trippoint.authentication.splash

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel<SplashContract.State, SplashContract.Intent, SplashContract.Effect>(
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
            // Simulation of sequential loading steps
            setState { copy(splashStep = SplashContract.SplashStep.Initializing) }
            delay(1000)
            
            setState { copy(splashStep = SplashContract.SplashStep.CheckingVersion) }
            delay(1000)
            
            setState { copy(splashStep = SplashContract.SplashStep.SyncingData) }
            delay(1000)

            // For now, always navigate to Login
            sendEffect(SplashContract.Effect.NavigateToLogin)
        }
    }
}
