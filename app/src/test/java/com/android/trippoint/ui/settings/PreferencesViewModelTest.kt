package com.android.trippoint.ui.settings

import com.android.trippoint.authentication.domain.model.UserPreferences
import com.android.trippoint.authentication.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: PreferencesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { authRepository.getMyPreferences() } returns Result.success(null)
        viewModel = PreferencesViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPreferences success updates state`() = runTest {
        val prefs = UserPreferences("USD", "en", "MM/DD/YYYY", "IMPERIAL", "DARK", "UTC")
        coEvery { authRepository.getMyPreferences() } returns Result.success(prefs)

        viewModel = PreferencesViewModel(authRepository)
        runCurrent()

        assertEquals("USD", viewModel.uiState.value.currency)
        assertEquals(true, viewModel.uiState.value.isDarkTheme)
    }

    @Test
    fun `currency change auto saves`() = runTest {
        coEvery { authRepository.updatePreferences(any()) } returns Result.success(null)

        viewModel.onIntent(PreferencesContract.Intent.CurrencyChanged("EUR"))
        runCurrent()

        assertEquals("EUR", viewModel.uiState.value.currency)
        coVerify { authRepository.updatePreferences(match { it.currency == "EUR" }) }
    }
}
