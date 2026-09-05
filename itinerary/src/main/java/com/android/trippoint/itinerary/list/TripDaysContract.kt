package com.android.trippoint.itinerary.list

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.itinerary.domain.model.TripDay

class TripDaysContract {
    sealed class Intent : UiIntent {
        data class LoadTripDays(val tripId: String) : Intent()
        data class DayClicked(val dayId: String) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val days: List<TripDay> = emptyList(),
        val selectedMonth: String = "May 2025",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToDayTimeline(val tripId: String, val date: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
