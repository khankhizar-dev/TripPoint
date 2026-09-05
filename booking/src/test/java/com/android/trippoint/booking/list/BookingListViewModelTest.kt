package com.android.trippoint.booking.list

import app.cash.turbine.test
import com.android.trippoint.booking.domain.model.Booking
import com.android.trippoint.booking.domain.model.BookingStatus
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
class BookingListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookingRepository = mockk()
    private lateinit var viewModel: BookingListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BookingListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadBookings fetches data and applies filters`() = runTest {
        val booking = mockk<Booking>()
        coEvery { booking.title } returns "Flight to Bali"
        coEvery { booking.location } returns "Bali"
        coEvery { booking.status } returns BookingStatus.CONFIRMED
        
        coEvery { repository.getBookings("t1") } returns Result.success(listOf(booking))

        viewModel.onIntent(BookingListContract.Intent.LoadBookings("t1"))
        
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.bookings.size)
        assertEquals(1, viewModel.uiState.value.filteredBookings.size)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `SearchQueryChanged updates filtered bookings`() = runTest {
        val b1 = mockk<Booking>()
        coEvery { b1.title } returns "Flight 1"
        coEvery { b1.location } returns "London"
        coEvery { b1.status } returns BookingStatus.CONFIRMED

        val b2 = mockk<Booking>()
        coEvery { b2.title } returns "Hotel 1"
        coEvery { b2.location } returns "Paris"
        coEvery { b2.status } returns BookingStatus.CONFIRMED

        coEvery { repository.getBookings("t1") } returns Result.success(listOf(b1, b2))

        viewModel.onIntent(BookingListContract.Intent.LoadBookings("t1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(BookingListContract.Intent.SearchQueryChanged("Flight"))
        
        assertEquals("Flight", viewModel.uiState.value.searchQuery)
        assertEquals(1, viewModel.uiState.value.filteredBookings.size)
        assertEquals(b1, viewModel.uiState.value.filteredBookings.first())
    }

    @Test
    fun `TabSelected updates filtered bookings`() = runTest {
        val b1 = mockk<Booking>()
        coEvery { b1.title } returns "Flight"
        coEvery { b1.status } returns BookingStatus.CONFIRMED
        coEvery { b1.location } returns null

        val b2 = mockk<Booking>()
        coEvery { b2.title } returns "Cancelled"
        coEvery { b2.status } returns BookingStatus.CANCELLED
        coEvery { b2.location } returns null

        coEvery { repository.getBookings("t1") } returns Result.success(listOf(b1, b2))

        viewModel.onIntent(BookingListContract.Intent.LoadBookings("t1"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Tab 2 is Cancelled
        viewModel.onIntent(BookingListContract.Intent.TabSelected(2))
        
        assertEquals(2, viewModel.uiState.value.selectedTab)
        assertEquals(1, viewModel.uiState.value.filteredBookings.size)
        assertEquals(b2, viewModel.uiState.value.filteredBookings.first())
    }

    @Test
    fun `BookingClicked sends navigation effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(BookingListContract.Intent.BookingClicked("t1", "b1"))
            assertEquals(BookingListContract.Effect.NavigateToBookingDetails("t1", "b1"), awaitItem())
        }
    }

    @Test
    fun `AddBookingClicked sends navigation effect`() = runTest {
        coEvery { repository.getBookings("t1") } returns Result.success(emptyList())
        viewModel.onIntent(BookingListContract.Intent.LoadBookings("t1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(BookingListContract.Intent.AddBookingClicked)
            assertEquals(BookingListContract.Effect.NavigateToCreateBooking("t1"), awaitItem())
        }
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(BookingListContract.Intent.BackClicked)
            assertEquals(BookingListContract.Effect.NavigateBack, awaitItem())
        }
    }
}
