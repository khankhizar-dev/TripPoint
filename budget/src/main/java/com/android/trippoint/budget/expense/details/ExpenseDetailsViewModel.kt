package com.android.trippoint.budget.expense.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class ExpenseDetailsViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<
    ExpenseDetailsContract.State,
    ExpenseDetailsContract.Intent,
    ExpenseDetailsContract.Effect
>(
    ExpenseDetailsContract.State()
) {
    override fun onIntent(intent: ExpenseDetailsContract.Intent) {
        when (intent) {
            is ExpenseDetailsContract.Intent.LoadExpense -> loadExpense(intent.tripId, intent.expenseId)
            ExpenseDetailsContract.Intent.BackClicked -> sendEffect(ExpenseDetailsContract.Effect.NavigateBack)
            ExpenseDetailsContract.Intent.EditClicked -> {
                val expense = uiState.value.expense ?: return
                sendEffect(ExpenseDetailsContract.Effect.NavigateToEdit(uiState.value.tripId, expense.id))
            }
            ExpenseDetailsContract.Intent.ArchiveClicked -> archiveExpense()
        }
    }

    private fun loadExpense(tripId: String, expenseId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId) }
            val result = repository.getExpense(tripId, expenseId)
            if (result.isSuccess) {
                setState { copy(isLoading = false, expense = result.getOrNull(), error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun archiveExpense() {
        val expense = uiState.value.expense ?: return
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.archiveExpense(uiState.value.tripId, expense.id)
            if (result.isSuccess) {
                sendEffect(ExpenseDetailsContract.Effect.NavigateBack)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
