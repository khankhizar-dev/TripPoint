package com.android.trippoint.itinerary.notes

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
class NotesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ItineraryRepository = mockk()
    private lateinit var viewModel: NotesViewModel

    private val dummyDay = TripDay("d1", "t1", 1, "2026-08-29", "Day 1", null, "now", "now")
    private val dummyEvent = TimelineEvent(
        "a1", "d1", "Note", "Content", EventType.ACTIVITY, "00:00", null, null, null, null, 1, false, "now", "now"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NotesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNotes updates state with notes`() = runTest {
        coEvery { repository.getItineraryDays("t1") } returns Result.success(listOf(dummyDay))
        coEvery { repository.getItineraryActivities("t1", "d1") } returns Result.success(listOf(dummyEvent))
        
        viewModel.uiState.test {
            assertEquals("", awaitItem().tripId)

            viewModel.onIntent(NotesContract.Intent.LoadNotes("t1"))
            
            assertEquals("t1", awaitItem().tripId)
            assertTrue(awaitItem().isLoading)
            
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(1, finalState.notes.size)
            assertEquals("Note", finalState.notes.first().title)
        }
    }

    @Test
    fun `addNoteClicked sends navigation effect`() = runTest {
        coEvery { repository.getItineraryDays("t1") } returns Result.success(listOf(dummyDay))
        coEvery { repository.getItineraryActivities("t1", "d1") } returns Result.success(listOf(dummyEvent))
        
        viewModel.onIntent(NotesContract.Intent.LoadNotes("t1"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.effect.test {
            viewModel.onIntent(NotesContract.Intent.AddNoteClicked)
            assertEquals(NotesContract.Effect.NavigateToAddNote("t1"), awaitItem())
        }
    }
}
