package com.android.trippoint.authentication.login

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class LoginContract {
    sealed class Intent : UiIntent {
        data class EmailChanged(val email: String) : Intent()
        data class PasswordChanged(val password: String) : Intent()
        object TogglePasswordVisibility : Intent()
        object LoginClicked : Intent()
        object ForgotPasswordClicked : Intent()
        object SignUpClicked : Intent()
        object GoogleLoginClicked : Intent()
        object AppleLoginClicked : Intent()
    }

    data class State(
        val email: String = "",
        val password: String = "",
        val isPasswordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val emailError: Int? = null,
        val passwordError: Int? = null,
        val offlineError: Boolean = false,
        val serverError: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateToHome : Effect()
        object NavigateToSignUp : Effect()
        object NavigateToForgotPassword : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
