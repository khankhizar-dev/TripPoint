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
class BookingDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookingRepository = mockk()
    private lateinit var viewModel: BookingDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BookingDetailsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadBookingDetails with valid IDs fetches data`() = runTest {
        val booking = mockk<Booking>()
        coEvery { repository.getBooking("t1", "b1") } returns Result.success(booking)
        coEvery { repository.getBookingTravellers("t1", "b1") } returns Result.success(emptyList())

        viewModel.onIntent(BookingDetailsContract.Intent.LoadBookingDetails("t1", "b1"))
        
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(booking, viewModel.uiState.value.booking)
        assertEquals(0, viewModel.uiState.value.travellers.size)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `LoadBookingDetails with empty tripId sets error`() = runTest {
        viewModel.onIntent(BookingDetailsContract.Intent.LoadBookingDetails("", "b1"))
        assertEquals("Trip ID is missing", viewModel.uiState.value.error)
    }

    @Test
    fun `LoadBookingDetails handle repository failure`() = runTest {
        coEvery { repository.getBooking("t1", "b1") } returns Result.failure(Exception("Not found"))
        coEvery { repository.getBookingTravellers("t1", "b1") } returns Result.success(emptyList())

        viewModel.onIntent(BookingDetailsContract.Intent.LoadBookingDetails("t1", "b1"))
        
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Not found", viewModel.uiState.value.error)
    }

    @Test
    fun `DeleteClicked calls repository and sends effect on success`() = runTest {
        coEvery { repository.getBooking(any(), any()) } returns Result.success(mockk())
        coEvery { repository.getBookingTravellers(any(), any()) } returns Result.success(emptyList())
        viewModel.onIntent(BookingDetailsContract.Intent.LoadBookingDetails("t1", "b1"))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { repository.deleteBooking("t1", "b1") } returns Result.success(true)

        viewModel.effect.test {
            viewModel.onIntent(BookingDetailsContract.Intent.DeleteClicked)
            assertEquals(BookingDetailsContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `DeleteClicked sets error on failure`() = runTest {
        coEvery { repository.getBooking(any(), any()) } returns Result.success(mockk())
        coEvery { repository.getBookingTravellers(any(), any()) } returns Result.success(emptyList())
        viewModel.onIntent(BookingDetailsContract.Intent.LoadBookingDetails("t1", "b1"))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { repository.deleteBooking("t1", "b1") } returns Result.success(false)

        viewModel.onIntent(BookingDetailsContract.Intent.DeleteClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Failed to delete booking", viewModel.uiState.value.error)
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(BookingDetailsContract.Intent.BackClicked)
            assertEquals(BookingDetailsContract.Effect.NavigateBack, awaitItem())
        }
    }
}
