package com.android.trippoint.checklist.add_item

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class AddChecklistItemContract {
    sealed class Intent : UiIntent {
        data class LoadIds(val checklistId: String, val sectionId: String) : Intent()
        data class NameChanged(val value: String) : Intent()
        data class CategoryChanged(val value: String) : Intent()
        data class NotesChanged(val value: String) : Intent()
        data class EssentialToggled(val value: Boolean) : Intent()
        data class RemindMeToggled(val value: Boolean) : Intent()
        data class ReminderTimeChanged(val value: String) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val checklistId: String = "",
        val sectionId: String = "",
        val name: String = "",
        val category: String = "Others",
        val notes: String = "",
        val isEssential: Boolean = false,
        val remindMe: Boolean = false,
        val reminderTime: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object ItemAdded : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
