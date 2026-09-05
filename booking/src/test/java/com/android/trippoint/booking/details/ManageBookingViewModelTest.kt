package com.android.trippoint.booking.details

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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManageBookingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookingRepository = mockk()
    private lateinit var viewModel: ManageBookingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ManageBookingViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadBooking with valid IDs updates state`() = runTest {
        viewModel.onIntent(ManageBookingContract.Intent.LoadBooking("t1", "b1"))
        assertEquals("t1", viewModel.uiState.value.tripId)
        assertEquals("b1", viewModel.uiState.value.bookingId)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `LoadBooking with empty tripId sets error`() = runTest {
        viewModel.onIntent(ManageBookingContract.Intent.LoadBooking("", "b1"))
        assertEquals("Trip ID is missing", viewModel.uiState.value.error)
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ManageBookingContract.Intent.BackClicked)
            assertEquals(ManageBookingContract.Effect.NavigateBack, awaitItem())
        }
    }
}
