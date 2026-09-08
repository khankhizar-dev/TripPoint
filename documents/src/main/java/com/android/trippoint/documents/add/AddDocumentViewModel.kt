package com.android.trippoint.documents.add

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.documents.domain.repository.DocumentRepository
import kotlinx.coroutines.launch
import java.io.File

class AddDocumentViewModel(
    private val repository: DocumentRepository,
) : BaseViewModel<
    AddDocumentContract.State,
    AddDocumentContract.Intent,
    AddDocumentContract.Effect
>(
    AddDocumentContract.State()
) {
    override fun onIntent(intent: AddDocumentContract.Intent) {
        when (intent) {
            is AddDocumentContract.Intent.LoadTripId -> setState { copy(tripId = intent.tripId) }
            is AddDocumentContract.Intent.TitleChanged -> setState { copy(title = intent.value) }
            is AddDocumentContract.Intent.TypeChanged -> setState { copy(type = intent.value) }
            is AddDocumentContract.Intent.ExpiryChanged -> setState { copy(expiryDate = intent.value) }
            is AddDocumentContract.Intent.RefChanged -> setState { copy(referenceNumber = intent.value) }
            is AddDocumentContract.Intent.FileSelected -> setState { copy(fileUrl = intent.url) }
            AddDocumentContract.Intent.SaveClicked -> saveDocument()
            AddDocumentContract.Intent.BackClicked -> sendEffect(AddDocumentContract.Effect.NavigateBack)
        }
    }

    private fun saveDocument() {
        val state = uiState.value
        if (state.title.isBlank() || state.fileUrl == null) {
            setState { copy(error = "Title and file are required") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val file = File(state.fileUrl)
            val result = repository.uploadDocument(
                tripId = state.tripId,
                name = state.title,
                category = state.type,
                source = "DEVICE",
                file = file,
                documentNumber = state.referenceNumber.takeIf { it.isNotBlank() },
                expiryDate = state.expiryDate.takeIf { it.isNotBlank() }
            )
            if (result.isSuccess) {
                setState { copy(isLoading = false) }
                sendEffect(AddDocumentContract.Effect.DocumentAdded)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
