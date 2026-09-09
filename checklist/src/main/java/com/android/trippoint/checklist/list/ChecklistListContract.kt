package com.android.trippoint.checklist.list

import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ChecklistListContract {
    sealed class Intent : UiIntent {
        data class LoadChecklists(val tripId: String) : Intent()
        data class TabSelected(val index: Int) : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class ChecklistClicked(val id: String) : Intent()
        object CreateChecklistClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val checklists: List<Checklist> = emptyList(),
        val filteredChecklists: List<Checklist> = emptyList(),
        val selectedTab: Int = 0,
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToDetails(val tripId: String, val id: String) : Effect()
        data class NavigateToCreate(val tripId: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
