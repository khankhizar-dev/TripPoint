package com.android.trippoint.budget.expense.list

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class ExpenseListViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<ExpenseListContract.State, ExpenseListContract.Intent, ExpenseListContract.Effect>(
    ExpenseListContract.State()
) {
    override fun onIntent(intent: ExpenseListContract.Intent) {
        when (intent) {
            is ExpenseListContract.Intent.LoadExpenses -> loadExpenses(intent.budgetId)
            ExpenseListContract.Intent.AddExpenseClicked -> {
                sendEffect(ExpenseListContract.Effect.NavigateToAddExpense(uiState.value.budgetId))
            }
            ExpenseListContract.Intent.BackClicked -> sendEffect(ExpenseListContract.Effect.NavigateBack)
        }
    }

    private fun loadExpenses(budgetId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, budgetId = budgetId) }
            val result = repository.getExpenses(budgetId)
            if (result.isSuccess) {
                setState { copy(isLoading = false, expenses = result.getOrDefault(emptyList()), error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
