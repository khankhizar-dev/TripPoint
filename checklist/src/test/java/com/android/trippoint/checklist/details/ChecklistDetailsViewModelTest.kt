package com.android.trippoint.checklist.details

import app.cash.turbine.test
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.checklist.domain.model.ChecklistStatus
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChecklistDetailsViewModelTest {

    private val repository: ChecklistRepository = mockk()
    private lateinit var viewModel: ChecklistDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val dummySection = ChecklistSection(
        id = "sec1", checklistId = "1", title = "Clothes", position = 0,
        totalItems = 1, completedItems = 0
    )
    private val dummyChecklist = Checklist(
        id = "1", tripId = "trip1", title = "Packing", description = null,
        dateRange = "Just now", totalItems = 1, completedItems = 0,
        status = ChecklistStatus.ACTIVE, sections = listOf(dummySection),
        createdAt = "", updatedAt = ""
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChecklistDetailsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadChecklist success updates state`() = runTest {
        coEvery { repository.getChecklist("trip1", "1") } returns Result.success(dummyChecklist)

        viewModel.onIntent(ChecklistDetailsContract.Intent.LoadChecklist("trip1", "1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Packing", state.checklist?.title)
            assertEquals(1, state.sections.size)
        }
    }

    @Test
    fun `addSection success reloads checklist`() = runTest {
        coEvery { repository.getChecklist("trip1", "1") } returns Result.success(dummyChecklist)
        coEvery { repository.createSection("trip1", "1", "Food", any()) } returns Result.success(dummySection)

        viewModel.onIntent(ChecklistDetailsContract.Intent.LoadChecklist("trip1", "1"))
        viewModel.onIntent(ChecklistDetailsContract.Intent.AddSection("Food"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals("Packing", awaitItem().checklist?.title)
        }
    }
}
