package com.android.trippoint.itinerary.details

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.itinerary.domain.model.TimelineEvent

class EventDetailsContract {
    sealed class Intent : UiIntent {
        data class LoadEventDetails(val tripId: String, val dayId: String, val activityId: String) : Intent()
        object BackClicked : Intent()
        object EditClicked : Intent()
        object ShareClicked : Intent()
        object DeleteClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val dayId: String = "",
        val activityId: String = "",
        val event: TimelineEvent? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
