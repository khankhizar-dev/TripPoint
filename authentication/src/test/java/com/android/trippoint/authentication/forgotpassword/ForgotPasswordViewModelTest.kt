package com.android.trippoint.authentication.forgotpassword

import app.cash.turbine.test
import com.android.trippoint.authentication.R
import com.android.trippoint.authentication.domain.usecase.RequestPasswordResetUseCase
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
class ForgotPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase = mockk()
    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForgotPasswordViewModel(requestPasswordResetUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertNull(state.emailError)
    }

    @Test
    fun `email change updates state`() {
        viewModel.onIntent(ForgotPasswordContract.Intent.EmailChanged("test@example.com"))
        assertEquals("test@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `send link with invalid email shows error`() {
        viewModel.onIntent(ForgotPasswordContract.Intent.EmailChanged("invalid-email"))
        viewModel.onIntent(ForgotPasswordContract.Intent.SendLinkClicked)
        assertEquals(R.string.auth_login_error_invalid_email, viewModel.uiState.value.emailError)
    }

    @Test
    fun `successful link request navigates to otp`() = runTest {
        coEvery { requestPasswordResetUseCase(any()) } returns Result.success(true)
        viewModel.onIntent(ForgotPasswordContract.Intent.EmailChanged("test@example.com"))
        
        viewModel.effect.test {
            viewModel.onIntent(ForgotPasswordContract.Intent.SendLinkClicked)
            runCurrent()
            
            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(true, viewModel.uiState.value.isSuccess)
            
            advanceTimeBy(2001)
            runCurrent()

            assertEquals(ForgotPasswordContract.Effect.NavigateToOtp("test@example.com"), awaitItem())
        }
    }
}
