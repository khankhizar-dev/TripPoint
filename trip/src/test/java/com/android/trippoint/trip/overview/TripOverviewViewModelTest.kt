package com.android.trippoint.trip.overview

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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripOverviewViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TripRepository = mockk()
    private lateinit var viewModel: TripOverviewViewModel

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
        viewModel = TripOverviewViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load trip details updates state`() = runTest {
        coEvery { repository.getTrip("1") } returns Result.success(dummyTrip)
        coEvery { repository.getTripMembers("1") } returns Result.success(emptyList())

        viewModel.uiState.test {
            assertEquals(null, awaitItem().trip) // Initial state
            
            viewModel.onIntent(TripOverviewContract.Intent.LoadTripDetails("1"))
            
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            
            val successState = awaitItem()
            assertEquals("1", successState.trip?.id)
            assertEquals(false, successState.isLoading)
        }
    }

    @Test
    fun `tab selection updates state`() {
        viewModel.onIntent(TripOverviewContract.Intent.TabSelected(1))
        assertEquals(1, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `back click sends effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(TripOverviewContract.Intent.BackClicked)
            assertEquals(TripOverviewContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `update status reloads trip`() = runTest {
        coEvery { repository.getTrip(any()) } returns Result.success(dummyTrip)
        coEvery { repository.getTripMembers(any()) } returns Result.success(emptyList())
        coEvery { repository.updateTrip(any(), status = any()) } returns Result.success(dummyTrip)

        viewModel.onIntent(TripOverviewContract.Intent.UpdateStatus(TripStatus.IN_PROGRESS))
        // Verify state is updated or reloaded
    }
}
