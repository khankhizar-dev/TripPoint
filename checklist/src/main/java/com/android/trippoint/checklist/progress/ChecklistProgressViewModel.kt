package com.android.trippoint.checklist.progress

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class ChecklistProgressViewModel(
    private val repository: ChecklistRepository
) : BaseViewModel<
    ChecklistProgressContract.State,
    ChecklistProgressContract.Intent,
    ChecklistProgressContract.Effect
>(
    ChecklistProgressContract.State()
) {
    override fun onIntent(intent: ChecklistProgressContract.Intent) {
        when (intent) {
            is ChecklistProgressContract.Intent.LoadProgress -> loadProgress(intent.tripId, intent.checklistId)
            ChecklistProgressContract.Intent.ViewCompletedClicked -> {
                uiState.value.checklist?.id?.let {
                    sendEffect(ChecklistProgressContract.Effect.NavigateToCompleted(uiState.value.tripId, it))
                }
            }
            ChecklistProgressContract.Intent.BackClicked -> sendEffect(ChecklistProgressContract.Effect.NavigateBack)
        }
    }

    private fun loadProgress(tripId: String, id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId) }
            val result = repository.getChecklist(tripId, id)
            
            if (result.isSuccess) {
                val checklist = result.getOrThrow()
                setState { 
                    copy(
                        isLoading = false, 
                        checklist = checklist, 
                        sections = checklist.sections 
                    ) 
                }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
