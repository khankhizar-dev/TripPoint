package com.android.trippoint.itinerary.add

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
class AddEventViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ItineraryRepository = mockk()
    private lateinit var viewModel: AddEventViewModel

    private val dummyDay = TripDay("d1", "t1", 1, "2026-08-29", "Day 1", null, "now", "now")
    private val dummyEvent = TimelineEvent(
        "a1", "d1", "Flight", null, EventType.FLIGHT, "08:00", "10:00", "LHR", null, null, 1, false, "now", "now"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddEventViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveEvent sends eventAdded effect on success`() = runTest {
        coEvery { repository.getItineraryDays("t1") } returns Result.success(listOf(dummyDay))
        coEvery { repository.createItineraryActivity(any(), any(), any()) } returns Result.success(dummyEvent)
        
        viewModel.onIntent(AddEventContract.Intent.LoadTripInfo("t1", "2026-08-29"))
        viewModel.onIntent(AddEventContract.Intent.NameChanged("Flight"))
        viewModel.onIntent(AddEventContract.Intent.CategoryChanged("Flight"))
        
        viewModel.effect.test {
            viewModel.onIntent(AddEventContract.Intent.SaveClicked)
            assertEquals(AddEventContract.Effect.EventAdded, awaitItem())
        }
    }

    @Test
    fun `saveEvent creates day if not exists`() = runTest {
        coEvery { repository.getItineraryDays("t1") } returns Result.success(emptyList())
        coEvery { repository.createItineraryDay(any(), any(), any(), any(), any()) } returns Result.success(dummyDay)
        coEvery { repository.createItineraryActivity(any(), any(), any()) } returns Result.success(dummyEvent)
        
        viewModel.onIntent(AddEventContract.Intent.LoadTripInfo("t1", "2026-08-29"))
        viewModel.onIntent(AddEventContract.Intent.NameChanged("Flight"))
        
        viewModel.effect.test {
            viewModel.onIntent(AddEventContract.Intent.SaveClicked)
            assertEquals(AddEventContract.Effect.EventAdded, awaitItem())
        }
    }
}
