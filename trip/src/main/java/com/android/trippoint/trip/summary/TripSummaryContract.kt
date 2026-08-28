package com.android.trippoint.trip.summary

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.core.common.model.Trip

class TripSummaryContract {
    sealed class Intent : UiIntent {
        data class LoadTrip(val tripId: String) : Intent()
        object CreateTripClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val trip: Trip? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateToHome : Effect()
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
