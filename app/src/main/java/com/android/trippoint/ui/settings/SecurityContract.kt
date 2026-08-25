package com.android.trippoint.ui.settings

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class SecurityContract {
    sealed class Intent : UiIntent {
        object Refresh : Intent()
        data class TwoFactorToggled(val enabled: Boolean) : Intent()
        object DeleteAccountClicked : Intent()
    }

    data class State(
        val deviceCount: Int = 0,
        val isTwoFactorEnabled: Boolean = false,
        val lastCheckupDate: String = "Today",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
