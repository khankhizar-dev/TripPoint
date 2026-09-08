package com.android.trippoint.budget.expense.add

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.core.common.model.TripMember

class AddExpenseContract {
    sealed class Intent : UiIntent {
        data class LoadIds(val tripId: String, val budgetId: String) : Intent()
        data class AmountChanged(val value: String) : Intent()
        data class CategoryChanged(val value: String) : Intent()
        data class DateChanged(val value: String) : Intent()
        data class DescriptionChanged(val value: String) : Intent()
        data class PaidByChanged(val value: String) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val budgetId: String = "",
        val amount: String = "",
        val category: String = "Food",
        val date: String = "",
        val description: String = "",
        val paidBy: String = "",
        val members: List<TripMember> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val errorResId: Int? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object ExpenseAdded : Effect()
    }
}
