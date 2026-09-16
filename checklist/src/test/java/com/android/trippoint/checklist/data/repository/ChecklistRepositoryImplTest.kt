package com.android.trippoint.checklist.data.repository

import com.android.trippoint.checklist.domain.model.ChecklistItemCategory
import com.android.trippoint.checklist.domain.model.ChecklistPriority
import com.android.trippoint.core.network.ChecklistDto
import com.android.trippoint.core.network.ChecklistItemDto
import com.android.trippoint.core.network.ChecklistRemoteDataSource
import com.android.trippoint.core.network.ChecklistSectionDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChecklistRepositoryImplTest {

    private val remoteDataSource: ChecklistRemoteDataSource = mockk()
    private lateinit var repository: ChecklistRepositoryImpl

    private val dummyChecklistDto = ChecklistDto(
        id = "1",
        tripId = "trip1",
        createdBy = "user1",
        name = "Packing",
        description = null,
        status = "ACTIVE",
        totalItems = 2,
        completedItems = 1,
        progress = 50,
        createdAt = "2026-09-13T10:00:00Z",
        updatedAt = "2026-09-13T10:05:00Z"
    )

    private val dummyItemDto = ChecklistItemDto(
        id = "item1",
        sectionId = "sec1",
        name = "Shirt",
        category = "CLOTHING",
        essential = true,
        completed = false,
        dueDate = null,
        position = 0,
        priority = "HIGH"
    )

    @Before
    fun setUp() {
        repository = ChecklistRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getChecklists returns domain models`() = runTest {
        coEvery { remoteDataSource.getChecklists("trip1") } returns listOf(dummyChecklistDto)

        val result = repository.getChecklists("trip1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Packing", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `getChecklist returns mapped sections and items`() = runTest {
        val sectionDto = ChecklistSectionDto(
            id = "sec1", checklistId = "1", name = "Clothes", position = 0,
            totalItems = 1, completedItems = 0, progress = 0, items = listOf(dummyItemDto)
        )
        val fullChecklistDto = dummyChecklistDto.copy(sections = listOf(sectionDto))
        coEvery { remoteDataSource.getChecklist("trip1", "1") } returns fullChecklistDto

        val result = repository.getChecklist("trip1", "1")

        assertTrue(result.isSuccess)
        val checklist = result.getOrNull()
        assertEquals(1, checklist?.sections?.size)
        assertEquals(ChecklistPriority.HIGH, checklist?.sections?.first()?.items?.first()?.priority)
    }

    @Test
    fun `addItem returns domain model`() = runTest {
        coEvery { remoteDataSource.addChecklistItem("trip1", "1", "sec1", any()) } returns dummyItemDto

        val result = repository.addItem(
            "trip1", "1", "sec1", "Shirt", ChecklistItemCategory.CLOTHING, true, null
        )

        assertTrue(result.isSuccess)
        assertEquals("Shirt", result.getOrNull()?.name)
    }

    @Test
    fun `updateItem success`() = runTest {
        coEvery { remoteDataSource.updateChecklistItem("trip1", "1", "sec1", "item1", any()) } returns dummyItemDto

        val result = repository.updateItem(
            "trip1", "1", "sec1", "item1", name = "New Name"
        )

        assertTrue(result.isSuccess)
        assertEquals("Shirt", result.getOrNull()?.name)
    }

    @Test
    fun `archiveChecklist returns mapped model`() = runTest {
        coEvery { remoteDataSource.archiveChecklist("trip1", "1") } returns dummyChecklistDto

        val result = repository.archiveChecklist("trip1", "1")

        assertTrue(result.isSuccess)
        assertEquals("1", result.getOrNull()?.id)
    }
}
