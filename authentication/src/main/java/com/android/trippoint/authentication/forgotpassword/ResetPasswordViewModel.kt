package com.android.trippoint.authentication.forgotpassword

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.R
import com.android.trippoint.authentication.register.PasswordStrength
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResetPasswordViewModel : 
    BaseViewModel<ResetPasswordContract.State, ResetPasswordContract.Intent, ResetPasswordContract.Effect>(
        initialState = ResetPasswordContract.State()
    ) {
    override fun onIntent(intent: ResetPasswordContract.Intent) {
        when (intent) {
            is ResetPasswordContract.Intent.PasswordChanged -> setState {
                copy(
                    password = intent.password,
                    passwordError = null,
                    passwordStrength = PasswordStrength.calculate(intent.password)
                )
            }
            is ResetPasswordContract.Intent.ConfirmPasswordChanged -> setState {
                copy(confirmPassword = intent.password, confirmPasswordError = null)
            }
            ResetPasswordContract.Intent.TogglePasswordVisibility -> setState {
                copy(isPasswordVisible = !isPasswordVisible)
            }
            ResetPasswordContract.Intent.ToggleConfirmPasswordVisibility -> setState {
                copy(isConfirmPasswordVisible = !isConfirmPasswordVisible)
            }
            ResetPasswordContract.Intent.ResetClicked -> resetPassword()
            ResetPasswordContract.Intent.RequestNewLinkClicked -> { /* Handle request new link */ }
        }
    }

    private fun resetPassword() {
        val currentState = uiState.value
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$".toRegex()

        if (currentState.password.length < 8) {
            setState { copy(passwordError = R.string.auth_register_error_password_too_short) }
            return
        }
        if (!currentState.password.matches(passwordRegex)) {
            setState { copy(passwordError = R.string.auth_register_error_password_weak_complexity) }
            return
        }
        if (currentState.password != currentState.confirmPassword) {
            setState { copy(confirmPasswordError = R.string.auth_register_error_password_mismatch) }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // Simulate network call
            delay(1500)
            setState { copy(isLoading = false, isSuccess = true) }
            delay(2000)
            sendEffect(ResetPasswordContract.Effect.NavigateToLogin)
        }
    }
}
