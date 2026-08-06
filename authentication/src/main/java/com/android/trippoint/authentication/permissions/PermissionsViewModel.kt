package com.android.trippoint.authentication.permissions

import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.database.preferences.PreferencesManager

class PermissionsViewModel(
    private val preferencesManager: PreferencesManager
) : 
    BaseViewModel<PermissionsContract.State, PermissionsContract.Intent, PermissionsContract.Effect>(
        initialState = PermissionsContract.State()
    ) {

    override fun onIntent(intent: PermissionsContract.Intent) {
        when (intent) {
            PermissionsContract.Intent.AllowClicked -> {
                sendEffect(PermissionsContract.Effect.RequestPermission(uiState.value.currentStep))
                handleNext()
            }
            PermissionsContract.Intent.DenyClicked -> handleNext()
            PermissionsContract.Intent.ExploreClicked -> {
                preferencesManager.setPermissionsRequested(true)
                sendEffect(PermissionsContract.Effect.NavigateToHome)
            }
        }
    }

    private fun handleNext() {
        val currentState = uiState.value
        when (currentState.currentStep) {
            PermissionsContract.Step.NOTIFICATIONS -> setState { 
                copy(currentStep = PermissionsContract.Step.LOCATION) 
            }
            PermissionsContract.Step.LOCATION -> setState { 
                copy(currentStep = PermissionsContract.Step.CALENDAR) 
            }
            PermissionsContract.Step.CALENDAR -> setState { copy(isAllSet = true) }
        }
    }
}
