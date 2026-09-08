package com.android.trippoint.trip.overview

import app.cash.turbine.test
import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.core.network.BudgetRemoteDataSource
import com.android.trippoint.core.network.ItineraryRemoteDataSource
import com.android.trippoint.trip.domain.repository.TripRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripOverviewViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TripRepository = mockk()
    private val budgetRemoteDataSource: BudgetRemoteDataSource = mockk()
    private val itineraryRemoteDataSource: ItineraryRemoteDataSource = mockk()
    private lateinit var viewModel: TripOverviewViewModel

    private val dummyTrip = Trip(
        id = "1", ownerId = "u1", title = "Test", location = "Loc",
        startDate = "2025-01-01", endDate = "2025-01-10",
        status = TripStatus.UPCOMING, imageUrl = "", progress = 0.5f
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { budgetRemoteDataSource.getBudgetSummary(any()) } returns null
        coEvery { itineraryRemoteDataSource.getItineraryDays(any()) } returns emptyList()
        viewModel = TripOverviewViewModel(repository, budgetRemoteDataSource, itineraryRemoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default`() {
        val state = viewModel.uiState.value
        assertNull(state.trip)
        assertEquals(0, state.selectedTab)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `LoadTripDetails success updates state`() = runTest {
        coEvery { repository.getTrip("1") } returns Result.success(dummyTrip)
        coEvery { repository.getTripMembers("1") } returns Result.success(emptyList())

        viewModel.onIntent(TripOverviewContract.Intent.LoadTripDetails("1"))
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals("1", state.trip?.id)
        assertNull(state.error)
    }

    @Test
    fun `TabSelected intent updates tab and sends effect`() = runTest {
        coEvery { repository.getTrip("1") } returns Result.success(dummyTrip)
        coEvery { repository.getTripMembers("1") } returns Result.success(emptyList())
        viewModel.onIntent(TripOverviewContract.Intent.LoadTripDetails("1"))
        runCurrent()

        viewModel.effect.test {
            viewModel.onIntent(TripOverviewContract.Intent.TabSelected(1))
            assertEquals(1, viewModel.uiState.value.selectedTab)
            assertEquals(TripOverviewContract.Effect.NavigateToTimeline("1"), awaitItem())
        }
    }
}
