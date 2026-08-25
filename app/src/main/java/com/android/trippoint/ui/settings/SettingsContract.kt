package com.android.trippoint.ui.settings

import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class SettingsContract {
    data class State(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null
    ) : UiState

    sealed class Intent : UiIntent {
        object LoadUser : Intent()
        object Logout : Intent()
    }

    sealed class Effect : UiEffect {
        object NavigateToLogin : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
