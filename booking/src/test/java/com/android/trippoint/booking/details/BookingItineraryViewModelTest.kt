package com.android.trippoint.booking.details

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
class BookingItineraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookingRepository = mockk()
    private lateinit var viewModel: BookingItineraryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BookingItineraryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadItinerary with valid IDs fetches data`() = runTest {
        val booking = mockk<Booking>()
        coEvery { repository.getBooking("t1", "b1") } returns Result.success(booking)

        viewModel.onIntent(BookingItineraryContract.Intent.LoadItinerary("t1", "b1"))
        
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(booking, viewModel.uiState.value.booking)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `LoadItinerary with empty tripId sets error`() = runTest {
        viewModel.onIntent(BookingItineraryContract.Intent.LoadItinerary("", "b1"))
        assertEquals("Trip ID is missing", viewModel.uiState.value.error)
    }

    @Test
    fun `LoadItinerary handle repository failure`() = runTest {
        coEvery { repository.getBooking("t1", "b1") } returns Result.failure(Exception("Not found"))

        viewModel.onIntent(BookingItineraryContract.Intent.LoadItinerary("t1", "b1"))
        
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Not found", viewModel.uiState.value.error)
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(BookingItineraryContract.Intent.BackClicked)
            assertEquals(BookingItineraryContract.Effect.NavigateBack, awaitItem())
        }
    }
}
