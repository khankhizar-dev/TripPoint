package com.android.trippoint.checklist.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class ChecklistDetailsViewModel(
    private val repository: ChecklistRepository
) : BaseViewModel<
    ChecklistDetailsContract.State,
    ChecklistDetailsContract.Intent,
    ChecklistDetailsContract.Effect
>(
    ChecklistDetailsContract.State()
) {
    override fun onIntent(intent: ChecklistDetailsContract.Intent) {
        when (intent) {
            is ChecklistDetailsContract.Intent.LoadChecklist -> loadChecklist(intent.tripId, intent.id)
            ChecklistDetailsContract.Intent.BackClicked -> sendEffect(ChecklistDetailsContract.Effect.NavigateBack)
            is ChecklistDetailsContract.Intent.SectionClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToSectionDetails(
                    uiState.value.tripId, 
                    uiState.value.checklist?.id ?: "", 
                    intent.id
                ))
            }
            ChecklistDetailsContract.Intent.AddSectionClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToAddSection)
            }
            ChecklistDetailsContract.Intent.AddItemClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToAddItem(
                    uiState.value.tripId,
                    uiState.value.checklist?.id ?: ""
                ))
            }
            ChecklistDetailsContract.Intent.AiSuggestClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToAiSuggest)
            }
            ChecklistDetailsContract.Intent.ProgressClicked -> {
                uiState.value.checklist?.id?.let {
                    sendEffect(ChecklistDetailsContract.Effect.NavigateToProgress(it))
                }
            }
        }
    }

    private fun loadChecklist(tripId: String, id: String) {
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
