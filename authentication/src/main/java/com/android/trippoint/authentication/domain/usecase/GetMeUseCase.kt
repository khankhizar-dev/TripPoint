package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.repository.AuthRepository

class GetMeUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<User?> {
        return repository.getMe()
    }
}
