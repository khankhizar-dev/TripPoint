package com.android.trippoint.notification.preferences

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.notification.domain.model.NotificationPreferences

class NotificationPreferencesContract {
    sealed class Intent : UiIntent {
        object LoadPreferences : Intent()
        data class PushToggled(val enabled: Boolean) : Intent()
        data class EmailToggled(val enabled: Boolean) : Intent()
        data class InAppToggled(val enabled: Boolean) : Intent()
        data class QuietHoursChanged(val start: String, val end: String) : Intent()
        data class DigestToggled(val enabled: Boolean) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val preferences: NotificationPreferences = NotificationPreferences(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
