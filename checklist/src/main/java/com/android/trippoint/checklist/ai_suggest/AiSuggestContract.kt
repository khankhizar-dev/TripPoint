package com.android.trippoint.checklist.ai_suggest

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class AiSuggestContract {
    sealed class Intent : UiIntent {
        data class LoadSuggestions(val tripId: String) : Intent()
        data class ItemToggled(val itemId: String) : Intent()
        object AddAllClicked : Intent()
        object RegenerateClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val location: String = "",
        val month: String = "",
        val suggestions: List<SuggestedItem> = emptyList(),
        val isLoading: Boolean = false,
        val isAdding: Boolean = false,
        val error: String? = null
    ) : UiState

    data class SuggestedItem(
        val id: String,
        val name: String,
        val isSelected: Boolean = true
    )

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object ItemsAdded : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
