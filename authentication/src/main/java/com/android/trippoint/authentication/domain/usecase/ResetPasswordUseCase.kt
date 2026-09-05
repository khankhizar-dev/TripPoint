package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.network.ResetPasswordInput

class ResetPasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(input: ResetPasswordInput): Result<Boolean> {
        return repository.resetPassword(input)
    }
}
