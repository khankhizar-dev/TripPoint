package com.android.trippoint.authentication.profilesetup

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileSetupViewModel(
    private val authRepository: com.android.trippoint.authentication.domain.repository.AuthRepository
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
            val currentState = uiState.value
            
            val names = currentState.fullName.split(" ")
            val firstName = names.firstOrNull()
            val lastName = if (names.size > 1) names.drop(1).joinToString(" ") else ""
            
            val profileInput = com.android.trippoint.core.network.UpdateProfileInput(
                firstName = firstName,
                lastName = lastName,
                username = currentState.username,
                phoneNumber = null, // Set if you add phone field to setup
                dateOfBirth = null, // Set if you add dob field to setup
                nationality = null, // Set if you add nationality field to setup
                profilePhotoUrl = currentState.profilePhotoUri,
                country = currentState.country
            )
            
            val preferencesInput = com.android.trippoint.core.network.UpdatePreferencesInput(
                currency = currentState.currency,
                language = currentState.language,
                dateFormat = "DD/MM/YYYY", // Default or add to state
                units = "METRIC", // Default or add to state
                theme = "LIGHT", // Default or add to state
                timezone = currentState.timezone
            )
            
            val profileResult = authRepository.updateProfile(profileInput)
            val preferencesResult = authRepository.updatePreferences(preferencesInput)
            
            if (profileResult.isSuccess && preferencesResult.isSuccess) {
                setState { copy(isLoading = false, isSuccess = true) }
                delay(2000)
                sendEffect(ProfileSetupContract.Effect.NavigateToHome)
            } else {
                setState { copy(isLoading = false) }
                val error = profileResult.exceptionOrNull()?.message 
                    ?: preferencesResult.exceptionOrNull()?.message 
                    ?: "Failed to save profile"
                sendEffect(ProfileSetupContract.Effect.ShowError(error))
            }
        }
    }
}
