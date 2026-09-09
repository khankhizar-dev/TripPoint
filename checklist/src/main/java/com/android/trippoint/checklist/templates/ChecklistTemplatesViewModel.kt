package com.android.trippoint.checklist.templates

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChecklistTemplatesViewModel : BaseViewModel<
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
            delay(600) // Simulation
            
            val mockTemplates = listOf(
                ChecklistTemplatesContract.Template(
                    "t1", designR.string.checklist_template_weekend, 25, 
                    "https://images.unsplash.com/photo-1506744038136-46273834b3fb"
                ),
                ChecklistTemplatesContract.Template(
                    "t2", designR.string.checklist_template_family, 45,
                    "https://images.unsplash.com/photo-1502602898657-3e917247a183"
                ),
                ChecklistTemplatesContract.Template(
                    "t3", designR.string.checklist_template_business, 30,
                    "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab"
                ),
                ChecklistTemplatesContract.Template(
                    "t4", designR.string.checklist_template_adventure, 50,
                    "https://images.unsplash.com/photo-1533240332313-0db49b459ad6"
                ),
                ChecklistTemplatesContract.Template(
                    "t5", designR.string.checklist_template_beach, 20,
                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e"
                )
            )
            
            setState { copy(isLoading = false, templates = mockTemplates) }
        }
    }
}
