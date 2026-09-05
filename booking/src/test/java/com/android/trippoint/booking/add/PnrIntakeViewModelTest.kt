package com.android.trippoint.booking.add

import app.cash.turbine.test
import com.android.trippoint.booking.domain.model.Booking
import com.android.trippoint.booking.domain.repository.BookingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PnrIntakeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookingRepository = mockk()
    private lateinit var viewModel: PnrIntakeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PnrIntakeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadTripId updates state correctly`() = runTest {
        viewModel.onIntent(PnrIntakeContract.Intent.LoadTripId("t1"))
        assertEquals("t1", viewModel.uiState.value.tripId)
    }

    @Test
    fun `PnrChanged updates state correctly`() = runTest {
        viewModel.onIntent(PnrIntakeContract.Intent.PnrChanged("REF123"))
        assertEquals("REF123", viewModel.uiState.value.pnr)
    }

    @Test
    fun `FetchClicked calls repository and sends effect on success`() = runTest {
        viewModel.onIntent(PnrIntakeContract.Intent.LoadTripId("t1"))
        viewModel.onIntent(PnrIntakeContract.Intent.PnrChanged("REF123"))

        val booking = mockk<Booking>()
        coEvery { repository.importBooking("t1", "REF123") } returns Result.success(booking)

        viewModel.effect.test {
            viewModel.onIntent(PnrIntakeContract.Intent.FetchClicked)
            assertEquals(PnrIntakeContract.Effect.BookingAdded, awaitItem())
        }
        
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `FetchClicked sets error on failure`() = runTest {
        viewModel.onIntent(PnrIntakeContract.Intent.LoadTripId("t1"))
        viewModel.onIntent(PnrIntakeContract.Intent.PnrChanged("REF123"))

        coEvery { repository.importBooking("t1", "REF123") } returns Result.failure(Exception("Import failed"))

        viewModel.onIntent(PnrIntakeContract.Intent.FetchClicked)
        
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Import failed", viewModel.uiState.value.error)
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(PnrIntakeContract.Intent.BackClicked)
            assertEquals(PnrIntakeContract.Effect.NavigateBack, awaitItem())
        }
    }
}
