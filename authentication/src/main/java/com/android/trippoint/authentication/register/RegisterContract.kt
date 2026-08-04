package com.android.trippoint.authentication.register

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class RegisterContract {
    sealed class Intent : UiIntent {
        data class NameChanged(val name: String) : Intent()
        data class EmailChanged(val email: String) : Intent()
        data class PasswordChanged(val password: String) : Intent()
        data class ConfirmPasswordChanged(val password: String) : Intent()
        object TogglePasswordVisibility : Intent()
        object ToggleConfirmPasswordVisibility : Intent()
        object RegisterClicked : Intent()
        object LoginClicked : Intent()
        object GoogleSignUpClicked : Intent()
        object AppleSignUpClicked : Intent()
    }

    data class State(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val passwordStrength: PasswordStrength = PasswordStrength.EMPTY,
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val nameError: Int? = null,
        val emailError: Int? = null,
        val passwordError: Int? = null,
        val confirmPasswordError: Int? = null,
        val offlineError: Boolean = false,
        val serverError: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateToHome : Effect()
        object NavigateToLogin : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
