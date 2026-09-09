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
                    uiState.value.id, 
                    intent.id
                ))
            }
            is ChecklistDetailsContract.Intent.AddSection -> addSection(intent.name)
            ChecklistDetailsContract.Intent.AddSectionClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToAddSection)
            }
            ChecklistDetailsContract.Intent.AddItemClicked -> {
                val state = uiState.value
                val sectionId = state.sections.firstOrNull()?.id ?: ""
                sendEffect(ChecklistDetailsContract.Effect.NavigateToAddItem(
                    state.tripId,
                    state.id,
                    sectionId
                ))
            }
            ChecklistDetailsContract.Intent.AiSuggestClicked -> {
                sendEffect(ChecklistDetailsContract.Effect.NavigateToAiSuggest)
            }
            ChecklistDetailsContract.Intent.ProgressClicked -> {
                val state = uiState.value
                sendEffect(ChecklistDetailsContract.Effect.NavigateToProgress(state.tripId, state.id))
            }
            ChecklistDetailsContract.Intent.RetryClicked -> {
                val state = uiState.value
                loadChecklist(state.tripId, state.id)
            }
        }
    }

    private fun loadChecklist(tripId: String, id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId, id = id, error = null) }
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

    private fun addSection(name: String) {
        viewModelScope.launch {
            val state = uiState.value
            
            val result = repository.createSection(
                tripId = state.tripId,
                checklistId = state.id,
                name = name,
                position = state.sections.size
            )
            
            if (result.isSuccess) {
                loadChecklist(state.tripId, state.id)
            } else {
                setState { copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
