package com.android.trippoint.authentication.forgotpassword

import com.android.trippoint.authentication.register.PasswordStrength
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ResetPasswordContract {
    sealed class Intent : UiIntent {
        data class PasswordChanged(val password: String) : Intent()
        data class ConfirmPasswordChanged(val password: String) : Intent()
        object TogglePasswordVisibility : Intent()
        object ToggleConfirmPasswordVisibility : Intent()
        object ResetClicked : Intent()
        object RequestNewLinkClicked : Intent()
    }

    data class State(
        val password: String = "",
        val confirmPassword: String = "",
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val passwordStrength: PasswordStrength = PasswordStrength.EMPTY,
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val isLinkExpired: Boolean = false,
        val passwordError: Int? = null,
        val confirmPasswordError: Int? = null,
        val offlineError: Boolean = false,
        val serverError: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateToLogin : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
