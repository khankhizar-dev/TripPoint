package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.repository.AuthRepository

class VerifyOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, otp: String): Result<Boolean> {
        return repository.verifyEmailOtp(email, otp)
    }
}
