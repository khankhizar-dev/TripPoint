package com.android.trippoint.trip.details

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class AddDetailsContract {
    sealed class Intent : UiIntent {
        data class LoadTrip(val tripId: String) : Intent()
        object SaveAndContinueClicked : Intent()
        object BackClicked : Intent()
        data class ToggleSection(val sectionId: String) : Intent()
    }

    data class State(
        val tripId: String = "",
        val sections: List<DetailSection> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    data class DetailSection(
        val id: String,
        val title: String,
        val isAdded: Boolean = false
    )

    sealed class Effect : UiEffect {
        object NavigateToHome : Effect()
        object NavigateBack : Effect()
        data class NavigateToItinerary(val tripId: String) : Effect()
        data class NavigateToTasks(val tripId: String) : Effect()
        data class NavigateToNotes(val tripId: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
