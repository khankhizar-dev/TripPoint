package com.android.trippoint.ui.settings

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.network.UpdateProfileInput
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<EditProfileContract.State, EditProfileContract.Intent, EditProfileContract.Effect>(
    initialState = EditProfileContract.State()
) {
    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = authRepository.getMe()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    setState {
                        copy(
                            isLoading = false,
                            fullName = user.fullName ?: "${user.firstName ?: ""} ${user.lastName ?: ""}".trim(),
                            email = user.email,
                            phone = user.phoneNumber ?: "",
                            dob = user.dateOfBirth ?: "",
                            profilePhotoUri = user.profilePhotoUrl,
                            nationality = user.nationality ?: user.country ?: ""
                        )
                    }
                }
            } else {
                setState { copy(isLoading = false, error = "Failed to load profile") }
            }
        }
    }

    override fun onIntent(intent: EditProfileContract.Intent) {
        when (intent) {
            is EditProfileContract.Intent.FullNameChanged -> setState { copy(fullName = intent.name) }
            is EditProfileContract.Intent.EmailChanged -> setState { copy(email = intent.email) }
            is EditProfileContract.Intent.PhoneChanged -> setState { copy(phone = intent.phone) }
            is EditProfileContract.Intent.DobChanged -> setState { copy(dob = intent.dob) }
            is EditProfileContract.Intent.NationalityChanged -> setState { copy(nationality = intent.nationality) }
            is EditProfileContract.Intent.PhotoSelected -> setState { copy(profilePhotoUri = intent.uri) }
            EditProfileContract.Intent.SaveClicked -> saveProfile()
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val currentState = uiState.value
            val names = currentState.fullName.split(" ")
            val firstName = names.firstOrNull() ?: ""
            val lastName = if (names.size > 1) names.drop(1).joinToString(" ") else ""
            
            val input = UpdateProfileInput(
                firstName = firstName,
                lastName = lastName,
                username = null,
                phoneNumber = currentState.phone,
                dateOfBirth = if (currentState.dob.isNotBlank()) "${currentState.dob}T00:00:00" else null,
                nationality = currentState.nationality,
                profilePhotoUrl = currentState.profilePhotoUri,
                country = null
            )
            
            val result = authRepository.updateProfile(input)
            if (result.isSuccess) {
                setState { copy(isLoading = false, isSuccess = true) }
                sendEffect(EditProfileContract.Effect.NavigateBack)
            } else {
                setState { copy(isLoading = false) }
                sendEffect(EditProfileContract.Effect.ShowError("Failed to save changes"))
            }
        }
    }
}
