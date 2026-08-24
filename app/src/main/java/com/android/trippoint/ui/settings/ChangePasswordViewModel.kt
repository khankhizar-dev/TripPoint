package com.android.trippoint.ui.settings

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<ChangePasswordContract.State, ChangePasswordContract.Intent, ChangePasswordContract.Effect>(
    initialState = ChangePasswordContract.State()
) {
    override fun onIntent(intent: ChangePasswordContract.Intent) {
        when (intent) {
            is ChangePasswordContract.Intent.CurrentPasswordChanged -> setState { copy(currentPassword = intent.value, currentError = null) }
            is ChangePasswordContract.Intent.NewPasswordChanged -> setState { copy(newPassword = intent.value, newError = null) }
            is ChangePasswordContract.Intent.ConfirmPasswordChanged -> setState { copy(confirmPassword = intent.value, confirmError = null) }
            ChangePasswordContract.Intent.ToggleCurrentVisibility -> setState { copy(isCurrentVisible = !isCurrentVisible) }
            ChangePasswordContract.Intent.ToggleNewVisibility -> setState { copy(isNewVisible = !isNewVisible) }
            ChangePasswordContract.Intent.ToggleConfirmVisibility -> setState { copy(isConfirmVisible = !isConfirmVisible) }
            ChangePasswordContract.Intent.SaveClicked -> savePassword()
        }
    }

    private fun savePassword() {
        val state = uiState.value
        if (state.currentPassword.isBlank()) {
            setState { copy(currentError = "Current password is required") }
            return
        }
        if (state.newPassword.length < 8) {
            setState { copy(newError = "Password must be at least 8 characters") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            setState { copy(confirmError = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = authRepository.changePassword(state.currentPassword, state.newPassword)
            if (result.isSuccess && result.getOrDefault(false)) {
                setState { copy(isLoading = false, isSuccess = true) }
                sendEffect(ChangePasswordContract.Effect.ShowSuccess)
                sendEffect(ChangePasswordContract.Effect.NavigateBack)
            } else {
                setState { copy(isLoading = false) }
                sendEffect(ChangePasswordContract.Effect.ShowError(result.exceptionOrNull()?.message ?: "Failed to change password"))
            }
        }
    }
}
