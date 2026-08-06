package com.android.trippoint.authentication.profilesetup

import app.cash.turbine.test
import com.android.trippoint.core.database.preferences.PreferencesManager
import io.mockk.mockk
import io.mockk.verify
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
class ProfileSetupViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val preferencesManager: PreferencesManager = mockk(relaxed = true)
    private lateinit var viewModel: ProfileSetupViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileSetupViewModel(preferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial step is PHOTO`() {
        assertEquals(ProfileSetupContract.Step.PHOTO, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `next clicked updates steps correctly`() {
        // PHOTO -> ABOUT
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
        assertEquals(ProfileSetupContract.Step.ABOUT, viewModel.uiState.value.currentStep)

        // ABOUT -> PREFERENCES
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
        assertEquals(ProfileSetupContract.Step.PREFERENCES, viewModel.uiState.value.currentStep)

        // PREFERENCES -> REVIEW
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
        assertEquals(ProfileSetupContract.Step.REVIEW, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `edit step clicked jumps to that step`() {
        // Move to REVIEW first
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
        assertEquals(ProfileSetupContract.Step.REVIEW, viewModel.uiState.value.currentStep)

        // Jump back to ABOUT
        viewModel.onIntent(ProfileSetupContract.Intent.EditStepClicked(ProfileSetupContract.Step.ABOUT))
        assertEquals(ProfileSetupContract.Step.ABOUT, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `input changes update state correctly`() {
        viewModel.onIntent(ProfileSetupContract.Intent.FullNameChanged("John Doe"))
        viewModel.onIntent(ProfileSetupContract.Intent.UsernameChanged("johndoe"))
        viewModel.onIntent(ProfileSetupContract.Intent.CountryChanged("USA"))
        
        val state = viewModel.uiState.value
        assertEquals("John Doe", state.fullName)
        assertEquals("johndoe", state.username)
        assertEquals("USA", state.country)
    }

    @Test
    fun `final step save saves status and navigates home`() = runTest {
        // Navigate to REVIEW
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
        
        viewModel.effect.test {
            viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
            runCurrent()
            
            assertEquals(true, viewModel.uiState.value.isLoading)
            advanceTimeBy(1501)
            runCurrent()
            
            assertEquals(true, viewModel.uiState.value.isSuccess)
            verify { preferencesManager.setProfileSetupCompleted(true) }

            advanceTimeBy(2001)
            runCurrent()
            
            assertEquals(ProfileSetupContract.Effect.NavigateToHome, awaitItem())
        }
    }
}
