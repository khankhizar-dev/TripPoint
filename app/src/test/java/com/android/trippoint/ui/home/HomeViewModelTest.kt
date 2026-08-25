package com.android.trippoint.ui.home

import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.usecase.LogoutUseCase
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val logoutUseCase: LogoutUseCase = mockk()
    private val repository: com.android.trippoint.authentication.domain.repository.AuthRepository = mockk()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(logoutUseCase, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData success updates state with user`() = runTest {
        val user = User("1", "test@example.com", "John", "Doe")
        coEvery { repository.getMe() } returns Result.success(user)
        coEvery { repository.getMyPreferences() } returns Result.success(null)

        viewModel.loadData()
        runCurrent()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(user, viewModel.uiState.value.user)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadData failure updates state with error`() = runTest {
        coEvery { repository.getMe() } returns Result.failure(Exception("Network error"))
        coEvery { repository.getMyPreferences() } returns Result.success(null)

        viewModel.loadData()
        runCurrent()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.user)
        assertEquals("Network error", viewModel.uiState.value.error)
    }

    @Test
    fun `logout success calls callback`() = runTest {
        coEvery { logoutUseCase() } returns Result.success(true)
        var callbackCalled = false

        viewModel.logout {
            callbackCalled = true
        }
        runCurrent()

        assertEquals(true, callbackCalled)
    }
}
