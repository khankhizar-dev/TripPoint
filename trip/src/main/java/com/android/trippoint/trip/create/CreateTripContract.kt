package com.android.trippoint.trip.create

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class CreateTripContract {
    sealed class Intent : UiIntent {
        data class NameChanged(val name: String) : Intent()
        data class DestinationChanged(val destination: String) : Intent()
        data class StartDateChanged(val date: String) : Intent()
        data class EndDateChanged(val date: String) : Intent()
        object NextClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val name: String = "",
        val destination: String = "",
        val startDate: String = "",
        val endDate: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        data class NavigateToAddDetails(val tripId: String) : Effect()
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
