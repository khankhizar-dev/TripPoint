package com.android.trippoint.documents.search

import com.android.trippoint.core.common.BaseViewModel

class SearchFilterViewModel : BaseViewModel<
    SearchFilterContract.State,
    SearchFilterContract.Intent,
    SearchFilterContract.Effect
>(
    SearchFilterContract.State()
) {
    override fun onIntent(intent: SearchFilterContract.Intent) {
        when (intent) {
            is SearchFilterContract.Intent.SearchQueryChanged -> setState { copy(searchQuery = intent.query) }
            is SearchFilterContract.Intent.CategorySelected -> setState { copy(selectedCategory = intent.type) }
            is SearchFilterContract.Intent.ExpirySelected -> setState { copy(selectedExpiryPeriod = intent.period) }
            is SearchFilterContract.Intent.IssuerChanged -> setState { copy(issuedBy = intent.issuer) }
            SearchFilterContract.Intent.ApplyFilters -> {
                sendEffect(SearchFilterContract.Effect.FiltersApplied(uiState.value))
            }
            SearchFilterContract.Intent.ResetFilters -> setState { SearchFilterContract.State() }
            SearchFilterContract.Intent.BackClicked -> sendEffect(SearchFilterContract.Effect.NavigateBack)
        }
    }
}
