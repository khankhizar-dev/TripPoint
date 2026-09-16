package com.android.trippoint.checklist.templates

import app.cash.turbine.test
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistStatus
import com.android.trippoint.checklist.domain.model.ChecklistTemplate
import com.android.trippoint.checklist.domain.model.TemplateType
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
class ChecklistTemplatesViewModelTest {

    private val repository: ChecklistRepository = mockk()
    private lateinit var viewModel: ChecklistTemplatesViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val dummyTemplate = ChecklistTemplate(
        id = "t1", name = "Weekend", description = null, type = TemplateType.SYSTEM,
        status = "ACTIVE", sections = emptyList(), createdAt = "", updatedAt = ""
    )
    private val dummyChecklist = Checklist(
        id = "c1", tripId = "trip1", title = "P", description = null,
        dateRange = "", totalItems = 0, completedItems = 0,
        status = ChecklistStatus.ACTIVE, createdAt = "", updatedAt = ""
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChecklistTemplatesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTemplates success updates state with blank option and results`() = runTest {
        coEvery { repository.getTemplates() } returns Result.success(listOf(dummyTemplate))

        viewModel.onIntent(ChecklistTemplatesContract.Intent.LoadTemplates("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.templates.size)
            assertEquals("blank", state.templates[0].id)
            assertEquals("Weekend", state.templates[1].title)
        }
    }

    @Test
    fun `templateClicked for blank creates blank checklist`() = runTest {
        coEvery { repository.getTemplates() } returns Result.success(emptyList())
        coEvery { repository.createChecklist(any(), any(), any()) } returns Result.success(dummyChecklist)

        viewModel.onIntent(ChecklistTemplatesContract.Intent.LoadTemplates("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onIntent(ChecklistTemplatesContract.Intent.TemplateClicked("blank", "Blank"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is ChecklistTemplatesContract.Effect.NavigateToDetails)
            assertEquals("c1", (effect as ChecklistTemplatesContract.Effect.NavigateToDetails).checklistId)
        }
    }

    @Test
    fun `templateClicked for real template creates from template`() = runTest {
        coEvery { repository.getTemplates() } returns Result.success(listOf(dummyTemplate))
        coEvery { repository.createFromTemplate(any(), "t1", any(), any()) } returns Result.success(dummyChecklist)

        viewModel.onIntent(ChecklistTemplatesContract.Intent.LoadTemplates("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onIntent(ChecklistTemplatesContract.Intent.TemplateClicked("t1", "Weekend"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is ChecklistTemplatesContract.Effect.NavigateToDetails)
        }
    }
}
