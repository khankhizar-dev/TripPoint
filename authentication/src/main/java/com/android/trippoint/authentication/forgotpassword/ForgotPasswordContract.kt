package com.android.trippoint.authentication.forgotpassword

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ForgotPasswordContract {
    sealed class Intent : UiIntent {
        data class EmailChanged(val email: String) : Intent()
        object SendLinkClicked : Intent()
        object BackToLoginClicked : Intent()
    }

    data class State(
        val email: String = "",
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val emailError: Int? = null,
        val offlineError: Boolean = false,
        val serverError: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateToLogin : Effect()
        data class NavigateToOtp(val email: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
