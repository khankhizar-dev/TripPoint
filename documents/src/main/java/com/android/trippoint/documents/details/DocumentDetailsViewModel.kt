package com.android.trippoint.documents.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.documents.domain.repository.DocumentRepository
import com.android.trippoint.core.designsystem.R as designR
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
            is DocumentDetailsContract.Intent.LoadDocument -> loadDocument(intent.tripId, intent.id)
            DocumentDetailsContract.Intent.BackClicked -> sendEffect(DocumentDetailsContract.Effect.NavigateBack)
            DocumentDetailsContract.Intent.ShareClicked -> {
                sendEffect(DocumentDetailsContract.Effect.ShowMessageResId(designR.string.documents_share_coming_soon))
            }
            DocumentDetailsContract.Intent.DownloadClicked -> {
                val resId = designR.string.documents_download_coming_soon
                sendEffect(DocumentDetailsContract.Effect.ShowMessageResId(resId))
            }
            DocumentDetailsContract.Intent.FavoriteClicked -> toggleFavorite()
            DocumentDetailsContract.Intent.DeleteClicked -> trashDocument()
        }
    }

    private fun loadDocument(tripId: String, id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId) }
            val result = repository.getDocument(tripId, id)
            if (result.isSuccess) {
                setState { copy(isLoading = false, document = result.getOrNull(), error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun toggleFavorite() {
        val tripId = uiState.value.tripId
        val doc = uiState.value.document ?: return
        viewModelScope.launch {
            val result = repository.toggleFavorite(tripId, doc.id, !doc.isFavorite)
            if (result.isSuccess) {
                val updatedDoc = result.getOrThrow()
                setState { copy(document = updatedDoc) }
                sendEffect(DocumentDetailsContract.Effect.ShowMessageResId(
                    if (updatedDoc.isFavorite) designR.string.documents_added_favorites 
                    else designR.string.documents_removed_favorites
                ))
            }
        }
    }

    private fun trashDocument() {
        val tripId = uiState.value.tripId
        val doc = uiState.value.document ?: return
        viewModelScope.launch {
            val result = repository.trashDocument(tripId, doc.id)
            if (result.isSuccess) {
                sendEffect(DocumentDetailsContract.Effect.NavigateBack)
            }
        }
    }
}
