package com.android.trippoint.checklist.items

import app.cash.turbine.test
import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import io.mockk.coEvery
import io.mockk.every
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
class ChecklistItemsViewModelTest {

    private val repository: ChecklistRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: ChecklistItemsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val dummyItem = ChecklistItem(id = "i1", sectionId = "sec1", name = "Shirt")
    private val dummySection = ChecklistSection(
        id = "sec1", checklistId = "1", title = "Clothes", position = 0,
        totalItems = 1, completedItems = 0, items = listOf(dummyItem)
    )
    private val dummyChecklist = Checklist(
        id = "1", tripId = "trip1", title = "P", description = null,
        dateRange = "", totalItems = 1, completedItems = 0,
        sections = listOf(dummySection), createdAt = "", updatedAt = ""
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.getUserId() } returns "u1"
        viewModel = ChecklistItemsViewModel(repository, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSection success updates state`() = runTest {
        coEvery { repository.getChecklist("trip1", "1") } returns Result.success(dummyChecklist)

        viewModel.onIntent(ChecklistItemsContract.Intent.LoadSection("trip1", "1", "sec1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.items.size)
            assertEquals("Shirt", state.items[0].name)
        }
    }

    @Test
    fun `itemToggled updates local state and hasChanges`() = runTest {
        coEvery { repository.getChecklist("trip1", "1") } returns Result.success(dummyChecklist)

        viewModel.onIntent(ChecklistItemsContract.Intent.LoadSection("trip1", "1", "sec1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(ChecklistItemsContract.Intent.ItemToggled("i1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(true, state.items[0].isCompleted)
            assertEquals(true, state.hasChanges)
        }
    }

    @Test
    fun `saveClicked syncs items to repository`() = runTest {
        coEvery { repository.getChecklist("trip1", "1") } returns Result.success(dummyChecklist)
        coEvery { repository.updateItem("trip1", "1", "sec1", "i1", isCompleted = true)
        } returns Result.success(dummyItem)

        viewModel.onIntent(ChecklistItemsContract.Intent.LoadSection("trip1", "1", "sec1"))
        viewModel.onIntent(ChecklistItemsContract.Intent.ItemToggled("i1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(ChecklistItemsContract.Intent.SaveClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(false, awaitItem().hasChanges)
        }
    }

    @Test
    fun `deleteItem success reloads section`() = runTest {
        coEvery { repository.deleteItem("trip1", "1", "sec1", "i1") } returns Result.success(true)
        coEvery { repository.getChecklist("trip1", "1") } returns Result.success(dummyChecklist)

        viewModel.onIntent(ChecklistItemsContract.Intent.LoadSection("trip1", "1", "sec1"))
        viewModel.onIntent(ChecklistItemsContract.Intent.DeleteItem("i1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(false, awaitItem().isLoading)
        }
    }
}
