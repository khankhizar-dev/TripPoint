package com.android.trippoint.itinerary.filter

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class FilterSortContract {
    sealed class Intent : UiIntent {
        data class LoadFilter(val tripId: String) : Intent()
        data class ToggleCategory(val category: String) : Intent()
        data class SortByChanged(val sortBy: String) : Intent()
        object ApplyClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val selectedCategories: Set<String> = emptySet(),
        val sortBy: String = "Time",
        val categories: List<String> = listOf("Events", "Tasks", "Notes"),
        val sortOptions: List<String> = listOf("Time", "Priority", "Category")
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
    }
}
