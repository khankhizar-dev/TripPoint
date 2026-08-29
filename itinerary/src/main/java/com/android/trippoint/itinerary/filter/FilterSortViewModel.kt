package com.android.trippoint.itinerary.filter

import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository

class FilterSortViewModel(
    private val repository: ItineraryRepository
) : BaseViewModel<
    FilterSortContract.State,
    FilterSortContract.Intent,
    FilterSortContract.Effect
>(
    FilterSortContract.State()
) {
    override fun onIntent(intent: FilterSortContract.Intent) {
        when (intent) {
            is FilterSortContract.Intent.LoadFilter -> setState { copy(tripId = intent.tripId) }
            is FilterSortContract.Intent.ToggleCategory -> {
                val current = uiState.value.selectedCategories.toMutableSet()
                if (current.contains(intent.category)) {
                    current.remove(intent.category)
                } else {
                    current.add(intent.category)
                }
                setState { copy(selectedCategories = current) }
            }
            is FilterSortContract.Intent.SortByChanged -> setState { copy(sortBy = intent.sortBy) }
            FilterSortContract.Intent.ApplyClicked -> sendEffect(FilterSortContract.Effect.NavigateBack)
            FilterSortContract.Intent.BackClicked -> sendEffect(FilterSortContract.Effect.NavigateBack)
        }
    }
}
