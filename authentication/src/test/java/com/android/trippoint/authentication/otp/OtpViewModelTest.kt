package com.android.trippoint.authentication.otp

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
class OtpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: OtpViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OtpViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has resend timer started at 30`() = runTest {
        runCurrent()
        assertEquals(30, viewModel.uiState.value.resendTimer)
    }

    @Test
    fun `otp change updates state`() {
        viewModel.onIntent(OtpContract.Intent.OtpChanged("123456"))
        assertEquals("123456", viewModel.uiState.value.otp)
    }

    @Test
    fun `resend timer counts down correctly`() = runTest {
        runCurrent()
        assertEquals(30, viewModel.uiState.value.resendTimer)
        
        advanceTimeBy(1000)
        runCurrent()
        assertEquals(29, viewModel.uiState.value.resendTimer)
        
        advanceTimeBy(2000)
        runCurrent()
        assertEquals(27, viewModel.uiState.value.resendTimer)
    }

    @Test
    fun `verify with invalid otp shows error`() = runTest {
        viewModel.onIntent(OtpContract.Intent.OtpChanged("000000"))
        viewModel.onIntent(OtpContract.Intent.VerifyClicked)
        runCurrent()
        
        assertEquals(true, viewModel.uiState.value.isLoading)
        advanceTimeBy(1500)
        runCurrent()
        
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(R.string.auth_otp_error_invalid, viewModel.uiState.value.error)
    }

    @Test
    fun `verify with correct otp navigates to home`() = runTest {
        viewModel.onIntent(OtpContract.Intent.OtpChanged("123456"))
        
        viewModel.effect.test {
            viewModel.onIntent(OtpContract.Intent.VerifyClicked)
            runCurrent()
            
            assertEquals(true, viewModel.uiState.value.isLoading)
            advanceTimeBy(1500)
            runCurrent()
            
            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(true, viewModel.uiState.value.isSuccess)
            
            advanceTimeBy(2000)
            runCurrent()
            assertEquals(OtpContract.Effect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `resend otp restarts timer after it expires`() = runTest {
        // Wait for timer to expire (30 seconds * 1000ms)
        advanceTimeBy(30000)
        runCurrent()
        assertEquals(0, viewModel.uiState.value.resendTimer)
        
        viewModel.onIntent(OtpContract.Intent.ResendClicked)
        runCurrent() // Start resend simulation
        
        // resend simulation delay (1000ms)
        advanceTimeBy(1000)
        runCurrent()
        
        assertEquals(30, viewModel.uiState.value.resendTimer)
    }
}
