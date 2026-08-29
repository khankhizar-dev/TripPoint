package com.android.trippoint.itinerary.day

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.itinerary.domain.model.TimelineEvent

class TimelineContract {
    sealed class Intent : UiIntent {
        data class LoadTimeline(val tripId: String, val date: String) : Intent()
        object AddEventClicked : Intent()
        object AddTaskClicked : Intent()
        object AddNoteClicked : Intent()
        object FilterClicked : Intent()
        data class EventClicked(val eventId: String) : Intent()
        data class ToggleEventCompletion(val eventId: String, val completed: Boolean) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val dayId: String = "",
        val events: List<TimelineEvent> = emptyList(),
        val selectedDate: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToEventDetails(val tripId: String, val dayId: String, val eventId: String) : Effect()
        data class NavigateToAddEvent(val tripId: String, val date: String) : Effect()
        data class NavigateToAddTask(val tripId: String, val date: String) : Effect()
        data class NavigateToAddNote(val tripId: String) : Effect()
        data class NavigateToFilter(val tripId: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
