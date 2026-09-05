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
class ScanTicketViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookingRepository = mockk()
    private lateinit var viewModel: ScanTicketViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScanTicketViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadTripId updates state correctly`() = runTest {
        viewModel.onIntent(ScanTicketContract.Intent.LoadTripId("t1"))
        assertEquals("t1", viewModel.uiState.value.tripId)
    }

    @Test
    fun `ScanClicked simulates scanning and sends BookingAdded effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ScanTicketContract.Intent.ScanClicked)
            
            // Advance time to bypass delay(2000)
            testDispatcher.scheduler.advanceTimeBy(2500)
            
            assertEquals(ScanTicketContract.Effect.BookingAdded, awaitItem())
        }
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ScanTicketContract.Intent.BackClicked)
            assertEquals(ScanTicketContract.Effect.NavigateBack, awaitItem())
        }
    }
}
