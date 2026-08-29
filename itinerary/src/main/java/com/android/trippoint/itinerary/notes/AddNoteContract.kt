package com.android.trippoint.itinerary.notes

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class AddNoteContract {
    sealed class Intent : UiIntent {
        data class LoadTripId(val tripId: String) : Intent()
        data class TitleChanged(val title: String) : Intent()
        data class ContentChanged(val content: String) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val title: String = "",
        val content: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object NoteAdded : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
