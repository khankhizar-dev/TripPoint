package com.android.trippoint.ui.settings

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class NotificationsContract {
    sealed class Intent : UiIntent {
        data class PushToggled(val enabled: Boolean) : Intent()
        data class EmailToggled(val enabled: Boolean) : Intent()
        data class AlertsToggled(val enabled: Boolean) : Intent()
        data class RemindersToggled(val enabled: Boolean) : Intent()
        data class MarketingToggled(val enabled: Boolean) : Intent()
    }

    data class State(
        val isPushEnabled: Boolean = true,
        val isEmailEnabled: Boolean = true,
        val isAlertsEnabled: Boolean = true,
        val isRemindersEnabled: Boolean = false,
        val isMarketingEnabled: Boolean = false,
        val isLoading: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
    }
}
