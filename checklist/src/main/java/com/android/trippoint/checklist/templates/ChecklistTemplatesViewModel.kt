package com.android.trippoint.checklist.templates

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class ChecklistTemplatesViewModel(
    private val repository: ChecklistRepository
) : BaseViewModel<
    ChecklistTemplatesContract.State,
    ChecklistTemplatesContract.Intent,
    ChecklistTemplatesContract.Effect
>(
    ChecklistTemplatesContract.State()
) {
    override fun onIntent(intent: ChecklistTemplatesContract.Intent) {
        when (intent) {
            is ChecklistTemplatesContract.Intent.LoadTemplates -> {
                setState { copy(tripId = intent.tripId) }
                loadTemplates()
            }
            is ChecklistTemplatesContract.Intent.TemplateClicked -> {
                if (intent.id == "blank") {
                    createBlankChecklist()
                } else {
                    createChecklistFromTemplate(intent.id, intent.name)
                }
            }
            ChecklistTemplatesContract.Intent.BackClicked -> sendEffect(ChecklistTemplatesContract.Effect.NavigateBack)
        }
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getTemplates()
            
            val templates = mutableListOf<ChecklistTemplatesContract.Template>()
            
            // Add Blank Option
            templates.add(
                ChecklistTemplatesContract.Template(
                    id = "blank",
                    title = "Blank Checklist",
                    itemCount = 0,
                    imageUrl = "https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b"
                )
            )

            if (result.isSuccess) {
                result.getOrDefault(emptyList()).forEach { template ->
                    templates.add(
                        ChecklistTemplatesContract.Template(
                            id = template.id,
                            title = template.name,
                            itemCount = template.sections.sumOf { it.items.size },
                            imageUrl = getPlaceholderImage(template.name)
                        )
                    )
                }
                setState { copy(isLoading = false, templates = templates) }
            } else {
                setState { copy(isLoading = false, templates = templates, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun createBlankChecklist() {
        viewModelScope.launch {
            setState { copy(isCreating = true) }
            val tripId = uiState.value.tripId
            val result = repository.createChecklist(
                tripId = tripId,
                name = "New Checklist",
                description = "Custom checklist"
            )
            
            if (result.isSuccess) {
                val checklist = result.getOrThrow()
                setState { copy(isCreating = false) }
                sendEffect(ChecklistTemplatesContract.Effect.NavigateToDetails(tripId, checklist.id))
            } else {
                setState { copy(isCreating = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun createChecklistFromTemplate(templateId: String, name: String) {
        viewModelScope.launch {
            setState { copy(isCreating = true) }
            val tripId = uiState.value.tripId
            val result = repository.createFromTemplate(
                tripId = tripId,
                templateId = templateId,
                name = name,
                description = "Created from template: $name"
            )
            
            if (result.isSuccess) {
                val checklist = result.getOrThrow()
                setState { copy(isCreating = false) }
                sendEffect(ChecklistTemplatesContract.Effect.NavigateToDetails(tripId, checklist.id))
            } else {
                setState { copy(isCreating = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun getPlaceholderImage(name: String): String {
        return when {
            name.contains("Weekend", ignoreCase = true) -> 
                "https://images.unsplash.com/photo-1506744038136-46273834b3fb"
            name.contains("Family", ignoreCase = true) -> 
                "https://images.unsplash.com/photo-1502602898657-3e917247a183"
            name.contains("Business", ignoreCase = true) -> 
                "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab"
            name.contains("Adventure", ignoreCase = true) -> 
                "https://images.unsplash.com/photo-1533240332313-0db49b459ad6"
            else -> "https://images.unsplash.com/photo-1507525428034-b723cf961d3e"
        }
    }
}
