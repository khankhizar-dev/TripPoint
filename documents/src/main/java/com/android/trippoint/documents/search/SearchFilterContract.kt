package com.android.trippoint.documents.search

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.documents.domain.model.DocumentType

class SearchFilterContract {
    sealed class Intent : UiIntent {
        data class SearchQueryChanged(val query: String) : Intent()
        data class CategorySelected(val type: DocumentType?) : Intent()
        data class ExpirySelected(val period: String) : Intent()
        data class IssuerChanged(val issuer: String) : Intent()
        object ApplyFilters : Intent()
        object ResetFilters : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val searchQuery: String = "",
        val selectedCategory: DocumentType? = null,
        val selectedExpiryPeriod: String = "All",
        val issuedBy: String = "",
        val isLoading: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class FiltersApplied(val state: State) : Effect()
    }
}
