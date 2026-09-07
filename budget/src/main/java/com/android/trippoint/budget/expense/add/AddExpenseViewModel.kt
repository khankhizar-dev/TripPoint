package com.android.trippoint.budget.expense.add

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class AddExpenseViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<AddExpenseContract.State, AddExpenseContract.Intent, AddExpenseContract.Effect>(
    AddExpenseContract.State()
) {
    override fun onIntent(intent: AddExpenseContract.Intent) {
        when (intent) {
            is AddExpenseContract.Intent.LoadBudgetId -> setState { copy(budgetId = intent.budgetId) }
            is AddExpenseContract.Intent.AmountChanged -> setState { copy(amount = intent.value) }
            is AddExpenseContract.Intent.CategoryChanged -> setState { copy(category = intent.value) }
            is AddExpenseContract.Intent.DateChanged -> setState { copy(date = intent.value) }
            is AddExpenseContract.Intent.DescriptionChanged -> setState { copy(description = intent.value) }
            AddExpenseContract.Intent.SaveClicked -> saveExpense()
            AddExpenseContract.Intent.BackClicked -> sendEffect(AddExpenseContract.Effect.NavigateBack)
        }
    }

    private fun saveExpense() {
        val state = uiState.value
        val amountValue = state.amount.toDoubleOrNull() ?: 0.0
        
        if (amountValue <= 0) {
            setState { copy(error = "Enter a valid amount") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.addExpense(
                budgetId = state.budgetId,
                amount = amountValue,
                category = state.category,
                date = state.date,
                description = state.description
            )
            if (result.isSuccess) {
                setState { copy(isLoading = false) }
                sendEffect(AddExpenseContract.Effect.ExpenseAdded)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
