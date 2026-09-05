package com.android.trippoint.booking.filter

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class BookingFilterContract {
    sealed class Intent : UiIntent {
        object BackClicked : Intent()
        object ApplyClicked : Intent()
        object ResetClicked : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class TypeSelected(val type: String) : Intent()
        data class StatusSelected(val status: String) : Intent()
        data class DateSelected(val date: String) : Intent()
        data class ProviderSelected(val provider: String) : Intent()
    }

    data class State(
        val searchQuery: String = "",
        val selectedType: String = "All",
        val selectedStatus: String = "All",
        val selectedDate: String = "",
        val selectedProvider: String = "All",
        val types: List<String> = listOf("All", "Flights", "Hotels", "Activities"),
        val statuses: List<String> = listOf("All", "Confirmed", "Pending", "Cancelled")
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class FiltersApplied(val state: State) : Effect()
    }
}
