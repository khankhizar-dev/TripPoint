package com.android.trippoint.notification.preferences

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.notification.domain.model.NotificationPreferences
import com.android.trippoint.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationPreferencesViewModel(
    private val repository: NotificationRepository
) : BaseViewModel<
    NotificationPreferencesContract.State,
    NotificationPreferencesContract.Intent,
    NotificationPreferencesContract.Effect
>(
    NotificationPreferencesContract.State()
) {

    init {
        onIntent(NotificationPreferencesContract.Intent.LoadPreferences)
    }

    override fun onIntent(intent: NotificationPreferencesContract.Intent) {
        when (intent) {
            NotificationPreferencesContract.Intent.LoadPreferences -> loadPreferences()
            is NotificationPreferencesContract.Intent.PushToggled -> 
                updatePreference { it.copy(pushEnabled = intent.enabled) }
            is NotificationPreferencesContract.Intent.EmailToggled -> 
                updatePreference { it.copy(emailEnabled = intent.enabled) }
            is NotificationPreferencesContract.Intent.InAppToggled -> 
                updatePreference { it.copy(inAppEnabled = intent.enabled) }
            is NotificationPreferencesContract.Intent.QuietHoursChanged -> 
                updatePreference { it.copy(quietHoursStart = intent.start, quietHoursEnd = intent.end) }
            is NotificationPreferencesContract.Intent.DigestToggled -> 
                updatePreference { it.copy(digestEnabled = intent.enabled) }
            NotificationPreferencesContract.Intent.BackClicked -> 
                sendEffect(NotificationPreferencesContract.Effect.NavigateBack)
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getPreferences()
            if (result.isSuccess) {
                setState { copy(isLoading = false, preferences = result.getOrThrow()) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun updatePreference(
        update: (NotificationPreferences) -> NotificationPreferences
    ) {
        viewModelScope.launch {
            val current = uiState.value.preferences
            val updated = update(current)
            setState { copy(preferences = updated) }
            repository.updatePreferences(updated)
        }
    }
}
