package com.android.trippoint.authentication.otp

import app.cash.turbine.test
import com.android.trippoint.authentication.R
import com.android.trippoint.authentication.domain.usecase.ResendOtpUseCase
import com.android.trippoint.authentication.domain.usecase.VerifyOtpUseCase
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
class OtpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val verifyOtpUseCase: VerifyOtpUseCase = mockk()
    private val resendOtpUseCase: ResendOtpUseCase = mockk()
    private lateinit var viewModel: OtpViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OtpViewModel(verifyOtpUseCase, resendOtpUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct default values`() = runTest {
        runCurrent() // Allow init timer to start
        val state = viewModel.uiState.value
        assertEquals("111111", state.otp)
        assertEquals(30, state.resendTimer)
        assertNull(state.error)
    }

    @Test
    fun `otp change updates state`() {
        viewModel.onIntent(OtpContract.Intent.OtpChanged("123456"))
        assertEquals("123456", viewModel.uiState.value.otp)
    }

    @Test
    fun `successful otp verification navigates to home`() = runTest {
        coEvery { verifyOtpUseCase(any(), any()) } returns Result.success(true)
        viewModel.email = "test@example.com"
        viewModel.onIntent(OtpContract.Intent.OtpChanged("123456"))
        
        viewModel.effect.test {
            viewModel.onIntent(OtpContract.Intent.VerifyClicked)
            runCurrent()
            
            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(true, viewModel.uiState.value.isSuccess)
            
            advanceTimeBy(2001)
            runCurrent()

            assertEquals(OtpContract.Effect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `invalid otp shows error`() = runTest {
        coEvery { verifyOtpUseCase(any(), any()) } returns Result.success(false)
        viewModel.email = "test@example.com"
        viewModel.onIntent(OtpContract.Intent.OtpChanged("000000"))
        
        viewModel.onIntent(OtpContract.Intent.VerifyClicked)
        runCurrent()
        
        assertEquals(R.string.auth_otp_error_invalid, viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }
}
