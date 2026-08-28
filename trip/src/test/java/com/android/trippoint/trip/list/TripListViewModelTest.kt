package com.android.trippoint.trip.list

import app.cash.turbine.test
import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripStatus
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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TripRepository = mockk()
    private lateinit var viewModel: TripListViewModel

    private val dummyTrip = Trip(
        id = "1",
        ownerId = "owner1",
        title = "Bali",
        location = "Indonesia",
        startDate = "2025-01-01",
        endDate = "2025-01-10",
        status = TripStatus.UPCOMING,
        imageUrl = "",
        progress = 0f
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getTrips(any(), any()) } returns Result.success(listOf(dummyTrip))
        viewModel = TripListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load fetches trips`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.trips.size)
        }
    }

    @Test
    fun `search query change reloads trips`() = runTest {
        viewModel.onIntent(TripListContract.Intent.SearchQueryChanged("Paris"))
        assertEquals("Paris", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `tab selection reloads trips`() = runTest {
        viewModel.onIntent(TripListContract.Intent.TabSelected(1))
        assertEquals(1, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `trip click sends navigation effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(TripListContract.Intent.TripClicked("1"))
            assertEquals(TripListContract.Effect.NavigateToTripDetails("1"), awaitItem())
        }
    }

    @Test
    fun `create trip click sends navigation effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(TripListContract.Intent.CreateTripClicked)
            assertEquals(TripListContract.Effect.NavigateToCreateTrip, awaitItem())
        }
    }
}
