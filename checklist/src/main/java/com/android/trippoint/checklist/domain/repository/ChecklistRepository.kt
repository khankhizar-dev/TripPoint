package com.android.trippoint.checklist.domain.repository

import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.checklist.domain.model.ChecklistItemCategory
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.checklist.domain.model.ChecklistStatus
import com.android.trippoint.checklist.domain.model.ChecklistTemplate

interface ChecklistRepository {
    suspend fun getChecklists(tripId: String): Result<List<Checklist>>
    suspend fun getChecklist(tripId: String, checklistId: String): Result<Checklist>
    suspend fun createChecklist(tripId: String, name: String, description: String?): Result<Checklist>
    suspend fun updateChecklist(
        tripId: String, 
        checklistId: String, 
        name: String?, 
        description: String?, 
        status: ChecklistStatus?
    ): Result<Checklist>
    suspend fun archiveChecklist(tripId: String, checklistId: String): Result<Checklist>
    
    suspend fun createSection(
        tripId: String, 
        checklistId: String, 
        name: String, 
        position: Int?
    ): Result<ChecklistSection>
    
    suspend fun updateSection(
        tripId: String, 
        checklistId: String, 
        sectionId: String, 
        name: String?, 
        position: Int?
    ): Result<ChecklistSection>
    
    suspend fun deleteSection(tripId: String, checklistId: String, sectionId: String): Result<Boolean>
    
    suspend fun addItem(
        tripId: String, 
        checklistId: String, 
        sectionId: String, 
        name: String, 
        category: ChecklistItemCategory, 
        isEssential: Boolean, 
        dueDate: String?
    ): Result<ChecklistItem>
    
    suspend fun updateItem(
        tripId: String,
        checklistId: String,
        sectionId: String,
        itemId: String,
        name: String? = null,
        category: ChecklistItemCategory? = null,
        isEssential: Boolean? = null,
        isCompleted: Boolean? = null,
        dueDate: String? = null
    ): Result<ChecklistItem>

    suspend fun deleteItem(tripId: String, checklistId: String, sectionId: String, itemId: String): Result<Boolean>
    
    suspend fun getTemplates(): Result<List<ChecklistTemplate>>
    suspend fun getTemplate(templateId: String): Result<ChecklistTemplate>
    suspend fun createTemplate(name: String, description: String?): Result<ChecklistTemplate>
    suspend fun updateTemplate(
        templateId: String, 
        name: String?, 
        description: String?, 
        status: String?
    ): Result<ChecklistTemplate>
    suspend fun archiveTemplate(templateId: String): Result<ChecklistTemplate>
    
    suspend fun saveAsTemplate(
        tripId: String, 
        checklistId: String, 
        name: String, 
        description: String?
    ): Result<ChecklistTemplate>
    
    suspend fun createFromTemplate(
        tripId: String, 
        templateId: String, 
        name: String?, 
        description: String?
    ): Result<Checklist>
}
