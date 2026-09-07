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
            // Mock trend data based on selected period
            val mockData = when (uiState.value.periods.getOrNull(uiState.value.selectedPeriodIndex)) {
                "Daily" -> listOf(
                    "Mon" to 120f, "Tue" to 80f, "Wed" to 200f, "Thu" to 150f, 
                    "Fri" to 300f, "Sat" to 450f, "Sun" to 100f
                )
                "Weekly" -> listOf(
                    "Week 1" to 1200f, "Week 2" to 800f, "Week 3" to 2000f, "Week 4" to 1500f
                )
                else -> emptyList()
            }
            setState { copy(isLoading = false, trendData = mockData) }
        }
    }
}
