package com.android.trippoint.authentication.domain.repository

import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.model.UserPreferences
import com.android.trippoint.core.network.RegisterInput
import com.android.trippoint.core.network.ResetPasswordInput
import com.android.trippoint.core.network.UpdatePreferencesInput
import com.android.trippoint.core.network.UpdateProfileInput
import com.android.trippoint.core.network.UserDevice

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(input: RegisterInput): Result<User>
    suspend fun updateProfile(input: UpdateProfileInput): Result<Boolean>
    suspend fun updatePreferences(input: UpdatePreferencesInput): Result<UserPreferences?>
    suspend fun changePassword(current: String, new: String): Result<Boolean>
    suspend fun verifyEmailOtp(email: String, otp: String): Result<Boolean>
    suspend fun resendEmailOtp(): Result<Boolean>
    suspend fun requestPasswordReset(email: String): Result<Boolean>
    suspend fun resetPassword(input: ResetPasswordInput): Result<Boolean>
    suspend fun getMe(): Result<User?>
    suspend fun getMyPreferences(): Result<UserPreferences?>
    suspend fun getUserDevices(): Result<List<UserDevice>>
    suspend fun logout(): Result<Boolean>
    
    // Persistence operations (often part of domain if logic depends on them)
    fun getAuthToken(): String?
    fun getRefreshToken(): String?
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
    fun isProfileSetupCompleted(): Boolean
    fun setProfileSetupCompleted(completed: Boolean)
    fun arePermissionsRequested(): Boolean
    fun setPermissionsRequested(requested: Boolean)
}
