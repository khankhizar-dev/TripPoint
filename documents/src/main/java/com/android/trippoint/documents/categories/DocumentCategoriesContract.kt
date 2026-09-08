package com.android.trippoint.documents.categories

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.documents.domain.model.CategoryInfo
import com.android.trippoint.documents.domain.model.DocumentType

class DocumentCategoriesContract {
    sealed class Intent : UiIntent {
        data class LoadCategories(val tripId: String) : Intent()
        data class CategoryClicked(val type: DocumentType) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val categories: List<CategoryInfo> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToDocumentsByType(val type: DocumentType) : Effect()
    }
}
