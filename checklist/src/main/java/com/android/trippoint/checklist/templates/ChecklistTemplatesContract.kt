package com.android.trippoint.checklist.templates

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ChecklistTemplatesContract {
    sealed class Intent : UiIntent {
        data class LoadTemplates(val tripId: String) : Intent()
        data class TemplateClicked(val id: String, val name: String) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val templates: List<Template> = emptyList(),
        val isLoading: Boolean = false,
        val isCreating: Boolean = false,
        val error: String? = null
    ) : UiState

    data class Template(
        val id: String,
        val title: String,
        val itemCount: Int,
        val imageUrl: String
    )

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToDetails(val tripId: String, val checklistId: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
