package com.android.trippoint.notification.center

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationCenterViewModel(
    private val repository: NotificationRepository
) : BaseViewModel<
    NotificationCenterContract.State,
    NotificationCenterContract.Intent,
    NotificationCenterContract.Effect
>(
    NotificationCenterContract.State()
) {

    init {
        onIntent(NotificationCenterContract.Intent.LoadNotifications)
        observeIncoming()
    }

    override fun onIntent(intent: NotificationCenterContract.Intent) {
        when (intent) {
            NotificationCenterContract.Intent.LoadNotifications -> loadNotifications()
            is NotificationCenterContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.index) }
                loadNotifications()
            }
            is NotificationCenterContract.Intent.NotificationClicked -> {
                sendEffect(NotificationCenterContract.Effect.NavigateToDetail(intent.notificationId))
                markAsRead(intent.notificationId)
            }
            is NotificationCenterContract.Intent.MarkAsRead -> markAsRead(intent.notificationId)
            NotificationCenterContract.Intent.ClearAll -> clearAll()
            NotificationCenterContract.Intent.BackClicked -> sendEffect(NotificationCenterContract.Effect.NavigateBack)
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getNotifications()
            val unreadResult = repository.getUnreadCount()
            
            if (result.isSuccess) {
                val notifications = result.getOrDefault(emptyList())
                setState { 
                    copy(
                        isLoading = false, 
                        notifications = notifications,
                        filteredNotifications = notifications,
                        unreadCount = unreadResult.getOrDefault(0)
                    ) 
                }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun observeIncoming() {
        viewModelScope.launch {
            repository.observeIncomingNotifications().collectLatest { notification ->
                setState { 
                    copy(
                        notifications = listOf(notification) + notifications,
                        unreadCount = unreadCount + 1
                    ) 
                }
            }
        }
    }

    private fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markAsRead(id)
            loadNotifications()
        }
    }

    private fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
            setState { 
                copy(
                    notifications = emptyList(), 
                    filteredNotifications = emptyList(), 
                    unreadCount = 0
                ) 
            }
        }
    }
}
