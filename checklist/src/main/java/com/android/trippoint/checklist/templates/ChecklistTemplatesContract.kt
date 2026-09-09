package com.android.trippoint.checklist.templates

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ChecklistTemplatesContract {
    sealed class Intent : UiIntent {
        object LoadTemplates : Intent()
        data class TemplateClicked(val id: String) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val templates: List<Template> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    data class Template(
        val id: String,
        val titleResId: Int,
        val itemCount: Int,
        val imageUrl: String
    )

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToCreate(val templateId: String) : Effect()
    }
}
