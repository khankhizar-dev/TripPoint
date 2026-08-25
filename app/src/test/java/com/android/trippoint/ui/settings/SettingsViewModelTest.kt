package com.android.trippoint.ui.settings

import app.cash.turbine.test
import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Set up mock before init if possible, but ViewModel calls getMe in init
        coEvery { authRepository.getMe() } returns Result.success(null)
        viewModel = SettingsViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser success updates state`() = runTest {
        val user = User("1", "test@example.com", "John", "Doe")
        coEvery { authRepository.getMe() } returns Result.success(user)

        viewModel.onIntent(SettingsContract.Intent.LoadUser)
        runCurrent()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(user, viewModel.uiState.value.user)
    }

    @Test
    fun `loadUser failure updates error`() = runTest {
        coEvery { authRepository.getMe() } returns Result.failure(Exception("Error"))

        viewModel.onIntent(SettingsContract.Intent.LoadUser)
        runCurrent()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Error", viewModel.uiState.value.error)
    }

    @Test
    fun `logout success navigates to login`() = runTest {
        coEvery { authRepository.logout() } returns Result.success(true)

        viewModel.effect.test {
            viewModel.onIntent(SettingsContract.Intent.Logout)
            runCurrent()
            assertEquals(SettingsContract.Effect.NavigateToLogin, awaitItem())
        }
    }
}
