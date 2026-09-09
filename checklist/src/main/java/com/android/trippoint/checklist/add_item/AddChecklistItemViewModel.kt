package com.android.trippoint.checklist.add_item

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddChecklistItemViewModel : BaseViewModel<
    AddChecklistItemContract.State,
    AddChecklistItemContract.Intent,
    AddChecklistItemContract.Effect
>(
    AddChecklistItemContract.State()
) {
    override fun onIntent(intent: AddChecklistItemContract.Intent) {
        when (intent) {
            is AddChecklistItemContract.Intent.LoadIds -> setState { 
                copy(checklistId = intent.checklistId, sectionId = intent.sectionId) 
            }
            is AddChecklistItemContract.Intent.NameChanged -> setState { copy(name = intent.value) }
            is AddChecklistItemContract.Intent.CategoryChanged -> setState { copy(category = intent.value) }
            is AddChecklistItemContract.Intent.NotesChanged -> setState { copy(notes = intent.value) }
            is AddChecklistItemContract.Intent.EssentialToggled -> setState { copy(isEssential = intent.value) }
            is AddChecklistItemContract.Intent.RemindMeToggled -> setState { copy(remindMe = intent.value) }
            is AddChecklistItemContract.Intent.ReminderTimeChanged -> setState { copy(reminderTime = intent.value) }
            AddChecklistItemContract.Intent.SaveClicked -> saveItem()
            AddChecklistItemContract.Intent.BackClicked -> sendEffect(AddChecklistItemContract.Effect.NavigateBack)
        }
    }

    private fun saveItem() {
        if (uiState.value.name.isBlank()) {
            setState { copy(error = "Item name is required") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(1000) // Simulation
            setState { copy(isLoading = false) }
            sendEffect(AddChecklistItemContract.Effect.ItemAdded)
        }
    }
}
