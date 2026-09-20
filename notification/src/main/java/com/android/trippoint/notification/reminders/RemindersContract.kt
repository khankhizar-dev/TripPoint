package com.android.trippoint.notification.reminders

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.notification.domain.model.Reminder

class RemindersContract {
    sealed class Intent : UiIntent {
        object LoadReminders : Intent()
        data class TabSelected(val index: Int) : Intent()
        data class MarkAsDone(val reminderId: String) : Intent()
        data class SnoozeReminder(val reminderId: String) : Intent()
        object BackClicked : Intent()
        object AddReminderClicked : Intent()
    }

    data class State(
        val reminders: List<Reminder> = emptyList(),
        val filteredReminders: List<Reminder> = emptyList(),
        val selectedTab: Int = 0, // 0: Upcoming, 1: Overdue, 2: Done
        val isLoading: Boolean = false,
        val error: String? = null,
        val upcomingCount: Int = 0,
        val overdueCount: Int = 0
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object NavigateToAddReminder : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
