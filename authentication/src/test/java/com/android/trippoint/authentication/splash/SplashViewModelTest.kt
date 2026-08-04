package com.android.trippoint.authentication.splash

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
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
    fun `initialization sequence updates steps and navigates`() = runTest {
        viewModel = SplashViewModel()
        
        viewModel.uiState.test {
            // Initial state (emitted immediately on collection)
            assertEquals(SplashContract.SplashStep.Initializing, awaitItem().splashStep)
            
            // Advance past first delay (1000ms)
            advanceTimeBy(1001)
            assertEquals(SplashContract.SplashStep.CheckingVersion, awaitItem().splashStep)
            
            // Advance past second delay (1000ms)
            advanceTimeBy(1001)
            assertEquals(SplashContract.SplashStep.SyncingData, awaitItem().splashStep)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sequence ends with navigation to welcome`() = runTest {
        viewModel = SplashViewModel()
        
        viewModel.effect.test {
            // Advance past all delays (3 * 1000ms)
            advanceTimeBy(3001)
            
            assertEquals(SplashContract.Effect.NavigateToWelcome, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `CheckAuth intent triggers authentication check again`() = runTest {
        viewModel = SplashViewModel()
        
        // Wait for the init-triggered sequence to finish
        advanceTimeBy(3001)
        
        viewModel.uiState.test {
            // Consume the current state (SyncingData)
            assertEquals(SplashContract.SplashStep.SyncingData, awaitItem().splashStep)
            
            // Trigger it manually
            viewModel.onIntent(SplashContract.Intent.CheckAuth)

            // It starts again with Initializing
            assertEquals(SplashContract.SplashStep.Initializing, awaitItem().splashStep)
            
            advanceTimeBy(1001)
            assertEquals(SplashContract.SplashStep.CheckingVersion, awaitItem().splashStep)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
