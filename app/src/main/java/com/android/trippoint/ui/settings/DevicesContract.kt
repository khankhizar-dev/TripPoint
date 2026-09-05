package com.android.trippoint.ui.settings

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.core.network.UserDevice

class DevicesContract {
    sealed class Intent : UiIntent {
        object LoadDevices : Intent()
        data class LogoutDevice(val id: String) : Intent()
        object LogoutAllDevices : Intent()
    }

    data class State(
        val devices: List<UserDevice> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
