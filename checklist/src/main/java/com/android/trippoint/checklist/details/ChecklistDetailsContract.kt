package com.android.trippoint.checklist.details

import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ChecklistDetailsContract {
    sealed class Intent : UiIntent {
        data class LoadChecklist(val id: String) : Intent()
        object BackClicked : Intent()
        data class SectionClicked(val id: String) : Intent()
        object AddSectionClicked : Intent()
        object AddItemClicked : Intent()
        object AiSuggestClicked : Intent()
        object ProgressClicked : Intent()
    }

    data class State(
        val checklist: Checklist? = null,
        val sections: List<ChecklistSection> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToSectionDetails(val id: String) : Effect()
        data class NavigateToProgress(val checklistId: String) : Effect()
        object NavigateToAddSection : Effect()
        object NavigateToAddItem : Effect()
        object NavigateToAiSuggest : Effect()
    }
}
