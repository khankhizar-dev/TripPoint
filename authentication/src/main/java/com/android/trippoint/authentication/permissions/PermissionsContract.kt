package com.android.trippoint.authentication.permissions

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class PermissionsContract {
    sealed class Intent : UiIntent {
        object AllowClicked : Intent()
        object DenyClicked : Intent()
        object ExploreClicked : Intent()
    }

    data class State(
        val currentStep: Step = Step.NOTIFICATIONS,
        val isAllSet: Boolean = false
    ) : UiState

    enum class Step {
        NOTIFICATIONS, LOCATION, CALENDAR
    }

    sealed class Effect : UiEffect {
        object NavigateToHome : Effect()
        data class RequestPermission(val permission: Step) : Effect()
    }
}
