package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.repository.AuthRepository

class RequestPasswordResetUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Boolean> {
        return repository.requestPasswordReset(email)
    }
}
