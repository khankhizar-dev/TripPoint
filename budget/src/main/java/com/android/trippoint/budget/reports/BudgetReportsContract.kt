package com.android.trippoint.budget.reports

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class BudgetReportsContract {
    sealed class Intent : UiIntent {
        data class LoadReports(val tripId: String, val budgetId: String) : Intent()
        object BackClicked : Intent()
        data class DownloadReport(val format: String) : Intent()
        data class ShareReport(val format: String) : Intent()
    }

    data class State(
        val tripId: String = "",
        val budgetId: String = "",
        val availableReports: List<String> = listOf("Expense Summary", "Category Analysis", "Receipt Export"),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowSuccess(val message: String) : Effect()
    }
}
