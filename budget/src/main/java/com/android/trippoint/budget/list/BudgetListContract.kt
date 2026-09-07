package com.android.trippoint.budget.list

import com.android.trippoint.budget.domain.model.Budget
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class BudgetListContract {
    sealed class Intent : UiIntent {
        data class LoadBudgets(val tripId: String? = null) : Intent()
        data class BudgetClicked(val tripId: String, val budgetId: String) : Intent()
        object CreateBudgetClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val budgets: List<Budget> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToBudgetDetails(val tripId: String, val budgetId: String) : Effect()
        object NavigateToCreateBudget : Effect()
    }
}
