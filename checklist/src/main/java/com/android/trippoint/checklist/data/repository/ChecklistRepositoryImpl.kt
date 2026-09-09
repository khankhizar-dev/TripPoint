package com.android.trippoint.checklist.data.repository

import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.checklist.domain.model.ChecklistItemCategory
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.checklist.domain.model.ChecklistStatus
import com.android.trippoint.checklist.domain.model.ChecklistTemplate
import com.android.trippoint.checklist.domain.model.TemplateItem
import com.android.trippoint.checklist.domain.model.TemplateSection
import com.android.trippoint.checklist.domain.model.TemplateType
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.core.network.ChecklistDto
import com.android.trippoint.core.network.ChecklistItemDto
import com.android.trippoint.core.network.ChecklistRemoteDataSource
import com.android.trippoint.core.network.ChecklistSectionDto
import com.android.trippoint.core.network.ChecklistTemplateDto
import com.android.trippoint.core.network.ChecklistTemplateItemDto
import com.android.trippoint.core.network.ChecklistTemplateSectionDto
import com.android.trippoint.core.network.CreateChecklistFromTemplateInput
import com.android.trippoint.core.network.CreateChecklistInput
import com.android.trippoint.core.network.CreateChecklistItemInput
import com.android.trippoint.core.network.CreateChecklistSectionInput
import com.android.trippoint.core.network.CreateChecklistTemplateInput
import com.android.trippoint.core.network.SaveChecklistAsTemplateInput
import com.android.trippoint.core.network.UpdateChecklistInput
import com.android.trippoint.core.network.UpdateChecklistItemInput
import com.android.trippoint.core.network.UpdateChecklistSectionInput
import com.android.trippoint.core.network.UpdateChecklistTemplateInput

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
        val input = CreateChecklistSectionInput(name = name)
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
        val result = if (isCompletedOnly(name, category, isEssential, isCompleted, dueDate)) {
            remoteDataSource.completeChecklistItem(tripId, checklistId, sectionId, itemId, isCompleted!!)
        } else {
            val input = UpdateChecklistItemInput(
                name = name,
                category = category?.name,
                essential = isEssential,
                completed = isCompleted,
                dueDate = dueDate
            )
            remoteDataSource.updateChecklistItem(tripId, checklistId, sectionId, itemId, input)
        }
        result?.toDomain() ?: throw Exception("Failed to update item")
    }

    private fun isCompletedOnly(
        name: String?, 
        category: ChecklistItemCategory?, 
        isEssential: Boolean?, 
        isCompleted: Boolean?, 
        dueDate: String?
    ): Boolean {
        return isCompleted != null && name == null && category == null && isEssential == null && dueDate == null
    }

    override suspend fun deleteItem(
        tripId: String, 
        checklistId: String, 
        sectionId: String, 
        itemId: String
    ): Result<Boolean> = runCatching {
        remoteDataSource.deleteChecklistItem(tripId, checklistId, sectionId, itemId)
    }

    override suspend fun getTemplates(): Result<List<ChecklistTemplate>> = runCatching {
        remoteDataSource.getChecklistTemplates().map { it.toDomain() }
    }

    override suspend fun getTemplate(templateId: String): Result<ChecklistTemplate> = runCatching {
        remoteDataSource.getChecklistTemplate(templateId)?.toDomain() ?: throw Exception("Template not found")
    }

    override suspend fun createTemplate(name: String, description: String?): Result<ChecklistTemplate> = runCatching {
        val input = CreateChecklistTemplateInput(name = name, description = description)
        remoteDataSource.createChecklistTemplate(input)?.toDomain() ?: throw Exception("Failed to create template")
    }

    override suspend fun updateTemplate(
        templateId: String, 
        name: String?, 
        description: String?, 
        status: String?
    ): Result<ChecklistTemplate> = runCatching {
        val input = UpdateChecklistTemplateInput(name = name, description = description, status = status)
        val result = remoteDataSource.updateChecklistTemplate(templateId, input)
        result?.toDomain() ?: throw Exception("Failed to update template")
    }

    override suspend fun archiveTemplate(templateId: String): Result<ChecklistTemplate> = runCatching {
        val result = remoteDataSource.archiveChecklistTemplate(templateId)
        result?.toDomain() ?: throw Exception("Failed to archive template")
    }

    override suspend fun saveAsTemplate(
        tripId: String, 
        checklistId: String, 
        name: String, 
        description: String?
    ): Result<ChecklistTemplate> = runCatching {
        val input = SaveChecklistAsTemplateInput(name = name, description = description)
        val result = remoteDataSource.saveChecklistAsTemplate(tripId, checklistId, input)
        result?.toDomain() ?: throw Exception("Failed to save as template")
    }

    override suspend fun createFromTemplate(
        tripId: String, 
        templateId: String, 
        name: String?, 
        description: String?
    ): Result<Checklist> = runCatching {
        val input = CreateChecklistFromTemplateInput(name = name, description = description)
        val result = remoteDataSource.createChecklistFromTemplate(tripId, templateId, input)
        result?.toDomain() ?: throw Exception("Failed to create checklist from template")
    }

    private fun ChecklistDto.toDomain() = Checklist(
        id = id,
        tripId = tripId,
        title = name,
        description = description,
        dateRange = updatedAt,
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

    private fun ChecklistTemplateDto.toDomain() = ChecklistTemplate(
        id = id,
        name = name,
        description = description,
        type = TemplateType.valueOf(type),
        status = status,
        sections = sections?.map { it.toDomain() } ?: emptyList(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun ChecklistTemplateSectionDto.toDomain() = TemplateSection(
        id = id,
        templateId = templateId,
        title = name,
        position = position,
        items = items?.map { it.toDomain() } ?: emptyList()
    )

    private fun ChecklistTemplateItemDto.toDomain() = TemplateItem(
        id = id,
        sectionId = sectionId,
        name = name,
        category = ChecklistItemCategory.valueOf(category),
        isEssential = essential,
        position = position
    )
}
