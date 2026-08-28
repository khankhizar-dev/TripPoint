package com.android.trippoint.trip.summary

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
class TripSummaryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TripRepository = mockk()
    private lateinit var viewModel: TripSummaryViewModel

    private val dummyTrip = Trip(
        id = "1",
        ownerId = "owner1",
        title = "Bali",
        location = "Indonesia",
        startDate = "2025-01-01",
        endDate = "2025-01-10",
        status = TripStatus.DRAFT,
        imageUrl = "",
        progress = 0f
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TripSummaryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load trip updates state`() = runTest {
        coEvery { repository.getTrip("1") } returns Result.success(dummyTrip)

        viewModel.uiState.test {
            assertEquals(null, awaitItem().trip) // Initial
            
            viewModel.onIntent(TripSummaryContract.Intent.LoadTrip("1"))
            
            assertEquals(true, awaitItem().isLoading)
            
            val successState = awaitItem()
            assertEquals("1", successState.trip?.id)
            assertEquals(false, successState.isLoading)
        }
    }

    @Test
    fun `create trip click finalizes and navigates`() = runTest {
        coEvery { repository.getTrip("1") } returns Result.success(dummyTrip)
        coEvery { repository.updateTrip(any(), status = any()) } returns Result.success(dummyTrip)

        viewModel.onIntent(TripSummaryContract.Intent.LoadTrip("1"))
        
        viewModel.effect.test {
            viewModel.onIntent(TripSummaryContract.Intent.CreateTripClicked)
            assertEquals(TripSummaryContract.Effect.NavigateToHome, awaitItem())
        }
    }
}
