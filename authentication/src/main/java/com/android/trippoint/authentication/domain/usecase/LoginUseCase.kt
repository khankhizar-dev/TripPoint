package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.login(email, password)
    }
}
