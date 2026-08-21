package com.android.trippoint.ui.home

import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.usecase.GetMeUseCase
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
    private val getMeUseCase: GetMeUseCase = mockk()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(logoutUseCase, getMeUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser success updates state with user`() = runTest {
        val user = User("1", "test@example.com", "John", "Doe")
        coEvery { getMeUseCase() } returns Result.success(user)

        viewModel.loadUser()
        runCurrent()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(user, viewModel.uiState.value.user)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadUser failure updates state with error`() = runTest {
        coEvery { getMeUseCase() } returns Result.failure(Exception("Network error"))

        viewModel.loadUser()
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
