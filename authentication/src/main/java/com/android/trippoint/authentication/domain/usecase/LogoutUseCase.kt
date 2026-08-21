package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Boolean> {
        return repository.logout()
    }
}
