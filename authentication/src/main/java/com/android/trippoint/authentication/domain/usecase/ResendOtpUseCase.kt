package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.repository.AuthRepository

class ResendOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Boolean> {
        return repository.resendEmailOtp()
    }
}
