package com.android.trippoint.checklist.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.checklist.domain.model.ChecklistStatus
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChecklistDetailsViewModel : BaseViewModel<
    ChecklistDetailsContract.State,
    ChecklistDetailsContract.Intent,
    ChecklistDetailsContract.Effect
>(
    ChecklistDetailsContract.State()
) {
    override fun onIntent(intent: ChecklistDetailsContract.Intent) {
        when (intent) {
            is ChecklistDetailsContract.Intent.LoadChecklist -> loadChecklist(intent.id)
            ChecklistDetailsContract.Intent.BackClicked -> sendEffect(ChecklistDetailsContract.Effect.NavigateBack)
            is ChecklistDetailsContract.Intent.SectionClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToSectionDetails(intent.id))
            }
            ChecklistDetailsContract.Intent.AddSectionClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToAddSection)
            }
            ChecklistDetailsContract.Intent.AddItemClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToAddItem)
            }
            ChecklistDetailsContract.Intent.AiSuggestClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToAiSuggest)
            }
        }
    }

    private fun loadChecklist(id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(800) // Simulation
            
            val mockChecklist = Checklist(
                id = id, tripId = "t1", title = "Thailand Trip", 
                dateRange = "24 May - 2 Jun", totalItems = 55, completedItems = 33,
                status = ChecklistStatus.ACTIVE, createdAt = "now", updatedAt = "now"
            )
            
            // In a real app, these would come from strings.xml via resource provider or directly in UI
            // For domain model, we use raw strings, but for initial mock we can use keys or localized strings
            val mockSections = listOf(
                ChecklistSection("s1", id, "Pre-trip Checklist", 20, 12),
                ChecklistSection("s2", id, "Packing List", 35, 18),
                ChecklistSection("s3", id, "Visa & Documents", 10, 5),
                ChecklistSection("s4", id, "On-Trip Tasks", 12, 4)
            )
            
            setState { copy(isLoading = false, checklist = mockChecklist, sections = mockSections) }
        }
    }
}
