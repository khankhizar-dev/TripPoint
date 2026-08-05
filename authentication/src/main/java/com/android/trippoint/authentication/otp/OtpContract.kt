package com.android.trippoint.authentication.otp

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class OtpContract {
    sealed class Intent : UiIntent {
        data class OtpChanged(val otp: String) : Intent()
        object VerifyClicked : Intent()
        object ResendClicked : Intent()
    }

    data class State(
        val email: String = "",
        val otp: String = "",
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val resendTimer: Int = 0,
        val error: Int? = null,
        val offlineError: Boolean = false,
        val serverError: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateToHome : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
