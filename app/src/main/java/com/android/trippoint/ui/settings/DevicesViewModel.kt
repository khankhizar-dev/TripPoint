package com.android.trippoint.ui.settings

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class DevicesViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<DevicesContract.State, DevicesContract.Intent, DevicesContract.Effect>(
    initialState = DevicesContract.State()
) {
    init {
        loadDevices()
    }

    private fun loadDevices() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = authRepository.getUserDevices()
            if (result.isSuccess) {
                setState { copy(isLoading = false, devices = result.getOrNull() ?: emptyList()) }
            } else {
                setState { copy(isLoading = false, error = "Failed to load devices") }
            }
        }
    }

    override fun onIntent(intent: DevicesContract.Intent) {
        when (intent) {
            DevicesContract.Intent.LoadDevices -> loadDevices()
            is DevicesContract.Intent.LogoutDevice -> { /* Implement logout specific device */ }
            DevicesContract.Intent.LogoutAllDevices -> { /* Implement logout all */ }
        }
    }
}
