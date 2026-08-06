package com.android.trippoint.authentication.profilesetup

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.database.preferences.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileSetupViewModel(
    private val preferencesManager: PreferencesManager
) : BaseViewModel<ProfileSetupContract.State, ProfileSetupContract.Intent, ProfileSetupContract.Effect>(
    initialState = ProfileSetupContract.State()
) {

    override fun onIntent(intent: ProfileSetupContract.Intent) {
        when (intent) {
            is ProfileSetupContract.Intent.PhotoSelected -> setState { copy(profilePhotoUri = intent.uri) }
            is ProfileSetupContract.Intent.FullNameChanged -> setState { copy(fullName = intent.name) }
            is ProfileSetupContract.Intent.UsernameChanged -> setState { copy(username = intent.username) }
            is ProfileSetupContract.Intent.CountryChanged -> setState { copy(country = intent.country) }
            is ProfileSetupContract.Intent.CurrencyChanged -> setState { copy(currency = intent.currency) }
            is ProfileSetupContract.Intent.LanguageChanged -> setState { copy(language = intent.language) }
            is ProfileSetupContract.Intent.TimezoneChanged -> setState { copy(timezone = intent.timezone) }
            is ProfileSetupContract.Intent.EditStepClicked -> setState { copy(currentStep = intent.step) }
            ProfileSetupContract.Intent.NextClicked -> handleNext()
            ProfileSetupContract.Intent.BackClicked -> handleBack()
            ProfileSetupContract.Intent.SkipPhotoClicked -> handleNext()
        }
    }

    private fun handleNext() {
        val currentState = uiState.value
        when (currentState.currentStep) {
            ProfileSetupContract.Step.PHOTO -> setState { copy(currentStep = ProfileSetupContract.Step.ABOUT) }
            ProfileSetupContract.Step.ABOUT -> setState { copy(currentStep = ProfileSetupContract.Step.PREFERENCES) }
            ProfileSetupContract.Step.PREFERENCES -> setState { copy(currentStep = ProfileSetupContract.Step.REVIEW) }
            ProfileSetupContract.Step.REVIEW -> saveProfile()
        }
    }

    private fun handleBack() {
        val currentState = uiState.value
        when (currentState.currentStep) {
            ProfileSetupContract.Step.PHOTO -> { /* Do nothing or exit */ }
            ProfileSetupContract.Step.ABOUT -> setState { copy(currentStep = ProfileSetupContract.Step.PHOTO) }
            ProfileSetupContract.Step.PREFERENCES -> setState { copy(currentStep = ProfileSetupContract.Step.ABOUT) }
            ProfileSetupContract.Step.REVIEW -> setState { copy(currentStep = ProfileSetupContract.Step.PREFERENCES) }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // Simulate API save
            delay(1500)
            preferencesManager.setProfileSetupCompleted(true)
            setState { copy(isLoading = false, isSuccess = true) }
            delay(2000)
            sendEffect(ProfileSetupContract.Effect.NavigateToHome)
        }
    }
}
