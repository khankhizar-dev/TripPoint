package com.android.trippoint.checklist.progress

import app.cash.turbine.test
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChecklistProgressViewModelTest {

    private val repository: ChecklistRepository = mockk()
    private lateinit var viewModel: ChecklistProgressViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val dummyChecklist = Checklist(
        id = "c1", tripId = "trip1", title = "P", description = null,
        dateRange = "", totalItems = 10, completedItems = 5,
        createdAt = "", updatedAt = ""
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChecklistProgressViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProgress success updates state`() = runTest {
        coEvery { repository.getChecklist("trip1", "c1") } returns Result.success(dummyChecklist)

        viewModel.onIntent(ChecklistProgressContract.Intent.LoadProgress("trip1", "c1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0.5f, state.checklist?.progress)
        }
    }

    @Test
    fun `viewCompletedClicked sends NavigateToCompleted effect`() = runTest {
        coEvery { repository.getChecklist("trip1", "c1") } returns Result.success(dummyChecklist)

        viewModel.onIntent(ChecklistProgressContract.Intent.LoadProgress("trip1", "c1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(ChecklistProgressContract.Intent.ViewCompletedClicked)

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is ChecklistProgressContract.Effect.NavigateToCompleted)
            assertEquals("c1", (effect as ChecklistProgressContract.Effect.NavigateToCompleted).checklistId)
        }
    }
}
