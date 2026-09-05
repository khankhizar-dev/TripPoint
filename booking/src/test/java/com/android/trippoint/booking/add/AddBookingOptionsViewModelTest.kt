package com.android.trippoint.booking.add

import app.cash.turbine.test
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
class AddBookingOptionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AddBookingOptionsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddBookingOptionsViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setTripId updates state correctly`() = runTest {
        viewModel.setTripId("t1")
        assertEquals("t1", viewModel.uiState.value.tripId)
    }

    @Test
    fun `BackClicked intent sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(AddBookingOptionsContract.Intent.BackClicked)
            assertEquals(AddBookingOptionsContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `ManualEntryClicked intent sends NavigateToManualEntry effect`() = runTest {
        viewModel.setTripId("t1")
        viewModel.effect.test {
            viewModel.onIntent(AddBookingOptionsContract.Intent.ManualEntryClicked)
            assertEquals(AddBookingOptionsContract.Effect.NavigateToManualEntry("t1"), awaitItem())
        }
    }

    @Test
    fun `PnrReferenceClicked intent sends NavigateToPnrEntry effect`() = runTest {
        viewModel.setTripId("t1")
        viewModel.effect.test {
            viewModel.onIntent(AddBookingOptionsContract.Intent.PnrReferenceClicked)
            assertEquals(AddBookingOptionsContract.Effect.NavigateToPnrEntry("t1"), awaitItem())
        }
    }

    @Test
    fun `ScanTicketClicked intent sends NavigateToScanTicket effect`() = runTest {
        viewModel.setTripId("t1")
        viewModel.effect.test {
            viewModel.onIntent(AddBookingOptionsContract.Intent.ScanTicketClicked)
            assertEquals(AddBookingOptionsContract.Effect.NavigateToScanTicket("t1"), awaitItem())
        }
    }

    @Test
    fun `ImportEmailClicked intent sends NavigateToImportEmail effect`() = runTest {
        viewModel.setTripId("t1")
        viewModel.effect.test {
            viewModel.onIntent(AddBookingOptionsContract.Intent.ImportEmailClicked)
            assertEquals(AddBookingOptionsContract.Effect.NavigateToImportEmail("t1"), awaitItem())
        }
    }
}
