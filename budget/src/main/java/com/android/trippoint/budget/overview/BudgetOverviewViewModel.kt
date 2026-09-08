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
            val overviewResult = repository.getBudgetOverview(tripId)
            val expensesResult = repository.getExpenses(tripId)
            
            if (summaryResult.isSuccess && overviewResult.isSuccess) {
                val summary = summaryResult.getOrThrow()
                val overview = overviewResult.getOrThrow()
                val expenses = expensesResult.getOrDefault(emptyList())
                
                val categorySum = overview.categoryBreakdown.sumOf { it.amount }
                val expenseSum = expenses.sumOf { it.amount }
                val spentAmount = maxOf(summary.spentAmount, categorySum, expenseSum)

                val budget = Budget(
                    id = summary.budget.id,
                    tripId = summary.budget.tripId,
                    totalAmount = summary.budget.totalAmount,
                    spentAmount = spentAmount,
                    currency = summary.budget.currency,
                    title = "Budget",
                    categories = overview.categoryBreakdown.map { 
                        BudgetCategory(
                            id = it.category,
                            name = it.category,
                            spentAmount = it.amount,
                            allocatedAmount = summary.budget.totalAmount // Fallback
                        )
                    },
                    createdAt = summary.budget.createdAt,
                    updatedAt = summary.budget.updatedAt
                )

                setState { 
                    copy(
                        isLoading = false, 
                        budget = budget,
                        expenses = expenses,
                        error = null
                    ) 
                }
            } else {
                // If combined fails, try individual budget fetch
                val budgetResult = repository.getBudget(tripId)
                val expenses = expensesResult.getOrDefault(emptyList())
                
                if (budgetResult.isSuccess) {
                    val budgetDto = budgetResult.getOrNull()
                    val spentAmount = expenses.sumOf { it.amount }
                    
                    setState { 
                        copy(
                            isLoading = false, 
                            budget = budgetDto?.copy(spentAmount = spentAmount),
                            expenses = expenses,
                            error = null
                        ) 
                    }
                } else {
                    val error = budgetResult.exceptionOrNull()?.message ?: summaryResult.exceptionOrNull()?.message
                    if (error?.contains("not found", ignoreCase = true) == true) {
                        setState { copy(isLoading = false, budget = null, error = null) }
                    } else {
                        setState { copy(isLoading = false, error = error) }
                    }
                }
            }
        }
    }
}
