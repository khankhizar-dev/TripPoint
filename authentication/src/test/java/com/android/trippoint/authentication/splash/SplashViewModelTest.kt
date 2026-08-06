package com.android.trippoint.authentication.splash

import app.cash.turbine.test
import com.android.trippoint.core.database.preferences.PreferencesManager
import io.mockk.every
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
    private val preferencesManager: PreferencesManager = mockk(relaxed = true)
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mock behavior
        every { preferencesManager.isOnboardingCompleted() } returns false
        every { preferencesManager.getAuthToken() } returns null
        every { preferencesManager.isProfileSetupCompleted() } returns false
        every { preferencesManager.arePermissionsRequested() } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization sequence updates steps correctly`() = runTest {
        viewModel = SplashViewModel(preferencesManager)
        
        viewModel.uiState.test {
            // Initial state
            assertEquals(SplashContract.SplashStep.Initializing, awaitItem().splashStep)
            
            advanceTimeBy(1000)
            runCurrent()
            assertEquals(SplashContract.SplashStep.CheckingVersion, awaitItem().splashStep)
            
            advanceTimeBy(1000)
            runCurrent()
            assertEquals(SplashContract.SplashStep.SyncingData, awaitItem().splashStep)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigation goes to welcome when onboarding not seen`() = runTest {
        every { preferencesManager.isOnboardingCompleted() } returns false
        viewModel = SplashViewModel(preferencesManager)
        
        viewModel.effect.test {
            advanceTimeBy(3001)
            runCurrent()
            
            assertEquals(SplashContract.Effect.NavigateToWelcome, awaitItem())
        }
    }

    @Test
    fun `navigation goes to login when onboarding seen but not logged in`() = runTest {
        every { preferencesManager.isOnboardingCompleted() } returns true
        every { preferencesManager.getAuthToken() } returns null
        
        viewModel = SplashViewModel(preferencesManager)
        
        viewModel.effect.test {
            advanceTimeBy(3001)
            runCurrent()
            
            assertEquals(SplashContract.Effect.NavigateToLogin, awaitItem())
        }
    }

    @Test
    fun `navigation goes to home when already logged in and profile done`() = runTest {
        every { preferencesManager.isOnboardingCompleted() } returns true
        every { preferencesManager.getAuthToken() } returns "valid_token"
        every { preferencesManager.isProfileSetupCompleted() } returns true
        every { preferencesManager.arePermissionsRequested() } returns true
        
        viewModel = SplashViewModel(preferencesManager)
        
        viewModel.effect.test {
            advanceTimeBy(3001)
            runCurrent()
            
            assertEquals(SplashContract.Effect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `navigation goes to profile setup when logged in but profile not done`() = runTest {
        every { preferencesManager.isOnboardingCompleted() } returns true
        every { preferencesManager.getAuthToken() } returns "valid_token"
        every { preferencesManager.isProfileSetupCompleted() } returns false
        
        viewModel = SplashViewModel(preferencesManager)
        
        viewModel.effect.test {
            advanceTimeBy(3001)
            runCurrent()
            
            assertEquals(SplashContract.Effect.NavigateToProfileSetup, awaitItem())
        }
    }
}
