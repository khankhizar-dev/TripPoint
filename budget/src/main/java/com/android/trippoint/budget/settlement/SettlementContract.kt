package com.android.trippoint.budget.settlement

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.core.network.SettlementSummaryDto

class SettlementContract {
    sealed class Intent : UiIntent {
        data class LoadSettlements(val tripId: String) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val summary: SettlementSummaryDto? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
    }
}
