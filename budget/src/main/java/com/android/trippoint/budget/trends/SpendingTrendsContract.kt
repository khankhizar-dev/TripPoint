package com.android.trippoint.budget.trends

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class SpendingTrendsContract {
    sealed class Intent : UiIntent {
        data class LoadTrends(val tripId: String, val budgetId: String) : Intent()
        object BackClicked : Intent()
        data class PeriodSelected(val period: String) : Intent()
    }

    data class State(
        val tripId: String = "",
        val budgetId: String = "",
        val trendData: List<Pair<String, Float>> = emptyList(),
        val selectedPeriodIndex: Int = 0,
        val periods: List<String> = listOf("Daily", "Weekly"),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
    }
}
