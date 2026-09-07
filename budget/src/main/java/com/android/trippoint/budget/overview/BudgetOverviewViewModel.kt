package com.android.trippoint.budget.overview

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class BudgetOverviewViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<BudgetOverviewContract.State, BudgetOverviewContract.Intent, BudgetOverviewContract.Effect>(
    BudgetOverviewContract.State()
) {
    override fun onIntent(intent: BudgetOverviewContract.Intent) {
        when (intent) {
            is BudgetOverviewContract.Intent.LoadBudget -> loadBudget(intent.tripId, intent.budgetId)
            BudgetOverviewContract.Intent.BackClicked -> sendEffect(BudgetOverviewContract.Effect.NavigateBack)
            BudgetOverviewContract.Intent.AddExpenseClicked -> {
                sendEffect(BudgetOverviewContract.Effect.NavigateToAddExpense(uiState.value.budgetId))
            }
            is BudgetOverviewContract.Intent.CategoryClicked -> { /* Handle */ }
        }
    }

    private fun loadBudget(tripId: String, budgetId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId, budgetId = budgetId) }
            val budgetResult = repository.getBudget(tripId, budgetId)
            val expensesResult = repository.getExpenses(budgetId)
            
            if (budgetResult.isSuccess) {
                setState { 
                    copy(
                        isLoading = false, 
                        budget = budgetResult.getOrNull(),
                        expenses = expensesResult.getOrDefault(emptyList()),
                        error = null
                    ) 
                }
            } else {
                setState { copy(isLoading = false, error = budgetResult.exceptionOrNull()?.message) }
            }
        }
    }
}
