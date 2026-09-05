package com.android.trippoint.booking.details

import app.cash.turbine.test
import com.android.trippoint.booking.domain.model.BookingTraveller
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
class AddTravellerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookingRepository = mockk()
    private lateinit var viewModel: AddTravellerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddTravellerViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadIds updates state correctly`() = runTest {
        viewModel.onIntent(AddTravellerContract.Intent.LoadIds("t1", "b1"))
        assertEquals("t1", viewModel.uiState.value.tripId)
        assertEquals("b1", viewModel.uiState.value.bookingId)
    }

    @Test
    fun `FirstNameChanged updates state correctly`() = runTest {
        viewModel.onIntent(AddTravellerContract.Intent.FirstNameChanged("John"))
        assertEquals("John", viewModel.uiState.value.firstName)
    }

    @Test
    fun `LastNameChanged updates state correctly`() = runTest {
        viewModel.onIntent(AddTravellerContract.Intent.LastNameChanged("Doe"))
        assertEquals("Doe", viewModel.uiState.value.lastName)
    }

    @Test
    fun `Other fields update state correctly`() = runTest {
        viewModel.onIntent(AddTravellerContract.Intent.EmailChanged("j@d.com"))
        viewModel.onIntent(AddTravellerContract.Intent.PhoneChanged("123"))
        viewModel.onIntent(AddTravellerContract.Intent.DobChanged("1990-01-01"))
        viewModel.onIntent(AddTravellerContract.Intent.TicketChanged("T123"))
        viewModel.onIntent(AddTravellerContract.Intent.SeatChanged("1A"))

        assertEquals("j@d.com", viewModel.uiState.value.email)
        assertEquals("123", viewModel.uiState.value.phone)
        assertEquals("1990-01-01", viewModel.uiState.value.dob)
        assertEquals("T123", viewModel.uiState.value.ticket)
        assertEquals("1A", viewModel.uiState.value.seat)
    }

    @Test
    fun `SaveClicked with empty name sets error`() = runTest {
        viewModel.onIntent(AddTravellerContract.Intent.SaveClicked)
        assertEquals("Name is required", viewModel.uiState.value.error)
    }

    @Test
    fun `SaveClicked with valid data calls repository and sends effect`() = runTest {
        viewModel.onIntent(AddTravellerContract.Intent.LoadIds("t1", "b1"))
        viewModel.onIntent(AddTravellerContract.Intent.FirstNameChanged("John"))
        viewModel.onIntent(AddTravellerContract.Intent.LastNameChanged("Doe"))
        
        val traveller = mockk<BookingTraveller>()
        coEvery { repository.addBookingTraveller(any(), any(), any()) } returns Result.success(traveller)

        viewModel.effect.test {
            viewModel.onIntent(AddTravellerContract.Intent.SaveClicked)
            assertEquals(AddTravellerContract.Effect.TravellerAdded, awaitItem())
        }
        
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `SaveClicked with repository failure sets error`() = runTest {
        viewModel.onIntent(AddTravellerContract.Intent.LoadIds("t1", "b1"))
        viewModel.onIntent(AddTravellerContract.Intent.FirstNameChanged("John"))
        viewModel.onIntent(AddTravellerContract.Intent.LastNameChanged("Doe"))
        
        coEvery { 
            repository.addBookingTraveller(any(), any(), any()) 
        } returns Result.failure(Exception("Network error"))

        viewModel.onIntent(AddTravellerContract.Intent.SaveClicked)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("Network error", viewModel.uiState.value.error)
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(AddTravellerContract.Intent.BackClicked)
            assertEquals(AddTravellerContract.Effect.NavigateBack, awaitItem())
        }
    }
}
