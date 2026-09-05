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
            val result = repository.getBudgets(tripId)
            if (result.isSuccess) {
                setState { copy(isLoading = false, budgets = result.getOrDefault(emptyList()), error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
