package com.android.trippoint.budget.expense.add

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class AddExpenseContract {
    sealed class Intent : UiIntent {
        data class LoadBudgetId(val budgetId: String) : Intent()
        data class AmountChanged(val value: String) : Intent()
        data class CategoryChanged(val value: String) : Intent()
        data class DateChanged(val value: String) : Intent()
        data class DescriptionChanged(val value: String) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val budgetId: String = "",
        val amount: String = "",
        val category: String = "Food",
        val date: String = "",
        val description: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object ExpenseAdded : Effect()
    }
}
