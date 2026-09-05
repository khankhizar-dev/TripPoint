package com.android.trippoint.booking.create

import app.cash.turbine.test
import com.android.trippoint.booking.domain.model.Booking
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.trip.domain.repository.TripRepository
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
class CreateBookingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookingRepository = mockk()
    private val tripRepository: TripRepository = mockk()
    private lateinit var viewModel: CreateBookingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateBookingViewModel(repository, tripRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadTripId updates state and fetches trips if id is blank`() = runTest {
        coEvery { tripRepository.getTrips() } returns Result.success(emptyList())
        
        viewModel.onIntent(CreateBookingContract.Intent.LoadTripId(""))
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("", viewModel.uiState.value.tripId)
        assertEquals(0, viewModel.uiState.value.availableTrips.size)
    }

    @Test
    fun `TitleChanged updates state correctly`() = runTest {
        viewModel.onIntent(CreateBookingContract.Intent.TitleChanged("My Flight"))
        assertEquals("My Flight", viewModel.uiState.value.title)
    }

    @Test
    fun `TypeChanged updates state correctly`() = runTest {
        viewModel.onIntent(CreateBookingContract.Intent.TypeChanged("HOTEL"))
        assertEquals("HOTEL", viewModel.uiState.value.type)
    }

    @Test
    fun `Date and Time changes update state correctly`() = runTest {
        viewModel.onIntent(CreateBookingContract.Intent.DateChanged("2025-01-01"))
        viewModel.onIntent(CreateBookingContract.Intent.TimeChanged("10:00"))
        assertEquals("2025-01-01", viewModel.uiState.value.date)
        assertEquals("10:00", viewModel.uiState.value.time)
    }

    @Test
    fun `EndDate and EndTime changes update state correctly`() = runTest {
        viewModel.onIntent(CreateBookingContract.Intent.EndDateChanged("2025-01-02"))
        viewModel.onIntent(CreateBookingContract.Intent.EndTimeChanged("12:00"))
        assertEquals("2025-01-02", viewModel.uiState.value.endDate)
        assertEquals("12:00", viewModel.uiState.value.endTime)
    }

    @Test
    fun `Other field changes update state correctly`() = runTest {
        viewModel.onIntent(CreateBookingContract.Intent.ProviderChanged("Hilton"))
        viewModel.onIntent(CreateBookingContract.Intent.ReferenceChanged("REF123"))
        viewModel.onIntent(CreateBookingContract.Intent.LocationChanged("Paris"))
        viewModel.onIntent(CreateBookingContract.Intent.AmountChanged("100"))
        viewModel.onIntent(CreateBookingContract.Intent.CurrencyChanged("EUR"))
        viewModel.onIntent(CreateBookingContract.Intent.NotesChanged("Some notes"))

        assertEquals("Hilton", viewModel.uiState.value.provider)
        assertEquals("REF123", viewModel.uiState.value.reference)
        assertEquals("Paris", viewModel.uiState.value.location)
        assertEquals("100", viewModel.uiState.value.amount)
        assertEquals("EUR", viewModel.uiState.value.currency)
        assertEquals("Some notes", viewModel.uiState.value.notes)
    }

    @Test
    fun `SaveClicked with empty tripId sets error`() = runTest {
        viewModel.onIntent(CreateBookingContract.Intent.SaveClicked)
        assertEquals("Trip ID is missing", viewModel.uiState.value.error)
    }

    @Test
    fun `SaveClicked with valid data calls repository and sends effect`() = runTest {
        viewModel.onIntent(CreateBookingContract.Intent.LoadTripId("t1"))
        viewModel.onIntent(CreateBookingContract.Intent.TitleChanged("My Flight"))
        viewModel.onIntent(CreateBookingContract.Intent.DateChanged("2025-01-01"))
        viewModel.onIntent(CreateBookingContract.Intent.TimeChanged("10:00"))

        val booking = mockk<Booking>()
        coEvery { repository.createBooking(any(), any()) } returns Result.success(booking)

        viewModel.effect.test {
            viewModel.onIntent(CreateBookingContract.Intent.SaveClicked)
            assertEquals(CreateBookingContract.Effect.BookingCreated, awaitItem())
        }
        
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `SaveClicked handle repository failure`() = runTest {
        viewModel.onIntent(CreateBookingContract.Intent.LoadTripId("t1"))
        viewModel.onIntent(CreateBookingContract.Intent.TitleChanged("My Flight"))
        viewModel.onIntent(CreateBookingContract.Intent.DateChanged("2025-01-01"))

        coEvery { repository.createBooking(any(), any()) } returns Result.failure(Exception("Creation failed"))

        viewModel.onIntent(CreateBookingContract.Intent.SaveClicked)
        
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Creation failed", viewModel.uiState.value.error)
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(CreateBookingContract.Intent.BackClicked)
            assertEquals(CreateBookingContract.Effect.NavigateBack, awaitItem())
        }
    }
}
