package com.android.trippoint.ui.settings

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class EditProfileContract {
    sealed class Intent : UiIntent {
        data class FullNameChanged(val name: String) : Intent()
        data class EmailChanged(val email: String) : Intent()
        data class PhoneChanged(val phone: String) : Intent()
        data class DobChanged(val dob: String) : Intent()
        data class NationalityChanged(val nationality: String) : Intent()
        data class PhotoSelected(val uri: String) : Intent()
        object SaveClicked : Intent()
    }

    data class State(
        val fullName: String = "",
        val email: String = "",
        val phone: String = "",
        val dob: String = "",
        val nationality: String = "",
        val profilePhotoUri: String? = null,
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
