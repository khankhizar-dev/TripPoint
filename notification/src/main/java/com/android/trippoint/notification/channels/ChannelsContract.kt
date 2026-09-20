package com.android.trippoint.notification.channels

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.notification.domain.model.NotificationChannel

class ChannelsContract {
    sealed class Intent : UiIntent {
        object LoadChannels : Intent()
        data class ToggleChannel(val id: String, val enabled: Boolean) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val channels: List<NotificationChannel> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
