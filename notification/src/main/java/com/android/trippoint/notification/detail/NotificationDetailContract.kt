package com.android.trippoint.notification.detail

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.notification.domain.model.NotificationDetail

class NotificationDetailContract {
    sealed class Intent : UiIntent {
        data class LoadDetail(val notificationId: String) : Intent()
        object ActionClicked : Intent()
        object MarkAsRead : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val detail: NotificationDetail? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToTrip(val tripId: String) : Effect()
        data class NavigateToRoute(val route: String) : Effect()
    }
}
