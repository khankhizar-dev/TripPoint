package com.android.trippoint.checklist.data.repository

import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.checklist.domain.model.ChecklistItemCategory
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.checklist.domain.model.ChecklistStatus
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.core.network.ChecklistDto
import com.android.trippoint.core.network.ChecklistItemDto
import com.android.trippoint.core.network.ChecklistRemoteDataSource
import com.android.trippoint.core.network.ChecklistSectionDto
import com.android.trippoint.core.network.CreateChecklistInput
import com.android.trippoint.core.network.CreateChecklistItemInput
import com.android.trippoint.core.network.CreateChecklistSectionInput
import com.android.trippoint.core.network.UpdateChecklistInput
import com.android.trippoint.core.network.UpdateChecklistItemInput
import com.android.trippoint.core.network.UpdateChecklistSectionInput

class ChecklistRepositoryImpl(
    private val remoteDataSource: ChecklistRemoteDataSource
) : ChecklistRepository {

    override suspend fun getChecklists(tripId: String): Result<List<Checklist>> = runCatching {
        remoteDataSource.getChecklists(tripId).map { it.toDomain() }
    }

    override suspend fun getChecklist(tripId: String, checklistId: String): Result<Checklist> = runCatching {
        remoteDataSource.getChecklist(tripId, checklistId)?.toDomain() ?: throw Exception("Checklist not found")
    }

    override suspend fun createChecklist(tripId: String, name: String, description: String?): Result<Checklist> = 
        runCatching {
            val input = CreateChecklistInput(name = name, description = description)
            remoteDataSource.createChecklist(tripId, input)?.toDomain() ?: throw Exception("Failed to create checklist")
        }

    override suspend fun updateChecklist(
        tripId: String, 
        checklistId: String, 
        name: String?, 
        description: String?, 
        status: ChecklistStatus?
    ): Result<Checklist> = runCatching {
        val input = UpdateChecklistInput(
            name = name, 
            description = description, 
            status = status?.name
        )
        val result = remoteDataSource.updateChecklist(tripId, checklistId, input)
        result?.toDomain() ?: throw Exception("Failed to update checklist")
    }

    override suspend fun archiveChecklist(tripId: String, checklistId: String): Result<Checklist> = runCatching {
        val result = remoteDataSource.archiveChecklist(tripId, checklistId)
        result?.toDomain() ?: throw Exception("Failed to archive checklist")
    }

    override suspend fun createSection(
        tripId: String, 
        checklistId: String, 
        name: String, 
        position: Int?
    ): Result<ChecklistSection> = runCatching {
        val input = CreateChecklistSectionInput(name = name, position = position)
        val result = remoteDataSource.createChecklistSection(tripId, checklistId, input)
        result?.toDomain() ?: throw Exception("Failed to create section")
    }

    override suspend fun updateSection(
        tripId: String, 
        checklistId: String, 
        sectionId: String, 
        name: String?, 
        position: Int?
    ): Result<ChecklistSection> = runCatching {
        val input = UpdateChecklistSectionInput(name = name, position = position)
        val result = remoteDataSource.updateChecklistSection(tripId, checklistId, sectionId, input)
        result?.toDomain() ?: throw Exception("Failed to update section")
    }

    override suspend fun deleteSection(
        tripId: String, 
        checklistId: String, 
        sectionId: String
    ): Result<Boolean> = runCatching {
        remoteDataSource.deleteChecklistSection(tripId, checklistId, sectionId)
    }

    override suspend fun addItem(
        tripId: String,
        checklistId: String,
        sectionId: String,
        name: String,
        category: ChecklistItemCategory,
        isEssential: Boolean,
        dueDate: String?
    ): Result<ChecklistItem> = runCatching {
        val input = CreateChecklistItemInput(
            name = name,
            category = category.name,
            essential = isEssential,
            dueDate = dueDate
        )
        val result = remoteDataSource.addChecklistItem(tripId, checklistId, sectionId, input)
        result?.toDomain() ?: throw Exception("Failed to add item")
    }

    override suspend fun updateItem(
        tripId: String,
        checklistId: String,
        sectionId: String,
        itemId: String,
        name: String?,
        category: ChecklistItemCategory?,
        isEssential: Boolean?,
        isCompleted: Boolean?,
        dueDate: String?
    ): Result<ChecklistItem> = runCatching {
        val input = UpdateChecklistItemInput(
            name = name,
            category = category?.name,
            essential = isEssential,
            completed = isCompleted,
            dueDate = dueDate
        )
        val result = remoteDataSource.updateChecklistItem(tripId, checklistId, sectionId, itemId, input)
        result?.toDomain() ?: throw Exception("Failed to update item")
    }

    private fun ChecklistDto.toDomain() = Checklist(
        id = id,
        tripId = tripId,
        title = name,
        description = description,
        dateRange = updatedAt, // Assuming fallback if dateRange not provided by API
        totalItems = totalItems,
        completedItems = completedItems,
        status = ChecklistStatus.valueOf(status),
        sections = sections?.map { it.toDomain() } ?: emptyList(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun ChecklistSectionDto.toDomain() = ChecklistSection(
        id = id,
        checklistId = checklistId,
        title = name,
        position = position,
        totalItems = totalItems,
        completedItems = completedItems,
        items = items?.map { it.toDomain() } ?: emptyList()
    )

    private fun ChecklistItemDto.toDomain() = ChecklistItem(
        id = id,
        sectionId = sectionId,
        name = name,
        isCompleted = completed,
        category = ChecklistItemCategory.valueOf(category),
        isEssential = essential,
        dueDate = dueDate,
        position = position
    )
}
