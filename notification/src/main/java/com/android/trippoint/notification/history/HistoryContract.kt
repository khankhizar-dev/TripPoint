package com.android.trippoint.notification.history

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.notification.domain.model.HistoryLog

class HistoryContract {
    sealed class Intent : UiIntent {
        data class LoadHistory(val query: String? = null) : Intent()
        object ClearHistory : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val logs: List<HistoryLog> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
