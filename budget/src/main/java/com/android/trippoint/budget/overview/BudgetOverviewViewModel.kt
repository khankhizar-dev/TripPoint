package com.android.trippoint.budget.overview

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.model.Budget
import com.android.trippoint.budget.domain.model.BudgetCategory
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
            val summaryResult = repository.getBudgetSummary(tripId)
            val expensesResult = repository.getExpenses(tripId)
            
            if (summaryResult.isSuccess) {
                val summary = summaryResult.getOrThrow()
                val budget = Budget(
                    id = summary.budget.id,
                    tripId = summary.budget.tripId,
                    totalAmount = summary.budget.totalAmount,
                    spentAmount = summary.spentAmount,
                    currency = summary.budget.currency,
                    title = "Trip Budget",
                    categories = summary.categoryBreakdown.map { 
                        BudgetCategory(it.category, it.category, 0.0, it.amount)
                    },
                    createdAt = summary.budget.createdAt,
                    updatedAt = summary.budget.updatedAt
                )

                setState { 
                    copy(
                        isLoading = false, 
                        budget = budget,
                        expenses = expensesResult.getOrDefault(emptyList()),
                        error = null
                    ) 
                }
            } else {
                setState { copy(isLoading = false, error = summaryResult.exceptionOrNull()?.message) }
            }
        }
    }
}
