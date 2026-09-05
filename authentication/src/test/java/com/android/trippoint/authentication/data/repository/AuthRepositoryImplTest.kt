package com.android.trippoint.authentication.data.repository

import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.network.AuthRemoteDataSource
import com.android.trippoint.core.network.AuthResponse
import com.android.trippoint.core.network.RegisterInput
import com.android.trippoint.core.network.UpdateProfileInput
import com.android.trippoint.core.network.User as NetworkUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private val remoteDataSource: AuthRemoteDataSource = mockk()
    private val preferencesManager: PreferencesManager = mockk(relaxed = true)
    private lateinit var repository: AuthRepositoryImpl

    private val networkUser = NetworkUser(
        id = "1",
        email = "test@example.com",
        firstName = "John",
        lastName = "Doe",
        username = "johndoe"
    )

    private val authResponse = AuthResponse(
        user = networkUser,
        token = "token",
        refreshToken = "refresh"
    )

    @Before
    fun setUp() {
        repository = AuthRepositoryImpl(remoteDataSource, preferencesManager)
    }

    @Test
    fun `login saves tokens and returns domain user`() = runBlocking {
        coEvery { remoteDataSource.login(any(), any()) } returns authResponse

        val result = repository.login("test@example.com", "password")

        assertTrue(result.isSuccess)
        assertEquals("John", result.getOrNull()?.firstName)
        verify { preferencesManager.setAuthToken("token") }
        verify { preferencesManager.setRefreshToken("refresh") }
    }

    @Test
    fun `register saves tokens and returns domain user`() = runBlocking {
        coEvery { remoteDataSource.register(any()) } returns authResponse

        val input = RegisterInput("test@example.com", "pass", "John", "Doe")
        val result = repository.register(input)

        assertTrue(result.isSuccess)
        verify { preferencesManager.setAuthToken("token") }
    }

    @Test
    fun `verifyEmailOtp with 111111 bypasses remote call`() = runBlocking {
        val result = repository.verifyEmailOtp("test@example.com", "111111")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrDefault(false))
        coVerify(exactly = 0) { remoteDataSource.verifyEmailOtp(any(), any()) }
    }

    @Test
    fun `verifyEmailOtp with other code calls remote`() = runBlocking {
        coEvery { remoteDataSource.verifyEmailOtp(any(), any()) } returns true

        val result = repository.verifyEmailOtp("test@example.com", "123456")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { remoteDataSource.verifyEmailOtp(any(), any()) }
    }

    @Test
    fun `updateProfile returns success and updates pref when remote call succeeds`() = runBlocking {
        coEvery { remoteDataSource.updateProfile(any()) } returns true

        val input = UpdateProfileInput(firstName = "John")
        val result = repository.updateProfile(input)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrDefault(false))
        verify { preferencesManager.setProfileSetupCompleted(true) }
    }

    @Test
    fun `getMe updates profile status if username present`() = runBlocking {
        coEvery { remoteDataSource.getMe() } returns networkUser

        val result = repository.getMe()

        assertTrue(result.isSuccess)
        verify { preferencesManager.setProfileSetupCompleted(true) }
    }

    @Test
    fun `getMe does not clear session on network failure`() = runBlocking {
        coEvery { remoteDataSource.getMe() } throws Exception("Network error")

        val result = repository.getMe()

        assertTrue(result.isFailure)
        verify(exactly = 0) { preferencesManager.clearSession() }
    }

    @Test
    fun `getMe clears session if remote user is null`() = runBlocking {
        coEvery { remoteDataSource.getMe() } returns null

        val result = repository.getMe()

        assertTrue(result.isSuccess)
        verify(exactly = 1) { preferencesManager.clearSession() }
    }

    @Test
    fun `updatePreferences returns domain preferences`() = runBlocking {
        val networkPrefs = com.android.trippoint.core.network.UserPreferences(
            currency = "USD",
            language = "en",
            dateFormat = "MM/DD/YYYY",
            units = "IMPERIAL",
            theme = "DARK",
            timezone = "UTC"
        )
        coEvery { remoteDataSource.updatePreferences(any()) } returns networkPrefs

        val result = repository.updatePreferences(com.android.trippoint.core.network.UpdatePreferencesInput())

        assertTrue(result.isSuccess)
        assertEquals("USD", result.getOrNull()?.currency)
    }

    @Test
    fun `getMyPreferences returns domain preferences`() = runBlocking {
        val networkPrefs = com.android.trippoint.core.network.UserPreferences(
            currency = "INR",
            language = "hi",
            dateFormat = "DD/MM/YYYY",
            units = "METRIC",
            theme = "LIGHT",
            timezone = "IST"
        )
        coEvery { remoteDataSource.getMyPreferences() } returns networkPrefs

        val result = repository.getMyPreferences()

        assertTrue(result.isSuccess)
        assertEquals("INR", result.getOrNull()?.currency)
    }

    @Test
    fun `changePassword returns success`() = runBlocking {
        coEvery { remoteDataSource.changePassword(any(), any()) } returns true

        val result = repository.changePassword("old", "new")

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `getUserDevices returns list of devices`() = runBlocking {
        val networkDevices = listOf(
            com.android.trippoint.core.network.UserDevice(
                id = "d1",
                deviceName = "Pixel 6",
                platform = "Android",
                appVersion = "1.0",
                lastLoginAt = "now",
                createdAt = "then"
            )
        )
        coEvery { remoteDataSource.getUserDevices() } returns networkDevices

        val result = repository.getUserDevices()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Pixel 6", result.getOrNull()?.first()?.deviceName)
    }

    @Test
    fun `logout clears preferences`() = runBlocking {
        coEvery { remoteDataSource.logout() } returns true

        repository.logout()

        verify { preferencesManager.clearSession() }
    }
}
