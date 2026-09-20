package com.android.trippoint.notification.center

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.notification.domain.model.Notification

class NotificationCenterContract {
    sealed class Intent : UiIntent {
        object LoadNotifications : Intent()
        data class TabSelected(val index: Int) : Intent()
        data class NotificationClicked(val notificationId: String) : Intent()
        data class MarkAsRead(val notificationId: String) : Intent()
        object ClearAll : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val notifications: List<Notification> = emptyList(),
        val filteredNotifications: List<Notification> = emptyList(),
        val selectedTab: Int = 0, // 0: All, 1: Unread, 2: Mentions
        val isLoading: Boolean = false,
        val error: String? = null,
        val unreadCount: Int = 0
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToDetail(val notificationId: String) : Effect()
    }
}
