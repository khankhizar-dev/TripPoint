package com.android.trippoint.ui.settings

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class SecurityViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<SecurityContract.State, SecurityContract.Intent, SecurityContract.Effect>(
    initialState = SecurityContract.State()
) {
    init {
        loadSecurityData()
    }

    private fun loadSecurityData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = authRepository.getUserDevices()
            if (result.isSuccess) {
                setState { 
                    copy(
                        isLoading = false, 
                        deviceCount = result.getOrNull()?.size ?: 0 
                    ) 
                }
            } else {
                setState { copy(isLoading = false, error = "Failed to load devices") }
            }
        }
    }

    override fun onIntent(intent: SecurityContract.Intent) {
        when (intent) {
            SecurityContract.Intent.Refresh -> loadSecurityData()
            is SecurityContract.Intent.TwoFactorToggled -> {
                setState { copy(isTwoFactorEnabled = intent.enabled) }
                // Simulate save
                viewModelScope.launch {
                    // authRepository.updateSecurity(...)
                }
            }
            SecurityContract.Intent.DeleteAccountClicked -> { /* Handle account deletion */ }
        }
    }
}
