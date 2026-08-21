package com.android.trippoint.authentication.forgotpassword

import app.cash.turbine.test
import com.android.trippoint.authentication.domain.usecase.ResetPasswordUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class ResetPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val resetPasswordUseCase: ResetPasswordUseCase = mockk()
    private lateinit var viewModel: ResetPasswordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ResetPasswordViewModel(resetPasswordUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.password)
        assertEquals("", state.confirmPassword)
        assertNull(state.passwordError)
    }

    @Test
    fun `successful reset navigates to login`() = runTest {
        coEvery { resetPasswordUseCase(any()) } returns Result.success(true)
        viewModel.email = "test@example.com"
        viewModel.otp = "123456"
        viewModel.onIntent(ResetPasswordContract.Intent.PasswordChanged("Password@123"))
        viewModel.onIntent(ResetPasswordContract.Intent.ConfirmPasswordChanged("Password@123"))
        
        viewModel.effect.test {
            viewModel.onIntent(ResetPasswordContract.Intent.ResetClicked)
            runCurrent()
            
            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(true, viewModel.uiState.value.isSuccess)
            
            advanceTimeBy(2001)
            runCurrent()

            assertEquals(ResetPasswordContract.Effect.NavigateToLogin, awaitItem())
        }
    }
}
