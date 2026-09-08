package com.android.trippoint.budget.expense.details

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.budget.domain.model.Expense

class ExpenseDetailsContract {
    sealed class Intent : UiIntent {
        data class LoadExpense(val tripId: String, val expenseId: String) : Intent()
        object BackClicked : Intent()
        object EditClicked : Intent()
        object ArchiveClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val expense: Expense? = null,
        val paidByName: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToEdit(val tripId: String, val expenseId: String) : Effect()
    }
}
