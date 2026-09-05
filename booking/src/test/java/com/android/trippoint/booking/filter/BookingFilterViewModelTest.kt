package com.android.trippoint.booking.filter

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
class BookingFilterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: BookingFilterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BookingFilterViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `SearchQueryChanged updates state correctly`() = runTest {
        viewModel.onIntent(BookingFilterContract.Intent.SearchQueryChanged("Paris"))
        assertEquals("Paris", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `TypeSelected updates state correctly`() = runTest {
        viewModel.onIntent(BookingFilterContract.Intent.TypeSelected("Flights"))
        assertEquals("Flights", viewModel.uiState.value.selectedType)
    }

    @Test
    fun `StatusSelected updates state correctly`() = runTest {
        viewModel.onIntent(BookingFilterContract.Intent.StatusSelected("Confirmed"))
        assertEquals("Confirmed", viewModel.uiState.value.selectedStatus)
    }

    @Test
    fun `DateSelected updates state correctly`() = runTest {
        viewModel.onIntent(BookingFilterContract.Intent.DateSelected("2025-01-01"))
        assertEquals("2025-01-01", viewModel.uiState.value.selectedDate)
    }

    @Test
    fun `ProviderSelected updates state correctly`() = runTest {
        viewModel.onIntent(BookingFilterContract.Intent.ProviderSelected("Airline"))
        assertEquals("Airline", viewModel.uiState.value.selectedProvider)
    }

    @Test
    fun `ResetClicked resets state to initial`() = runTest {
        viewModel.onIntent(BookingFilterContract.Intent.SearchQueryChanged("Paris"))
        viewModel.onIntent(BookingFilterContract.Intent.ResetClicked)
        assertEquals("", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `ApplyClicked sends FiltersApplied effect`() = runTest {
        viewModel.onIntent(BookingFilterContract.Intent.SearchQueryChanged("Paris"))
        viewModel.effect.test {
            viewModel.onIntent(BookingFilterContract.Intent.ApplyClicked)
            val effect = awaitItem() as BookingFilterContract.Effect.FiltersApplied
            assertEquals("Paris", effect.state.searchQuery)
        }
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(BookingFilterContract.Intent.BackClicked)
            assertEquals(BookingFilterContract.Effect.NavigateBack, awaitItem())
        }
    }
}
