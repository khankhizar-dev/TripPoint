package com.android.trippoint.itinerary.task

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.core.common.model.Priority

class AddTaskContract {
    sealed class Intent : UiIntent {
        data class LoadTripInfo(val tripId: String, val date: String) : Intent()
        data class NameChanged(val name: String) : Intent()
        data class DateChanged(val date: String) : Intent()
        data class TimeChanged(val time: String) : Intent()
        data class PriorityChanged(val priority: Priority) : Intent()
        data class ReminderToggled(val hasReminder: Boolean) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val name: String = "",
        val date: String = "",
        val time: String = "",
        val priority: Priority = Priority.MEDIUM,
        val hasReminder: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object TaskAdded : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
