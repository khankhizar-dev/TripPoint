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
            DocumentListContract.Intent.LoadDocuments -> loadDocuments()
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
            DocumentListContract.Intent.BackClicked -> sendEffect(DocumentListContract.Effect.NavigateBack)
        }
    }

    private fun loadDocuments() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getDocuments()
            val recentResult = repository.getDocuments(isRecent = true)
            
            if (result.isSuccess) {
                allDocuments = result.getOrDefault(emptyList())
                val recentDocs = recentResult.getOrDefault(emptyList())
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
            1 -> filtered // Recent tab - in a real app this might be different, but for now we'll just show all
            2 -> filtered.filter { it.isFavorite }
            else -> filtered
        }
        
        setState { copy(documents = filtered) }
    }

    private fun toggleFavorite(id: String) {
        viewModelScope.launch {
            val result = repository.toggleFavorite(id)
            if (result.isSuccess) {
                loadDocuments() // Refresh
            }
        }
    }
}
