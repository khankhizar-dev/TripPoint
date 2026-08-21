package com.android.trippoint.authentication.register

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.R
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.database.preferences.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: com.android.trippoint.authentication.domain.usecase.RegisterUseCase
) : BaseViewModel<RegisterContract.State, RegisterContract.Intent, RegisterContract.Effect>(
    initialState = RegisterContract.State()
) {
    override fun onIntent(intent: RegisterContract.Intent) {
        when (intent) {
            is RegisterContract.Intent.NameChanged -> setState {
                copy(name = intent.name, nameError = null)
            }
            is RegisterContract.Intent.EmailChanged -> setState {
                copy(email = intent.email, emailError = null)
            }
            is RegisterContract.Intent.PasswordChanged -> setState {
                copy(
                    password = intent.password,
                    passwordError = null,
                    passwordStrength = PasswordStrength.calculate(intent.password)
                )
            }
            is RegisterContract.Intent.ConfirmPasswordChanged -> setState {
                copy(confirmPassword = intent.password, confirmPasswordError = null)
            }
            RegisterContract.Intent.TogglePasswordVisibility -> setState {
                copy(isPasswordVisible = !isPasswordVisible)
            }
            RegisterContract.Intent.ToggleConfirmPasswordVisibility -> setState {
                copy(isConfirmPasswordVisible = !isConfirmPasswordVisible)
            }
            RegisterContract.Intent.RegisterClicked -> register()
            RegisterContract.Intent.LoginClicked -> sendEffect(RegisterContract.Effect.NavigateToLogin)
            RegisterContract.Intent.GoogleSignUpClicked -> { /* Handle Google Sign Up */ }
            RegisterContract.Intent.AppleSignUpClicked -> { /* Handle Apple Sign Up */ }
        }
    }

    private fun register() {
        val currentState = uiState.value
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        if (currentState.name.isEmpty()) {
            setState { copy(nameError = R.string.auth_register_error_invalid_name) }
            return
        }
        if (currentState.email.isEmpty() || !currentState.email.matches(emailRegex)) {
            setState { copy(emailError = R.string.auth_login_error_invalid_email) }
            return
        }
        if (currentState.password.length < MIN_PASSWORD_LENGTH) {
            setState { copy(passwordError = R.string.auth_register_error_password_too_short) }
            return
        }
        
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$".toRegex()
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
            val input = com.android.trippoint.core.network.RegisterInput(
                email = currentState.email,
                password = currentState.password,
                firstName = currentState.name.split(" ").firstOrNull() ?: "",
                lastName = currentState.name.split(" ").getOrNull(1) ?: ""
            )
            val result = registerUseCase(input)
            if (result.isSuccess) {
                setState { copy(isLoading = false, isSuccess = true) }
                delay(SUCCESS_DISPLAY_DELAY)
                sendEffect(RegisterContract.Effect.NavigateToOtp(currentState.email))
            } else {
                setState { copy(isLoading = false, serverError = true) }
            }
        }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val REGISTER_SIMULATION_DELAY = 1500L
        private const val SUCCESS_DISPLAY_DELAY = 2000L
    }
}
