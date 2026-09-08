package com.android.trippoint.documents.list

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.documents.domain.model.Document
import com.android.trippoint.documents.domain.repository.DocumentRepository
import kotlinx.coroutines.launch

class DocumentListViewModel(
    private val repository: DocumentRepository
) : BaseViewModel<
    DocumentListContract.State,
    DocumentListContract.Intent,
    DocumentListContract.Effect
>(
    DocumentListContract.State()
) {
    private var allDocuments = emptyList<Document>()

    override fun onIntent(intent: DocumentListContract.Intent) {
        when (intent) {
            is DocumentListContract.Intent.LoadDocuments -> loadDocuments(intent.tripId)
            is DocumentListContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.index) }
                filterDocuments()
            }
            is DocumentListContract.Intent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
                filterDocuments()
            }
            is DocumentListContract.Intent.DocumentClicked -> {
                sendEffect(DocumentListContract.Effect.NavigateToDetails(intent.id))
            }
            is DocumentListContract.Intent.ToggleFavorite -> toggleFavorite(intent.id)
            DocumentListContract.Intent.ViewAllRecentClicked -> {
                sendEffect(DocumentListContract.Effect.NavigateToCategories)
            }
            is DocumentListContract.Intent.CategoryClicked -> {
                sendEffect(DocumentListContract.Effect.NavigateToCategories)
            }
            DocumentListContract.Intent.AddDocumentClicked -> {
                sendEffect(DocumentListContract.Effect.NavigateToAddDocument)
            }
            is DocumentListContract.Intent.DocumentLongClicked -> toggleSelection(intent.id)
            DocumentListContract.Intent.ClearSelection -> setState {
                copy(selectedDocumentIds = emptySet(), isSelectionMode = false)
            }
            DocumentListContract.Intent.BackClicked -> {
                if (uiState.value.isSelectionMode) {
                    onIntent(DocumentListContract.Intent.ClearSelection)
                } else {
                    sendEffect(DocumentListContract.Effect.NavigateBack)
                }
            }
        }
    }

    private fun toggleSelection(id: String) {
        val currentSelected = uiState.value.selectedDocumentIds
        val newSelected = if (currentSelected.contains(id)) {
            currentSelected - id
        } else {
            currentSelected + id
        }
        setState {
            copy(
                selectedDocumentIds = newSelected,
                isSelectionMode = newSelected.isNotEmpty()
            )
        }
    }

    private fun loadDocuments(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId) }
            val result = repository.getDocuments(tripId)
            
            if (result.isSuccess) {
                allDocuments = result.getOrDefault(emptyList())
                // Sort by updatedAt for recent documents
                val recentDocs = allDocuments.sortedByDescending { it.updatedAt }.take(5)
                setState { 
                    copy(
                        isLoading = false, 
                        recentDocuments = recentDocs,
                        error = null
                    ) 
                }
                filterDocuments()
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun filterDocuments() {
        val query = uiState.value.searchQuery
        val tab = uiState.value.selectedTab
        
        var filtered = allDocuments.filter { 
            it.title.contains(query, ignoreCase = true) 
        }
        
        filtered = when (tab) {
            0 -> filtered // All
            1 -> filtered // Recent - in this UI we just show list, top section handles recent
            2 -> filtered.filter { it.isFavorite }
            else -> filtered
        }
        
        setState { copy(documents = filtered) }
    }

    private fun toggleFavorite(id: String) {
        viewModelScope.launch {
            val tripId = uiState.value.tripId
            val doc = allDocuments.find { it.id == id } ?: return@launch
            val result = repository.toggleFavorite(tripId, id, !doc.isFavorite)
            if (result.isSuccess) {
                loadDocuments(tripId) // Refresh
            }
        }
    }
}
