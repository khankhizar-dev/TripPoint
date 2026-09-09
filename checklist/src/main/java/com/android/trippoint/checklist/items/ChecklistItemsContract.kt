package com.android.trippoint.checklist.items

import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ChecklistItemsContract {
    sealed class Intent : UiIntent {
        data class LoadSection(val checklistId: String, val sectionId: String) : Intent()
        data class ItemToggled(val itemId: String) : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        object AddItemClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val section: ChecklistSection? = null,
        val items: List<ChecklistItem> = emptyList(),
        val filteredItems: List<ChecklistItem> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToAddItem(val checklistId: String, val sectionId: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
