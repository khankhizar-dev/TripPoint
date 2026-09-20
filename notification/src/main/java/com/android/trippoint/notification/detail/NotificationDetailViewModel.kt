package com.android.trippoint.notification.detail

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationDetailViewModel(
    private val repository: NotificationRepository
) : BaseViewModel<
    NotificationDetailContract.State,
    NotificationDetailContract.Intent,
    NotificationDetailContract.Effect
>(
    NotificationDetailContract.State()
) {
    override fun onIntent(intent: NotificationDetailContract.Intent) {
        when (intent) {
            is NotificationDetailContract.Intent.LoadDetail -> loadDetail(intent.notificationId)
            NotificationDetailContract.Intent.ActionClicked -> {
                uiState.value.detail?.actionRoute?.let {
                    sendEffect(NotificationDetailContract.Effect.NavigateToRoute(it))
                } ?: run {
                    uiState.value.detail?.notification?.tripId?.let {
                        sendEffect(NotificationDetailContract.Effect.NavigateToTrip(it))
                    }
                }
            }
            NotificationDetailContract.Intent.MarkAsRead -> {
                markAsRead()
            }
            NotificationDetailContract.Intent.BackClicked -> sendEffect(NotificationDetailContract.Effect.NavigateBack)
        }
    }

    private fun loadDetail(id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getNotificationDetail(id)
            
            if (result.isSuccess) {
                setState { copy(isLoading = false, detail = result.getOrNull()) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun markAsRead() {
        viewModelScope.launch {
            uiState.value.detail?.notification?.id?.let { id ->
                repository.markAsRead(id)
            }
            sendEffect(NotificationDetailContract.Effect.NavigateBack)
        }
    }
}
