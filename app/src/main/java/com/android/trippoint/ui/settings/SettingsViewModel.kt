package com.android.trippoint.ui.settings

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<SettingsContract.State, SettingsContract.Intent, SettingsContract.Effect>(
    initialState = SettingsContract.State()
) {

    init {
        onIntent(SettingsContract.Intent.LoadUser)
    }

    override fun onIntent(intent: SettingsContract.Intent) {
        when (intent) {
            is SettingsContract.Intent.LoadUser -> loadUser()
            is SettingsContract.Intent.Logout -> logout()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            authRepository.getMe()
                .onSuccess { user ->
                    setState { copy(isLoading = false, user = user) }
                }
                .onFailure { error ->
                    setState { copy(isLoading = false, error = error.message) }
                    sendEffect(SettingsContract.Effect.ShowError(error.message ?: "Failed to load user info"))
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess {
                    sendEffect(SettingsContract.Effect.NavigateToLogin)
                }
                .onFailure { error ->
                    sendEffect(SettingsContract.Effect.ShowError(error.message ?: "Logout failed"))
                }
        }
    }
}
