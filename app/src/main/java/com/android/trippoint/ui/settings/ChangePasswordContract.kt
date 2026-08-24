package com.android.trippoint.ui.settings

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ChangePasswordContract {
    sealed class Intent : UiIntent {
        data class CurrentPasswordChanged(val value: String) : Intent()
        data class NewPasswordChanged(val value: String) : Intent()
        data class ConfirmPasswordChanged(val value: String) : Intent()
        object ToggleCurrentVisibility : Intent()
        object ToggleNewVisibility : Intent()
        object ToggleConfirmVisibility : Intent()
        object SaveClicked : Intent()
    }

    data class State(
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        val isCurrentVisible: Boolean = false,
        val isNewVisible: Boolean = false,
        val isConfirmVisible: Boolean = false,
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val currentError: String? = null,
        val newError: String? = null,
        val confirmError: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
        object ShowSuccess : Effect()
    }
}
