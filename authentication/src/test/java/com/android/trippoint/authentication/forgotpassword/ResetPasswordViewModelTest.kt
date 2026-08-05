package com.android.trippoint.authentication.forgotpassword

import app.cash.turbine.test
import com.android.trippoint.authentication.R
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResetPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ResetPasswordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ResetPasswordViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `password change updates strength`() {
        viewModel.onIntent(ResetPasswordContract.Intent.PasswordChanged("Weak"))
        assertEquals("Weak", viewModel.uiState.value.password)
        assertEquals(
            com.android.trippoint.authentication.register.PasswordStrength.WEAK,
            viewModel.uiState.value.passwordStrength
        )
    }

    @Test
    fun `reset with invalid password shows error`() {
        viewModel.onIntent(ResetPasswordContract.Intent.PasswordChanged("short"))
        viewModel.onIntent(ResetPasswordContract.Intent.ResetClicked)
        assertEquals(R.string.auth_register_error_password_too_short, viewModel.uiState.value.passwordError)
    }

    @Test
    fun `reset with successful flow navigates to login`() = runTest {
        viewModel.onIntent(ResetPasswordContract.Intent.PasswordChanged("Strong@123"))
        viewModel.onIntent(ResetPasswordContract.Intent.ConfirmPasswordChanged("Strong@123"))
        
        viewModel.effect.test {
            viewModel.onIntent(ResetPasswordContract.Intent.ResetClicked)
            runCurrent()

            assertEquals(true, viewModel.uiState.value.isLoading)
            advanceTimeBy(1501)
            runCurrent()

            assertEquals(true, viewModel.uiState.value.isSuccess)
            advanceTimeBy(2001)
            runCurrent()

            assertEquals(ResetPasswordContract.Effect.NavigateToLogin, awaitItem())
        }
    }
}
