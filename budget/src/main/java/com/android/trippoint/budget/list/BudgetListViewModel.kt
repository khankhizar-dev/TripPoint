package com.android.trippoint.budget.list

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class BudgetListViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<BudgetListContract.State, BudgetListContract.Intent, BudgetListContract.Effect>(
    BudgetListContract.State()
) {
    override fun onIntent(intent: BudgetListContract.Intent) {
        when (intent) {
            is BudgetListContract.Intent.LoadBudgets -> loadBudgets(intent.tripId)
            is BudgetListContract.Intent.BudgetClicked -> {
                sendEffect(BudgetListContract.Effect.NavigateToBudgetDetails(intent.tripId, intent.budgetId))
            }
            BudgetListContract.Intent.CreateBudgetClicked -> {
                sendEffect(BudgetListContract.Effect.NavigateToCreateBudget)
            }
            BudgetListContract.Intent.BackClicked -> sendEffect(BudgetListContract.Effect.NavigateBack)
        }
    }

    private fun loadBudgets(tripId: String?) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = if (tripId != null) {
                // Fetch summary to get spentAmount
                val summaryResult = repository.getBudgetSummary(tripId)
                if (summaryResult.isSuccess) {
                    val summary = summaryResult.getOrThrow()
                    val expensesResult = repository.getExpenses(tripId)
                    val expenses = expensesResult.getOrDefault(emptyList())
                    
                    // Intelligent summation to ensure accuracy
                    val categorySum = summary.categoryBreakdown.sumOf { it.amount }
                    val expenseSum = expenses.sumOf { it.amount }
                    val spentAmount = maxOf(summary.spentAmount, categorySum, expenseSum)

                    val budget = com.android.trippoint.budget.domain.model.Budget(
                        id = summary.budget.id,
                        tripId = summary.budget.tripId,
                        totalAmount = summary.budget.totalAmount,
                        spentAmount = spentAmount,
                        currency = summary.budget.currency,
                        title = "Budget",
                        createdAt = summary.budget.createdAt,
                        updatedAt = summary.budget.updatedAt
                    )
                    Result.success(listOf(budget))
                } else {
                    // Fallback to simple budget fetch
                    repository.getBudget(tripId).map { listOf(it) }
                }
            } else {
                repository.getBudgets()
            }
            
            if (result.isSuccess) {
                setState {
                    copy(
                        isLoading = false,
                        budgets = result.getOrDefault(emptyList()),
                        error = null
                    )
                }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
