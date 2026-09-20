package com.android.trippoint.notification.channels

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.launch

class ChannelsViewModel(
    private val repository: NotificationRepository
) : BaseViewModel<
    ChannelsContract.State,
    ChannelsContract.Intent,
    ChannelsContract.Effect
>(
    ChannelsContract.State()
) {

    init {
        onIntent(ChannelsContract.Intent.LoadChannels)
    }

    override fun onIntent(intent: ChannelsContract.Intent) {
        when (intent) {
            ChannelsContract.Intent.LoadChannels -> loadChannels()
            is ChannelsContract.Intent.ToggleChannel -> toggleChannel(intent.id, intent.enabled)
            ChannelsContract.Intent.BackClicked -> sendEffect(ChannelsContract.Effect.NavigateBack)
        }
    }

    private fun loadChannels() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getChannels()
            if (result.isSuccess) {
                setState { copy(isLoading = false, channels = result.getOrThrow()) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun toggleChannel(id: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateChannelStatus(id, enabled)
            loadChannels()
        }
    }
}
