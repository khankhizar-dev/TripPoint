package com.android.trippoint.authentication.profilesetup

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ProfileSetupContract {
    sealed class Intent : UiIntent {
        data class PhotoSelected(val uri: String) : Intent()
        data class FullNameChanged(val name: String) : Intent()
        data class UsernameChanged(val username: String) : Intent()
        data class CountryChanged(val country: String) : Intent()
        data class CurrencyChanged(val currency: String) : Intent()
        data class LanguageChanged(val language: String) : Intent()
        data class TimezoneChanged(val timezone: String) : Intent()
        data class EditStepClicked(val step: Step) : Intent()
        object NextClicked : Intent()
        object BackClicked : Intent()
        object SkipPhotoClicked : Intent()
    }

    data class State(
        val currentStep: Step = Step.PHOTO,
        val profilePhotoUri: String? = null,
        val fullName: String = "",
        val username: String = "",
        val country: String = "",
        val currency: String = "",
        val language: String = "",
        val timezone: String = "",
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false
    ) : UiState

    enum class Step {
        PHOTO, ABOUT, PREFERENCES, REVIEW
    }

    sealed class Effect : UiEffect {
        object NavigateToHome : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
