package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.network.RegisterInput

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(input: RegisterInput): Result<User> {
        return repository.register(input)
    }
}
