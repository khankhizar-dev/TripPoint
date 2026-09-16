package com.android.trippoint.checklist.add_item

import app.cash.turbine.test
import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.checklist.domain.model.ChecklistItemCategory
import com.android.trippoint.checklist.domain.model.ChecklistPriority
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.trip.domain.repository.TripRepository
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
class AddChecklistItemViewModelTest {

    private val repository: ChecklistRepository = mockk()
    private val tripRepository: TripRepository = mockk()
    private lateinit var viewModel: AddChecklistItemViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { tripRepository.getTripMembers(any()) } returns Result.success(emptyList())
        viewModel = AddChecklistItemViewModel(repository, tripRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveItem success sends ItemAdded effect`() = runTest {
        val dummyItem = ChecklistItem(id = "i1", sectionId = "s1", name = "Test")
        coEvery { 
            repository.addItem(any(), any(), any(), "Test", any(), any(), any(), any(), any()) 
        } returns Result.success(dummyItem)

        viewModel.onIntent(AddChecklistItemContract.Intent.LoadIds("t1", "c1", "s1"))
        viewModel.onIntent(AddChecklistItemContract.Intent.NameChanged("Test"))
        viewModel.onIntent(AddChecklistItemContract.Intent.NotesChanged("Notes"))
        viewModel.onIntent(AddChecklistItemContract.Intent.EssentialToggled(true))
        
        viewModel.onIntent(AddChecklistItemContract.Intent.SaveClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            assertEquals(AddChecklistItemContract.Effect.ItemAdded, awaitItem())
        }
    }

    @Test
    fun `input changes update state`() = runTest {
        viewModel.onIntent(AddChecklistItemContract.Intent.NameChanged("New Name"))
        viewModel.onIntent(AddChecklistItemContract.Intent.CategoryChanged("CLOTHING"))
        viewModel.onIntent(AddChecklistItemContract.Intent.PriorityChanged(ChecklistPriority.HIGH))
        viewModel.onIntent(AddChecklistItemContract.Intent.RemindMeToggled(true))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("New Name", state.name)
            assertEquals(ChecklistItemCategory.CLOTHING, state.category)
            assertEquals(ChecklistPriority.HIGH, state.priority)
            assertEquals(true, state.remindMe)
        }
    }

    @Test
    fun `saveItem blank name shows error`() = runTest {
        viewModel.onIntent(AddChecklistItemContract.Intent.NameChanged(""))
        viewModel.onIntent(AddChecklistItemContract.Intent.SaveClicked)

        viewModel.uiState.test {
            assertEquals("Item name is required", awaitItem().error)
        }
    }
}
