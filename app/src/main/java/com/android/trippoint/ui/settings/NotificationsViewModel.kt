package com.android.trippoint.ui.settings

import com.android.trippoint.core.common.BaseViewModel

class NotificationsViewModel : 
    BaseViewModel<NotificationsContract.State, NotificationsContract.Intent, NotificationsContract.Effect>(
        initialState = NotificationsContract.State()
    ) {
    override fun onIntent(intent: NotificationsContract.Intent) {
        when (intent) {
            is NotificationsContract.Intent.PushToggled -> setState { copy(isPushEnabled = intent.enabled) }
            is NotificationsContract.Intent.EmailToggled -> setState { copy(isEmailEnabled = intent.enabled) }
            is NotificationsContract.Intent.AlertsToggled -> setState { copy(isAlertsEnabled = intent.enabled) }
            is NotificationsContract.Intent.RemindersToggled -> setState { copy(isRemindersEnabled = intent.enabled) }
            is NotificationsContract.Intent.MarketingToggled -> setState { copy(isMarketingEnabled = intent.enabled) }
        }
    }
}
