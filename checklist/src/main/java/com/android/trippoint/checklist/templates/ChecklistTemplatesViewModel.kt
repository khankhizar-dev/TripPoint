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
            ChecklistTemplatesContract.Intent.LoadTemplates -> loadTemplates()
            is ChecklistTemplatesContract.Intent.TemplateClicked -> {
                sendEffect(ChecklistTemplatesContract.Effect.NavigateToCreate(intent.id))
            }
            ChecklistTemplatesContract.Intent.BackClicked -> sendEffect(ChecklistTemplatesContract.Effect.NavigateBack)
        }
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getTemplates()
            
            if (result.isSuccess) {
                val templates = result.getOrDefault(emptyList()).map { template ->
                    ChecklistTemplatesContract.Template(
                        id = template.id,
                        title = template.name,
                        itemCount = template.sections.sumOf { it.items.size },
                        imageUrl = getPlaceholderImage(template.name)
                    )
                }
                setState { copy(isLoading = false, templates = templates) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun getPlaceholderImage(name: String): String {
        return if (name.contains("Weekend", ignoreCase = true)) {
            "https://images.unsplash.com/photo-1506744038136-46273834b3fb"
        } else if (name.contains("Family", ignoreCase = true)) {
            "https://images.unsplash.com/photo-1502602898657-3e917247a183"
        } else if (name.contains("Business", ignoreCase = true)) {
            "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab"
        } else if (name.contains("Adventure", ignoreCase = true)) {
            "https://images.unsplash.com/photo-1533240332313-0db49b459ad6"
        } else {
            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e"
        }
    }
}
