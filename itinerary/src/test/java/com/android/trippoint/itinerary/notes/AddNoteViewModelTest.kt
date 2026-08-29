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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddNoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ItineraryRepository = mockk()
    private lateinit var viewModel: AddNoteViewModel

    private val dummyDay = TripDay("d1", "t1", 1, "2026-08-29", "Day 1", null, "now", "now")
    private val dummyEvent = TimelineEvent(
        "a1", "d1", "Note", "Content", EventType.ACTIVITY, "00:00", null, null, null, null, 1, false, "now", "now"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddNoteViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveNote sends noteAdded effect on success`() = runTest {
        coEvery { repository.getItineraryDays("t1") } returns Result.success(listOf(dummyDay))
        coEvery { repository.createItineraryActivity(any(), any(), any()) } returns Result.success(dummyEvent)
        
        viewModel.onIntent(AddNoteContract.Intent.LoadTripId("t1"))
        viewModel.onIntent(AddNoteContract.Intent.TitleChanged("Note 1"))
        viewModel.onIntent(AddNoteContract.Intent.ContentChanged("Content 1"))
        
        viewModel.effect.test {
            viewModel.onIntent(AddNoteContract.Intent.SaveClicked)
            assertEquals(AddNoteContract.Effect.NoteAdded, awaitItem())
        }
    }
}
