package com.android.trippoint.checklist.progress

import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ChecklistProgressContract {
    sealed class Intent : UiIntent {
        data class LoadProgress(val tripId: String, val checklistId: String) : Intent()
        object ViewCompletedClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val checklist: Checklist? = null,
        val sections: List<ChecklistSection> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToCompleted(val tripId: String, val checklistId: String) : Effect()
    }
}
