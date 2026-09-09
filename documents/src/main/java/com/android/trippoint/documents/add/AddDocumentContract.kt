package com.android.trippoint.documents.add

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.documents.domain.model.DocumentType

class AddDocumentContract {
    sealed class Intent : UiIntent {
        data class LoadTripId(val tripId: String) : Intent()
        data class TitleChanged(val value: String) : Intent()
        data class TypeChanged(val value: DocumentType) : Intent()
        data class ExpiryChanged(val value: String) : Intent()
        data class RefChanged(val value: String) : Intent()
        data class FileSelected(val url: String) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val title: String = "",
        val type: DocumentType = DocumentType.PASSPORT_VISA,
        val expiryDate: String = "",
        val referenceNumber: String = "",
        val fileUrl: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val errorResId: Int? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object DocumentAdded : Effect()
    }
}
