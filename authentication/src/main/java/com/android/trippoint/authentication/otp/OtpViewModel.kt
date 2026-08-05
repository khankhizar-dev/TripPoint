package com.android.trippoint.authentication.otp

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.R
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpViewModel : BaseViewModel<OtpContract.State, OtpContract.Intent, OtpContract.Effect>(
    initialState = OtpContract.State()
) {
    private var timerJob: Job? = null

    init {
        startResendTimer()
    }

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
            // Simulate API verification
            delay(1500)
            if (currentState.otp == "123456") { // Dummy correct OTP
                setState { copy(isLoading = false, isSuccess = true) }
                delay(2000)
                sendEffect(OtpContract.Effect.NavigateToHome)
            } else {
                setState { copy(isLoading = false, error = R.string.auth_otp_error_invalid) }
            }
        }
    }

    private fun resendOtp() {
        if (uiState.value.resendTimer > 0) return
        
        viewModelScope.launch {
            // Simulate resend API
            delay(1000)
            startResendTimer()
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
