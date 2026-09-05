package com.android.trippoint.trip.list

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.core.common.model.Trip

class TripListContract {
    sealed class Intent : UiIntent {
        object LoadTrips : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class TabSelected(val tabIndex: Int) : Intent()
        data class TripClicked(val tripId: String) : Intent()
        object CreateTripClicked : Intent()
    }

    data class State(
        val trips: List<Trip> = emptyList(),
        val filteredTrips: List<Trip> = emptyList(),
        val searchQuery: String = "",
        val selectedTab: Int = 0,
        val isLoading: Boolean = false,
        val isOffline: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        data class NavigateToTripDetails(val tripId: String) : Effect()
        object NavigateToCreateTrip : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
