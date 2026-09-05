package com.android.trippoint.itinerary.details

import app.cash.turbine.test
import com.android.trippoint.itinerary.domain.model.EventType
import com.android.trippoint.itinerary.domain.model.TimelineEvent
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
class EventDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ItineraryRepository = mockk()
    private lateinit var viewModel: EventDetailsViewModel

    private val dummyEvent = TimelineEvent(
        id = "a1",
        itineraryDayId = "d1",
        title = "Flight",
        description = "Description",
        type = EventType.FLIGHT,
        startTime = "08:00",
        endTime = "10:00",
        location = "LHR",
        latitude = null,
        longitude = null,
        sortOrder = 1,
        completed = false,
        createdAt = "now",
        updatedAt = "now"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EventDetailsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadEventDetails updates state with event`() = runTest {
        coEvery { repository.getItineraryActivity("t1", "d1", "a1") } returns Result.success(dummyEvent)
        
        viewModel.uiState.test {
            assertEquals(null, awaitItem().event)
            
            viewModel.onIntent(EventDetailsContract.Intent.LoadEventDetails("t1", "d1", "a1"))
            
            // State 1: IDs set
            val stateAfterIntent = awaitItem()
            assertEquals("a1", stateAfterIntent.activityId)
            assertFalse(stateAfterIntent.isLoading)

            // State 2: loading = true
            assertTrue(awaitItem().isLoading)

            // State 3: Final state
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals("Flight", finalState.event?.title)
        }
    }

    @Test
    fun `deleteClicked sends back effect on success`() = runTest {
        coEvery { repository.getItineraryActivity("t1", "d1", "a1") } returns Result.success(dummyEvent)
        coEvery { repository.deleteItineraryActivity("t1", "d1", "a1") } returns Result.success(true)
        
        viewModel.onIntent(EventDetailsContract.Intent.LoadEventDetails("t1", "d1", "a1"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.effect.test {
            viewModel.onIntent(EventDetailsContract.Intent.DeleteClicked)
            assertEquals(EventDetailsContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `backClicked sends back effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(EventDetailsContract.Intent.BackClicked)
            assertEquals(EventDetailsContract.Effect.NavigateBack, awaitItem())
        }
    }
}
