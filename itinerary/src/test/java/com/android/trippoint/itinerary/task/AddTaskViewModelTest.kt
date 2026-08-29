package com.android.trippoint.itinerary.task

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
class AddTaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ItineraryRepository = mockk()
    private lateinit var viewModel: AddTaskViewModel

    private val dummyDay = TripDay("d1", "t1", 1, "2026-08-29", "Day 1", null, "now", "now")
    private val dummyEvent = TimelineEvent(
        "a1", "d1", "Task", null, EventType.ACTIVITY, "08:00", "10:00", null, null, null, 1, false, "now", "now"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddTaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveTask sends taskAdded effect on success`() = runTest {
        coEvery { repository.getItineraryDays("t1") } returns Result.success(listOf(dummyDay))
        coEvery { repository.createItineraryActivity(any(), any(), any()) } returns Result.success(dummyEvent)
        
        viewModel.onIntent(AddTaskContract.Intent.LoadTripInfo("t1", "2026-08-29"))
        viewModel.onIntent(AddTaskContract.Intent.NameChanged("Task 1"))
        
        viewModel.effect.test {
            viewModel.onIntent(AddTaskContract.Intent.SaveClicked)
            assertEquals(AddTaskContract.Effect.TaskAdded, awaitItem())
        }
    }
}
