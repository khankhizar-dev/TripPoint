package com.android.trippoint.itinerary.list

import app.cash.turbine.test
import com.android.trippoint.itinerary.domain.model.TripDay
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripDaysViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ItineraryRepository = mockk()
    private lateinit var viewModel: TripDaysViewModel

    private val dummyDay = TripDay("1", "trip1", 1, "2026-08-29", "Day 1", null, "now", "now")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TripDaysViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTripDays updates state with days`() = runTest {
        coEvery { repository.getItineraryDays("trip1") } returns Result.success(listOf(dummyDay))
        
        viewModel.uiState.test {
            assertEquals(0, awaitItem().days.size)

            viewModel.onIntent(TripDaysContract.Intent.LoadTripDays("trip1"))
            
            assertTrue(awaitItem().isLoading)
            
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(1, finalState.days.size)
            assertEquals("Day 1", finalState.days.first().title)
        }
    }

    @Test
    fun `dayClicked sends navigation effect`() = runTest {
        coEvery { repository.getItineraryDays("trip1") } returns Result.success(listOf(dummyDay))
        viewModel.onIntent(TripDaysContract.Intent.LoadTripDays("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.effect.test {
            viewModel.onIntent(TripDaysContract.Intent.DayClicked("1"))
            assertEquals(TripDaysContract.Effect.NavigateToDayTimeline("trip1", "2026-08-29"), awaitItem())
        }
    }

    @Test
    fun `backClicked sends back effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(TripDaysContract.Intent.BackClicked)
            assertEquals(TripDaysContract.Effect.NavigateBack, awaitItem())
        }
    }
}
