package com.android.trippoint.authentication.splash

import app.cash.turbine.test
import com.android.trippoint.authentication.domain.usecase.AuthState
import com.android.trippoint.authentication.domain.usecase.GetAuthStateUseCase
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getAuthStateUseCase: GetAuthStateUseCase = mockk()
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct step`() = runTest {
        coEvery { getAuthStateUseCase() } returns AuthState.ONBOARDING_REQUIRED
        viewModel = SplashViewModel(getAuthStateUseCase)
        assertEquals(SplashContract.SplashStep.Initializing, viewModel.uiState.value.splashStep)
    }

    @Test
    fun `navigation goes to welcome when onboarding not completed`() = runTest {
        coEvery { getAuthStateUseCase() } returns AuthState.ONBOARDING_REQUIRED
        
        viewModel = SplashViewModel(getAuthStateUseCase)
        
        viewModel.effect.test {
            // Initializing (1s) -> CheckingVersion (1s) -> SyncingData -> Navigate
            advanceTimeBy(2001)
            runCurrent()
            
            assertEquals(SplashContract.Effect.NavigateToWelcome, awaitItem())
        }
    }

    @Test
    fun `navigation goes to login when login required`() = runTest {
        coEvery { getAuthStateUseCase() } returns AuthState.LOGIN_REQUIRED
        
        viewModel = SplashViewModel(getAuthStateUseCase)
        
        viewModel.effect.test {
            advanceTimeBy(2001)
            runCurrent()
            
            assertEquals(SplashContract.Effect.NavigateToLogin, awaitItem())
        }
    }

    @Test
    fun `navigation goes to home when authenticated`() = runTest {
        coEvery { getAuthStateUseCase() } returns AuthState.AUTHENTICATED
        
        viewModel = SplashViewModel(getAuthStateUseCase)
        
        viewModel.effect.test {
            advanceTimeBy(2001)
            runCurrent()
            
            assertEquals(SplashContract.Effect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `navigation goes to profile setup when required`() = runTest {
        coEvery { getAuthStateUseCase() } returns AuthState.PROFILE_SETUP_REQUIRED
        
        viewModel = SplashViewModel(getAuthStateUseCase)
        
        viewModel.effect.test {
            advanceTimeBy(2001)
            runCurrent()
            
            assertEquals(SplashContract.Effect.NavigateToProfileSetup, awaitItem())
        }
    }
}
