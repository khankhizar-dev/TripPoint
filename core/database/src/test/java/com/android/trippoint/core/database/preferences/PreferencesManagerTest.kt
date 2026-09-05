package com.android.trippoint.core.database.preferences

import android.content.Context
import org.robolectric.RuntimeEnvironment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PreferencesManagerTest {

    private lateinit var preferencesManager: PreferencesManager
    private val context: Context by lazy { RuntimeEnvironment.getApplication() }

    @Before
    fun setUp() {
        preferencesManager = PreferencesManager(context)
    }

    @Test
    fun `Onboarding status is saved and retrieved correctly`() {
        assertFalse(preferencesManager.isOnboardingCompleted())
        preferencesManager.setOnboardingCompleted(true)
        assertTrue(preferencesManager.isOnboardingCompleted())
    }

    @Test
    fun `Profile setup status is saved and retrieved correctly`() {
        assertFalse(preferencesManager.isProfileSetupCompleted())
        preferencesManager.setProfileSetupCompleted(true)
        assertTrue(preferencesManager.isProfileSetupCompleted())
    }

    @Test
    fun `Permissions requested status is saved and retrieved correctly`() {
        assertFalse(preferencesManager.arePermissionsRequested())
        preferencesManager.setPermissionsRequested(true)
        assertTrue(preferencesManager.arePermissionsRequested())
    }

    @Test
    fun `Auth token is saved and retrieved correctly`() {
        assertNull(preferencesManager.getAuthToken())
        preferencesManager.setAuthToken("test_token")
        assertEquals("test_token", preferencesManager.getAuthToken())
        preferencesManager.setAuthToken(null)
        assertNull(preferencesManager.getAuthToken())
    }

    @Test
    fun `Refresh token is saved and retrieved correctly`() {
        assertNull(preferencesManager.getRefreshToken())
        preferencesManager.setRefreshToken("refresh_token")
        assertEquals("refresh_token", preferencesManager.getRefreshToken())
        preferencesManager.setRefreshToken(null)
        assertNull(preferencesManager.getRefreshToken())
    }

    @Test
    fun `clearSession removes tokens but keeps setup flags`() {
        preferencesManager.setAuthToken("at")
        preferencesManager.setRefreshToken("rt")
        preferencesManager.setProfileSetupCompleted(true)

        preferencesManager.clearSession()

        assertNull(preferencesManager.getAuthToken())
        assertNull(preferencesManager.getRefreshToken())
        assertTrue(preferencesManager.isProfileSetupCompleted())
    }
}
