package com.android.trippoint.documents.details

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.documents.domain.model.Document

class DocumentDetailsContract {
    sealed class Intent : UiIntent {
        data class LoadDocument(val id: String) : Intent()
        object BackClicked : Intent()
        object ShareClicked : Intent()
        object DownloadClicked : Intent()
        object FavoriteClicked : Intent()
        object DeleteClicked : Intent()
    }

    data class State(
        val document: Document? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowMessage(val message: String) : Effect()
    }
}
