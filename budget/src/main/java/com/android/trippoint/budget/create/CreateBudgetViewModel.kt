package com.android.trippoint.budget.create

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class CreateBudgetViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<
    CreateBudgetContract.State,
    CreateBudgetContract.Intent,
    CreateBudgetContract.Effect
>(
    CreateBudgetContract.State()
) {
    override fun onIntent(intent: CreateBudgetContract.Intent) {
        when (intent) {
            is CreateBudgetContract.Intent.LoadTripId -> setState { copy(tripId = intent.tripId) }
            is CreateBudgetContract.Intent.AmountChanged -> setState { copy(amount = intent.value) }
            is CreateBudgetContract.Intent.CurrencyChanged -> setState { copy(currency = intent.value) }
            CreateBudgetContract.Intent.SaveClicked -> createBudget()
            CreateBudgetContract.Intent.BackClicked -> sendEffect(CreateBudgetContract.Effect.NavigateBack)
        }
    }

    private fun createBudget() {
        val state = uiState.value
        val amountValue = state.amount.toDoubleOrNull() ?: 0.0
        
        if (amountValue <= 0) {
            setState { copy(error = "Enter a valid budget amount") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.createBudget(
                tripId = state.tripId,
                totalAmount = amountValue,
                currency = state.currency
            )
            if (result.isSuccess) {
                setState { copy(isLoading = false) }
                sendEffect(CreateBudgetContract.Effect.BudgetCreated(result.getOrThrow()))
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
