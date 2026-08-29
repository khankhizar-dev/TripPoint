package com.android.trippoint.itinerary.day

import app.cash.turbine.test
import com.android.trippoint.itinerary.domain.model.EventType
import com.android.trippoint.itinerary.domain.model.TimelineEvent
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
class TimelineViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ItineraryRepository = mockk()
    private lateinit var viewModel: TimelineViewModel

    private val dummyDay = TripDay("d1", "trip1", 1, "2026-08-29", "Day 1", null, "now", "now")
    private val dummyEvent = TimelineEvent(
        "a1", "d1", "Flight", null, EventType.FLIGHT, "08:00", "10:00", "LHR", null, null, 1, false, "now", "now"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TimelineViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTimeline updates state with events`() = runTest {
        coEvery { repository.getItineraryDays("trip1") } returns Result.success(listOf(dummyDay))
        coEvery { repository.getItineraryActivities("trip1", "d1") } returns Result.success(listOf(dummyEvent))
        
        viewModel.uiState.test {
            // Initial state
            assertEquals("", awaitItem().tripId)

            viewModel.onIntent(TimelineContract.Intent.LoadTimeline("trip1", "2026-08-29"))
            
            // State 1: after LoadTimeline intent (immediate setState for tripId and selectedDate)
            val stateAfterIntent = awaitItem()
            assertEquals("trip1", stateAfterIntent.tripId)
            assertFalse(stateAfterIntent.isLoading)

            // State 2: loading = true from loadTimeline launch
            assertTrue(awaitItem().isLoading)

            // State 3: dayId set
            val stateWithDayId = awaitItem()
            assertEquals("d1", stateWithDayId.dayId)
            assertTrue(stateWithDayId.isLoading)

            // State 4: Final state
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(1, finalState.events.size)
            assertEquals("Flight", finalState.events.first().title)
        }
    }

    @Test
    fun `eventClicked sends navigation effect`() = runTest {
        coEvery { repository.getItineraryDays("trip1") } returns Result.success(listOf(dummyDay))
        coEvery { repository.getItineraryActivities("trip1", "d1") } returns Result.success(listOf(dummyEvent))
        
        viewModel.onIntent(TimelineContract.Intent.LoadTimeline("trip1", "2026-08-29"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(TimelineContract.Intent.EventClicked("a1"))
            assertEquals(TimelineContract.Effect.NavigateToEventDetails("trip1", "d1", "a1"), awaitItem())
        }
    }

    @Test
    fun `toggleEventCompletion reloads timeline`() = runTest {
        coEvery { repository.getItineraryDays("trip1") } returns Result.success(listOf(dummyDay))
        coEvery { repository.getItineraryActivities("trip1", "d1") } returns Result.success(listOf(dummyEvent))
        coEvery { repository.markItineraryActivityCompleted("trip1", "d1", "a1", true) } returns Result.success(true)
        
        viewModel.onIntent(TimelineContract.Intent.LoadTimeline("trip1", "2026-08-29"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(TimelineContract.Intent.ToggleEventCompletion("a1", true))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("d1", viewModel.uiState.value.dayId)
    }
}
