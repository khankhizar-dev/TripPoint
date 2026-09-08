package com.android.trippoint.budget.trends

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class SpendingTrendsViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<SpendingTrendsContract.State, SpendingTrendsContract.Intent, SpendingTrendsContract.Effect>(
    SpendingTrendsContract.State()
) {
    override fun onIntent(intent: SpendingTrendsContract.Intent) {
        when (intent) {
            is SpendingTrendsContract.Intent.LoadTrends -> loadTrends(intent.tripId, intent.budgetId)
            SpendingTrendsContract.Intent.BackClicked -> sendEffect(SpendingTrendsContract.Effect.NavigateBack)
            is SpendingTrendsContract.Intent.PeriodSelected -> {
                setState { copy(selectedPeriodIndex = uiState.value.periods.indexOf(intent.period)) }
                loadTrends(uiState.value.tripId, uiState.value.budgetId)
            }
        }
    }

    private fun loadTrends(tripId: String, budgetId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId, budgetId = budgetId) }
            
            // Use real API for Daily trends
            val result = repository.getDailyExpenseReport(
                tripId = tripId,
                fromDate = "2024-01-01T00:00:00", // Should ideally be calculated
                toDate = "2026-12-31T23:59:59"
            )
            
            if (result.isSuccess) {
                val report = result.getOrThrow()
                val trendData = report.map { it.date.takeLast(5) to it.amount.toFloat() }
                setState { copy(isLoading = false, trendData = trendData, error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
