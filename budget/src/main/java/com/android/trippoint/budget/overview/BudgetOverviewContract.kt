package com.android.trippoint.budget.overview

import com.android.trippoint.budget.domain.model.Budget
import com.android.trippoint.budget.domain.model.Expense
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class BudgetOverviewContract {
    sealed class Intent : UiIntent {
        data class LoadBudget(val tripId: String, val budgetId: String) : Intent()
        object BackClicked : Intent()
        object AddExpenseClicked : Intent()
        data class CategoryClicked(val categoryId: String) : Intent()
    }

    data class State(
        val tripId: String = "",
        val budgetId: String = "",
        val budget: Budget? = null,
        val expenses: List<Expense> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToAddExpense(val budgetId: String) : Effect()
    }
}
