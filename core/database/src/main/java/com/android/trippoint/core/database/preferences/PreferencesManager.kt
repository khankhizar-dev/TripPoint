package com.android.trippoint.core.database.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferencesManager(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "trippoint_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setProfileSetupCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_PROFILE_SETUP_COMPLETED, completed).apply()
    }

    fun isProfileSetupCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_PROFILE_SETUP_COMPLETED, false)
    }

    fun setPermissionsRequested(requested: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_PERMISSIONS_REQUESTED, requested).apply()
    }

    fun arePermissionsRequested(): Boolean {
        return sharedPreferences.getBoolean(KEY_PERMISSIONS_REQUESTED, false)
    }

    fun setAuthToken(token: String?) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearSession() {
        sharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_PROFILE_SETUP_COMPLETED)
            .apply()
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_PROFILE_SETUP_COMPLETED = "profile_setup_completed"
        private const val KEY_PERMISSIONS_REQUESTED = "permissions_requested"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
