package com.android.trippoint.authentication.forgotpassword

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
class ForgotPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForgotPasswordViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `email change updates state`() {
        viewModel.onIntent(ForgotPasswordContract.Intent.EmailChanged("test@example.com"))
        assertEquals("test@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `send link with invalid email shows error`() {
        viewModel.onIntent(ForgotPasswordContract.Intent.EmailChanged("invalid"))
        viewModel.onIntent(ForgotPasswordContract.Intent.SendLinkClicked)
        assertEquals(R.string.auth_login_error_invalid_email, viewModel.uiState.value.emailError)
    }

    @Test
    fun `successful link send updates state`() = runTest {
        viewModel.onIntent(ForgotPasswordContract.Intent.EmailChanged("test@example.com"))
        viewModel.onIntent(ForgotPasswordContract.Intent.SendLinkClicked)
        runCurrent()

        assertEquals(true, viewModel.uiState.value.isLoading)
        advanceTimeBy(1501)
        runCurrent()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(true, viewModel.uiState.value.isSuccess)
    }
}
