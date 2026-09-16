package com.android.trippoint.checklist.add_item

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.model.ChecklistItemCategory
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.trip.domain.repository.TripRepository
import kotlinx.coroutines.launch

class AddChecklistItemViewModel(
    private val repository: ChecklistRepository,
    private val tripRepository: TripRepository
) : BaseViewModel<
    AddChecklistItemContract.State,
    AddChecklistItemContract.Intent,
    AddChecklistItemContract.Effect
>(
    AddChecklistItemContract.State()
) {
    override fun onIntent(intent: AddChecklistItemContract.Intent) {
        when (intent) {
            is AddChecklistItemContract.Intent.LoadIds -> {
                setState { 
                    copy(
                        tripId = intent.tripId, 
                        checklistId = intent.checklistId, 
                        sectionId = intent.sectionId
                    ) 
                }
                loadTripMembers(intent.tripId)
            }
            is AddChecklistItemContract.Intent.NameChanged -> setState { copy(name = intent.value) }
            is AddChecklistItemContract.Intent.CategoryChanged -> setState { 
                copy(category = ChecklistItemCategory.valueOf(intent.value)) 
            }
            is AddChecklistItemContract.Intent.NotesChanged -> setState { copy(notes = intent.value) }
            is AddChecklistItemContract.Intent.PriorityChanged -> setState { copy(priority = intent.value) }
            is AddChecklistItemContract.Intent.AssigneeChanged -> setState { copy(assigneeId = intent.value) }
            is AddChecklistItemContract.Intent.EssentialToggled -> setState { copy(isEssential = intent.value) }
            is AddChecklistItemContract.Intent.RemindMeToggled -> setState { copy(remindMe = intent.value) }
            is AddChecklistItemContract.Intent.ReminderTimeChanged -> setState { copy(reminderTime = intent.value) }
            AddChecklistItemContract.Intent.SaveClicked -> saveItem()
            AddChecklistItemContract.Intent.BackClicked -> sendEffect(AddChecklistItemContract.Effect.NavigateBack)
        }
    }

    private fun loadTripMembers(tripId: String) {
        viewModelScope.launch {
            val result = tripRepository.getTripMembers(tripId)
            if (result.isSuccess) {
                setState { copy(members = result.getOrDefault(emptyList())) }
            }
        }
    }

    private fun saveItem() {
        val state = uiState.value
        if (state.name.isBlank()) {
            setState { copy(error = "Item name is required") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.addItem(
                tripId = state.tripId,
                checklistId = state.checklistId,
                sectionId = state.sectionId,
                name = state.name,
                category = state.category,
                isEssential = state.isEssential,
                dueDate = if (state.remindMe) state.reminderTime else null,
                assigneeId = state.assigneeId,
                priority = state.priority
            )
            
            if (result.isSuccess) {
                setState { copy(isLoading = false) }
                sendEffect(AddChecklistItemContract.Effect.ItemAdded)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
