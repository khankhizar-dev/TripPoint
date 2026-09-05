package com.android.trippoint.booking.add

import app.cash.turbine.test
import com.android.trippoint.booking.domain.repository.BookingRepository
import io.mockk.mockk
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
class ImportEmailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookingRepository = mockk()
    private lateinit var viewModel: ImportEmailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ImportEmailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadTripId updates state correctly`() = runTest {
        viewModel.onIntent(ImportEmailContract.Intent.LoadTripId("t1"))
        assertEquals("t1", viewModel.uiState.value.tripId)
    }

    @Test
    fun `ProviderClicked simulates connection and sends success effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ImportEmailContract.Intent.ProviderClicked("Gmail"))
            
            // Advance time to bypass delay(1500)
            testDispatcher.scheduler.advanceTimeBy(2000)
            
            assertEquals(ImportEmailContract.Effect.Success, awaitItem())
        }
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ImportEmailContract.Intent.BackClicked)
            assertEquals(ImportEmailContract.Effect.NavigateBack, awaitItem())
        }
    }
}
