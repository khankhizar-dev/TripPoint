package com.android.trippoint.itinerary.notes

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.itinerary.domain.model.TripNote

class NotesContract {
    sealed class Intent : UiIntent {
        data class LoadNotes(val tripId: String) : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        object AddNoteClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val notes: List<TripNote> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToAddNote(val tripId: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
