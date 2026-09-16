package com.android.trippoint.trip.collaboration.activity

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.trip.collaboration.domain.model.ActivityLog

class ActivityFeedContract {
    sealed class Intent : UiIntent {
        data class LoadFeed(val tripId: String) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val activities: List<ActivityLog> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
