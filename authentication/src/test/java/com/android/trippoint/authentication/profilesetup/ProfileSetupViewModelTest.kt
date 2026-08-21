package com.android.trippoint.authentication.profilesetup

import app.cash.turbine.test
import com.android.trippoint.authentication.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileSetupViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private lateinit var viewModel: ProfileSetupViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileSetupViewModel(authRepository)
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
    fun `final step save saves profile and navigates home`() = runTest {
        // Setup initial data
        viewModel.onIntent(ProfileSetupContract.Intent.FullNameChanged("John Doe"))
        viewModel.onIntent(ProfileSetupContract.Intent.UsernameChanged("johndoe"))
        
        // Navigate to REVIEW
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked) // PHOTO -> ABOUT
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked) // ABOUT -> PREFERENCES
        viewModel.onIntent(ProfileSetupContract.Intent.NextClicked) // PREFERENCES -> REVIEW
        
        coEvery { authRepository.updateProfile(any()) } returns Result.success(true)

        viewModel.effect.test {
            viewModel.onIntent(ProfileSetupContract.Intent.NextClicked)
            
            // Advance to execute the mutation
            advanceUntilIdle()
            
            val state = viewModel.uiState.value
            assertEquals("isSuccess should be true", true, state.isSuccess)
            assertEquals("isLoading should be false", false, state.isLoading)
            
            coVerify { authRepository.updateProfile(any()) }

            advanceTimeBy(2001)
            assertEquals(ProfileSetupContract.Effect.NavigateToHome, awaitItem())
        }
    }
}
