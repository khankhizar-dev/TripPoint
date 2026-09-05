package com.android.trippoint.budget.expense.list

import com.android.trippoint.budget.domain.model.Expense
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ExpenseListContract {
    sealed class Intent : UiIntent {
        data class LoadExpenses(val budgetId: String) : Intent()
        object AddExpenseClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val budgetId: String = "",
        val expenses: List<Expense> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToAddExpense(val budgetId: String) : Effect()
    }
}
