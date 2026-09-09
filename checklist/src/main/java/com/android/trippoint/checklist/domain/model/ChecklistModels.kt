package com.android.trippoint.checklist.domain.model

data class Checklist(
    val id: String,
    val tripId: String,
    val title: String,
    val description: String?,
    val dateRange: String,
    val totalItems: Int,
    val completedItems: Int,
    val status: ChecklistStatus = ChecklistStatus.ACTIVE,
    val sections: List<ChecklistSection> = emptyList(),
    val createdAt: String,
    val updatedAt: String
) {
    val progress: Float
        get() = if (totalItems > 0) completedItems.toFloat() / totalItems else 0f
}

enum class ChecklistStatus {
    ACTIVE,
    COMPLETED,
    ARCHIVED
}

data class ChecklistSection(
    val id: String,
    val checklistId: String,
    val title: String,
    val position: Int,
    val totalItems: Int,
    val completedItems: Int,
    val items: List<ChecklistItem> = emptyList()
)

data class ChecklistItem(
    val id: String,
    val sectionId: String,
    val name: String,
    val isCompleted: Boolean = false,
    val category: ChecklistItemCategory = ChecklistItemCategory.OTHER,
    val isEssential: Boolean = false,
    val dueDate: String? = null,
    val position: Int = 0,
    val notes: String? = null
)

enum class ChecklistItemCategory {
    CLOTHING,
    DOCUMENTS,
    ELECTRONICS,
    HEALTH,
    TOILETRIES,
    OTHER
}
