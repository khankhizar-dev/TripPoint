package com.android.trippoint.trip.collaboration.activity

import app.cash.turbine.test
import com.android.trippoint.trip.collaboration.domain.model.ActivityLog
import com.android.trippoint.trip.collaboration.domain.model.ActivityTarget
import com.android.trippoint.trip.collaboration.domain.repository.CollaborationRepository
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
class ActivityFeedViewModelTest {

    private val repository: CollaborationRepository = mockk()
    private lateinit var viewModel: ActivityFeedViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val dummyActivity = ActivityLog(
        id = "1",
        tripId = "trip1",
        userId = "user1",
        userName = "Rohan",
        userPhotoUrl = null,
        action = "updated task",
        targetType = ActivityTarget.TASK,
        targetName = "Book flights",
        timestamp = "10:30 AM"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ActivityFeedViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadFeed success updates state`() = runTest {
        coEvery { repository.getActivityLogs("trip1") } returns Result.success(listOf(dummyActivity))

        viewModel.onIntent(ActivityFeedContract.Intent.LoadFeed("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.activities.size)
            assertEquals("updated task", state.activities[0].action)
        }
    }

    @Test
    fun `loadFeed failure updates error`() = runTest {
        coEvery { repository.getActivityLogs("trip1") } returns Result.failure(Exception("Network Error"))

        viewModel.onIntent(ActivityFeedContract.Intent.LoadFeed("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals("Network Error", awaitItem().error)
        }
    }

    @Test
    fun `backClicked sends NavigateBack effect`() = runTest {
        viewModel.onIntent(ActivityFeedContract.Intent.BackClicked)

        viewModel.effect.test {
            assertEquals(ActivityFeedContract.Effect.NavigateBack, awaitItem())
        }
    }
}
