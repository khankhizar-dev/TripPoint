package com.android.trippoint.checklist.progress

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.checklist.domain.model.ChecklistStatus
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChecklistProgressViewModel : BaseViewModel<
    ChecklistProgressContract.State,
    ChecklistProgressContract.Intent,
    ChecklistProgressContract.Effect
>(
    ChecklistProgressContract.State()
) {
    override fun onIntent(intent: ChecklistProgressContract.Intent) {
        when (intent) {
            is ChecklistProgressContract.Intent.LoadProgress -> loadProgress(intent.checklistId)
            ChecklistProgressContract.Intent.ViewCompletedClicked -> {
                uiState.value.checklist?.id?.let {
                    sendEffect(ChecklistProgressContract.Effect.NavigateToCompleted(it))
                }
            }
            ChecklistProgressContract.Intent.BackClicked -> sendEffect(ChecklistProgressContract.Effect.NavigateBack)
        }
    }

    private fun loadProgress(id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(500) // Simulation
            
            val mockChecklist = Checklist(
                id = id, tripId = "t1", title = "Thailand Trip", 
                dateRange = "24 May - 2 Jun", totalItems = 55, completedItems = 33,
                status = ChecklistStatus.ACTIVE, createdAt = "now", updatedAt = "now"
            )
            
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
