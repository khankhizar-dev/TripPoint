package com.android.trippoint.documents.list

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.documents.domain.model.Document
import com.android.trippoint.documents.domain.model.DocumentType

class DocumentListContract {
    sealed class Intent : UiIntent {
        data class LoadDocuments(val tripId: String) : Intent()
        data class TabSelected(val index: Int) : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class DocumentClicked(val id: String) : Intent()
        data class ToggleFavorite(val id: String) : Intent()
        object ViewAllRecentClicked : Intent()
        data class CategoryClicked(val type: DocumentType) : Intent()
        object AddDocumentClicked : Intent()
        data class DocumentLongClicked(val id: String) : Intent()
        object ClearSelection : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val documents: List<Document> = emptyList(),
        val recentDocuments: List<Document> = emptyList(),
        val selectedTab: Int = 0,
        val searchQuery: String = "",
        val selectedDocumentIds: Set<String> = emptySet(),
        val isSelectionMode: Boolean = false,
        val isOffline: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToDetails(val id: String) : Effect()
        object NavigateToCategories : Effect()
        object NavigateToAddDocument : Effect()
        data class ShowMessage(val message: String) : Effect()
    }
}
