package com.android.trippoint.authentication.forgotpassword

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.R
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : 
    BaseViewModel<ForgotPasswordContract.State, ForgotPasswordContract.Intent, ForgotPasswordContract.Effect>(
        initialState = ForgotPasswordContract.State()
    ) {
    override fun onIntent(intent: ForgotPasswordContract.Intent) {
        when (intent) {
            is ForgotPasswordContract.Intent.EmailChanged -> setState { 
                copy(email = intent.email, emailError = null) 
            }
            ForgotPasswordContract.Intent.SendLinkClicked -> sendResetLink()
            ForgotPasswordContract.Intent.BackToLoginClicked -> sendEffect(
                ForgotPasswordContract.Effect.NavigateToLogin
            )
        }
    }

    private fun sendResetLink() {
        val currentState = uiState.value
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        
        if (currentState.email.isEmpty() || !currentState.email.matches(emailRegex)) {
            setState { copy(emailError = R.string.auth_login_error_invalid_email) }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // Simulate network call
            delay(SIMULATION_DELAY)
            setState { copy(isLoading = false, isSuccess = true) }
            delay(2000)
            sendEffect(ForgotPasswordContract.Effect.NavigateToOtp(currentState.email))
        }
    }

    companion object {
        private const val SIMULATION_DELAY = 1500L
    }
}
