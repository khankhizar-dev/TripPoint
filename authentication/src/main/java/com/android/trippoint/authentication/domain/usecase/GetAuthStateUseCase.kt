package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.repository.AuthRepository

class GetAuthStateUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): AuthState {
        if (!repository.isOnboardingCompleted()) return AuthState.ONBOARDING_REQUIRED
        
        val token = repository.getAuthToken()
        if (token == null) return AuthState.LOGIN_REQUIRED
        
        val userResult = repository.getMe()
        if (userResult.isFailure || userResult.getOrNull() == null) {
            return AuthState.LOGIN_REQUIRED
        }
        
        if (!repository.isProfileSetupCompleted()) return AuthState.PROFILE_SETUP_REQUIRED
        if (!repository.arePermissionsRequested()) return AuthState.PERMISSIONS_REQUIRED
        
        return AuthState.AUTHENTICATED
    }
}

enum class AuthState {
    ONBOARDING_REQUIRED,
    LOGIN_REQUIRED,
    PROFILE_SETUP_REQUIRED,
    PERMISSIONS_REQUIRED,
    AUTHENTICATED
}
