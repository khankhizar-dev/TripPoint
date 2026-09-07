package com.android.trippoint.documents.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.documents.domain.repository.DocumentRepository
import kotlinx.coroutines.launch

class DocumentDetailsViewModel(
    private val repository: DocumentRepository
) : BaseViewModel<
    DocumentDetailsContract.State,
    DocumentDetailsContract.Intent,
    DocumentDetailsContract.Effect
>(
    DocumentDetailsContract.State()
) {
    override fun onIntent(intent: DocumentDetailsContract.Intent) {
        when (intent) {
            is DocumentDetailsContract.Intent.LoadDocument -> loadDocument(intent.id)
            DocumentDetailsContract.Intent.BackClicked -> sendEffect(DocumentDetailsContract.Effect.NavigateBack)
            DocumentDetailsContract.Intent.ShareClicked -> {
                sendEffect(DocumentDetailsContract.Effect.ShowMessage("Share functionality coming soon"))
            }
            DocumentDetailsContract.Intent.DownloadClicked -> {
                sendEffect(DocumentDetailsContract.Effect.ShowMessage("Download functionality coming soon"))
            }
            DocumentDetailsContract.Intent.FavoriteClicked -> toggleFavorite()
            DocumentDetailsContract.Intent.DeleteClicked -> deleteDocument()
        }
    }

    private fun loadDocument(id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getDocument(id)
            if (result.isSuccess) {
                setState { copy(isLoading = false, document = result.getOrNull(), error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun toggleFavorite() {
        val doc = uiState.value.document ?: return
        viewModelScope.launch {
            val result = repository.toggleFavorite(doc.id)
            if (result.isSuccess) {
                val updatedDoc = doc.copy(isFavorite = result.getOrDefault(doc.isFavorite))
                setState { copy(document = updatedDoc) }
                sendEffect(DocumentDetailsContract.Effect.ShowMessage(
                    if (updatedDoc.isFavorite) "Added to favorites" else "Removed from favorites"
                ))
            }
        }
    }

    private fun deleteDocument() {
        val doc = uiState.value.document ?: return
        viewModelScope.launch {
            val result = repository.deleteDocument(doc.id)
            if (result.isSuccess) {
                sendEffect(DocumentDetailsContract.Effect.NavigateBack)
            }
        }
    }
}
