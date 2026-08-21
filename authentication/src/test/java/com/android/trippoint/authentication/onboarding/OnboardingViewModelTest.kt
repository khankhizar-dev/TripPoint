package com.android.trippoint.authentication.onboarding

import app.cash.turbine.test
import com.android.trippoint.authentication.domain.repository.AuthRepository
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OnboardingViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onboarding completed saves status and navigates to login`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(OnboardingContract.Intent.OnboardingCompleted)
            verify { authRepository.setOnboardingCompleted(true) }
            assertEquals(OnboardingContract.Effect.NavigateToLogin, awaitItem())
        }
    }
}
