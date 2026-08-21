package com.android.trippoint.authentication.data.repository

import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.network.AuthRemoteDataSource
import com.android.trippoint.core.network.AuthResponse
import com.android.trippoint.core.network.RegisterInput
import com.android.trippoint.core.network.UpdateProfileInput
import com.android.trippoint.core.network.User as NetworkUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
    fun `updateProfile saves completion status locally`() = runBlocking {
        coEvery { remoteDataSource.updateProfile(any()) } returns true

        val input = UpdateProfileInput(firstName = "John")
        val result = repository.updateProfile(input)

        assertTrue(result.isSuccess)
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
    fun `logout clears preferences`() = runBlocking {
        coEvery { remoteDataSource.logout() } returns true

        repository.logout()

        verify { preferencesManager.clearSession() }
    }
}
