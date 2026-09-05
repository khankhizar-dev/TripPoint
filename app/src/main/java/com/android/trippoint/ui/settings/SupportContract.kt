package com.android.trippoint.ui.settings

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class SupportContract {
    sealed class Intent : UiIntent {
        object HelpCenterClicked : Intent()
        object FaqClicked : Intent()
        object ContactUsClicked : Intent()
        object ReportIssueClicked : Intent()
        object FeedbackClicked : Intent()
    }

    data class State(
        val isLoading: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
    }
}
