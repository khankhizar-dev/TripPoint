package com.android.trippoint.notification.reminders

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.notification.domain.model.ReminderStatus
import com.android.trippoint.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.launch

class RemindersViewModel(
    private val repository: NotificationRepository
) : BaseViewModel<
    RemindersContract.State,
    RemindersContract.Intent,
    RemindersContract.Effect
>(
    RemindersContract.State()
) {

    init {
        onIntent(RemindersContract.Intent.LoadReminders)
    }

    override fun onIntent(intent: RemindersContract.Intent) {
        when (intent) {
            RemindersContract.Intent.LoadReminders -> loadReminders()
            is RemindersContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.index) }
                filterReminders()
            }
            is RemindersContract.Intent.MarkAsDone -> markAsDone(intent.reminderId)
            is RemindersContract.Intent.SnoozeReminder -> { /* Handle Snooze */ }
            RemindersContract.Intent.BackClicked -> sendEffect(RemindersContract.Effect.NavigateBack)
            RemindersContract.Intent.AddReminderClicked -> sendEffect(RemindersContract.Effect.NavigateToAddReminder)
        }
    }

    private fun loadReminders() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getReminders()
            if (result.isSuccess) {
                val reminders = result.getOrDefault(emptyList())
                setState { 
                    copy(
                        isLoading = false, 
                        reminders = reminders,
                        upcomingCount = reminders.count { it.status == ReminderStatus.UPCOMING },
                        overdueCount = reminders.count { it.status == ReminderStatus.OVERDUE }
                    ) 
                }
                filterReminders()
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun filterReminders() {
        val tab = uiState.value.selectedTab
        val filtered = when (tab) {
            0 -> uiState.value.reminders.filter { it.status == ReminderStatus.UPCOMING }
            1 -> uiState.value.reminders.filter { it.status == ReminderStatus.OVERDUE }
            2 -> uiState.value.reminders.filter { it.status == ReminderStatus.DONE }
            else -> uiState.value.reminders
        }
        setState { copy(filteredReminders = filtered) }
    }

    private fun markAsDone(id: String) {
        viewModelScope.launch {
            repository.markReminderAsDone(id)
            loadReminders()
        }
    }
}
