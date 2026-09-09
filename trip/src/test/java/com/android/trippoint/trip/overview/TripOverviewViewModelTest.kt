package com.android.trippoint.trip.overview

import app.cash.turbine.test
import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.trip.domain.repository.TripRepository
import com.android.trippoint.trip.domain.usecase.GetTripOverviewUseCase
import com.android.trippoint.trip.domain.usecase.TripOverviewData
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
    private val getTripOverviewUseCase: GetTripOverviewUseCase = mockk()
    private lateinit var viewModel: TripOverviewViewModel

    private val dummyTrip = Trip(
        id = "1", ownerId = "u1", title = "Test", location = "Loc",
        startDate = "2025-01-01", endDate = "2025-01-10",
        status = TripStatus.UPCOMING, imageUrl = "", progress = 0.5f
    )

    private val dummyOverviewData = TripOverviewData(
        trip = dummyTrip,
        budgetSummary = "INR 1000",
        totalTasks = 5,
        completedTasks = 2
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TripOverviewViewModel(repository, getTripOverviewUseCase)
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
        coEvery { getTripOverviewUseCase("1") } returns Result.success(dummyOverviewData)

        viewModel.onIntent(TripOverviewContract.Intent.LoadTripDetails("1"))
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals("1", state.trip?.id)
        assertEquals("INR 1000", state.trip?.budget)
        assertEquals(5, state.trip?.tasksCount)
        assertNull(state.error)
    }

    @Test
    fun `TabSelected intent updates tab and sends effect`() = runTest {
        coEvery { getTripOverviewUseCase("1") } returns Result.success(dummyOverviewData)
        viewModel.onIntent(TripOverviewContract.Intent.LoadTripDetails("1"))
        runCurrent()

        viewModel.effect.test {
            viewModel.onIntent(TripOverviewContract.Intent.TabSelected(1))
            assertEquals(1, viewModel.uiState.value.selectedTab)
            assertEquals(TripOverviewContract.Effect.NavigateToTimeline("1"), awaitItem())
        }
    }
}
