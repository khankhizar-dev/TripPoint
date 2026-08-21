package com.android.trippoint.authentication.otp

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.R
import com.android.trippoint.authentication.domain.usecase.ResendOtpUseCase
import com.android.trippoint.authentication.domain.usecase.VerifyOtpUseCase
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpViewModel(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resendOtpUseCase: ResendOtpUseCase
) : BaseViewModel<OtpContract.State, OtpContract.Intent, OtpContract.Effect>(
    initialState = OtpContract.State()
) {
    private var timerJob: Job? = null
    var email: String = ""

    init {
        startResendTimer()
    }

    var isForgotPasswordFlow: Boolean = false

    override fun onIntent(intent: OtpContract.Intent) {
        when (intent) {
            is OtpContract.Intent.OtpChanged -> setState { copy(otp = intent.otp, error = null) }
            OtpContract.Intent.VerifyClicked -> verifyOtp()
            OtpContract.Intent.ResendClicked -> resendOtp()
        }
    }

    private fun verifyOtp() {
        val currentState = uiState.value
        if (currentState.otp.length < 6) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = verifyOtpUseCase(email, currentState.otp)
            
            if (result.isSuccess && result.getOrDefault(false)) {
                setState { copy(isLoading = false, isSuccess = true) }
                delay(2000)
                if (isForgotPasswordFlow) {
                    sendEffect(OtpContract.Effect.NavigateToResetPassword)
                } else {
                    sendEffect(OtpContract.Effect.NavigateToHome)
                }
            } else {
                setState { copy(isLoading = false, error = R.string.auth_otp_error_invalid) }
            }
        }
    }

    private fun resendOtp() {
        if (uiState.value.resendTimer > 0) return
        
        viewModelScope.launch {
            val result = resendOtpUseCase()
            if (result.isSuccess && result.getOrDefault(false)) {
                startResendTimer()
            } else {
                // Handle error
            }
        }
    }

    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (seconds in RESEND_TIMEOUT_SECONDS downTo 0) {
                setState { copy(resendTimer = seconds) }
                if (seconds > 0) delay(1000)
            }
        }
    }

    companion object {
        private const val RESEND_TIMEOUT_SECONDS = 30
    }
}
