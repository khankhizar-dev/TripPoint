package com.android.trippoint.authentication.login

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.R
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel<LoginContract.State, LoginContract.Intent, LoginContract.Effect>(
    initialState = LoginContract.State()
) {
    override fun onIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.EmailChanged -> setState {
                copy(email = intent.email, emailError = null)
            }
            is LoginContract.Intent.PasswordChanged -> setState {
                copy(password = intent.password, passwordError = null)
            }
            LoginContract.Intent.TogglePasswordVisibility -> setState {
                copy(isPasswordVisible = !isPasswordVisible)
            }
            LoginContract.Intent.LoginClicked -> login()
            LoginContract.Intent.ForgotPasswordClicked -> sendEffect(
                LoginContract.Effect.NavigateToForgotPassword
            )
            LoginContract.Intent.SignUpClicked -> sendEffect(LoginContract.Effect.NavigateToSignUp)
            LoginContract.Intent.GoogleLoginClicked -> { /* Handle Google Login */ }
            LoginContract.Intent.AppleLoginClicked -> { /* Handle Apple Login */ }
        }
    }

    private fun login() {
        val currentState = uiState.value
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        
        if (currentState.email.isEmpty() || !currentState.email.matches(emailRegex)) {
            setState { copy(emailError = R.string.auth_login_error_invalid_email) }
            return
        }
        if (currentState.password.isEmpty()) {
            setState { copy(passwordError = R.string.auth_login_error_empty_password) }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // Simulate network call
            delay(LOGIN_SIMULATION_DELAY)
            setState { copy(isLoading = false, isSuccess = true) }
            delay(SUCCESS_DISPLAY_DELAY)
            sendEffect(LoginContract.Effect.NavigateToHome)
        }
    }

    companion object {
        private const val LOGIN_SIMULATION_DELAY = 1500L
        private const val SUCCESS_DISPLAY_DELAY = 2000L
    }
}
