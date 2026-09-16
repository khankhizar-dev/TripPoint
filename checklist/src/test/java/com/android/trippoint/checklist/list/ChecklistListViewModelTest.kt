package com.android.trippoint.checklist.list

import app.cash.turbine.test
import com.android.trippoint.checklist.domain.model.Checklist
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
class ChecklistListViewModelTest {

    private val repository: ChecklistRepository = mockk()
    private lateinit var viewModel: ChecklistListViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val dummyChecklist = Checklist(
        id = "1",
        tripId = "trip1",
        title = "Packing",
        description = null,
        dateRange = "Just now",
        totalItems = 2,
        completedItems = 0,
        status = ChecklistStatus.ACTIVE,
        createdAt = "",
        updatedAt = ""
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChecklistListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadChecklists success updates state`() = runTest {
        coEvery { repository.getChecklists("trip1") } returns Result.success(listOf(dummyChecklist))

        viewModel.onIntent(ChecklistListContract.Intent.LoadChecklists("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.checklists.size)
            assertEquals("Packing", state.checklists[0].title)
        }
    }

    @Test
    fun `tabSelected filters checklists`() = runTest {
        val lists = listOf(
            dummyChecklist,
            dummyChecklist.copy(id = "2", status = ChecklistStatus.COMPLETED)
        )
        coEvery { repository.getChecklists("trip1") } returns Result.success(lists)

        viewModel.onIntent(ChecklistListContract.Intent.LoadChecklists("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(ChecklistListContract.Intent.TabSelected(1)) // Active
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(1, awaitItem().filteredChecklists.size)
        }
    }

    @Test
    fun `archiveChecklist success reloads list`() = runTest {
        coEvery { repository.archiveChecklist("trip1", "1") } returns Result.success(dummyChecklist)
        coEvery { repository.getChecklists("trip1") } returns Result.success(emptyList())

        viewModel.onIntent(ChecklistListContract.Intent.LoadChecklists("trip1"))
        viewModel.onIntent(ChecklistListContract.Intent.ArchiveChecklist("1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(0, awaitItem().checklists.size)
        }
    }
}
