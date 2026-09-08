package com.android.trippoint.budget.expense.add

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.network.CreateExpenseInput
import kotlinx.coroutines.launch

class AddExpenseViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<AddExpenseContract.State, AddExpenseContract.Intent, AddExpenseContract.Effect>(
    AddExpenseContract.State()
) {
    override fun onIntent(intent: AddExpenseContract.Intent) {
        when (intent) {
            is AddExpenseContract.Intent.LoadIds -> loadInitialData(intent.tripId, intent.budgetId)
            is AddExpenseContract.Intent.AmountChanged -> setState { copy(amount = intent.value) }
            is AddExpenseContract.Intent.CategoryChanged -> setState { copy(category = intent.value) }
            is AddExpenseContract.Intent.DateChanged -> setState { copy(date = intent.value) }
            is AddExpenseContract.Intent.DescriptionChanged -> setState { copy(description = intent.value) }
            is AddExpenseContract.Intent.PaidByChanged -> setState { copy(paidBy = intent.value) }
            AddExpenseContract.Intent.SaveClicked -> saveExpense()
            AddExpenseContract.Intent.BackClicked -> sendEffect(AddExpenseContract.Effect.NavigateBack)
        }
    }

    private fun loadInitialData(tripId: String, budgetId: String) {
        setState { 
            copy(
                tripId = tripId, 
                budgetId = budgetId,
                paidBy = repository.getCurrentUserId() ?: ""
            ) 
        }
        viewModelScope.launch {
            val result = repository.getTripMembers(tripId)
            if (result.isSuccess) {
                setState { copy(members = result.getOrDefault(emptyList())) }
            }
        }
    }

    private fun saveExpense() {
        val state = uiState.value
        val amountValue = state.amount.toDoubleOrNull() ?: 0.0
        
        if (amountValue <= 0) {
            setState { copy(error = "Enter a valid amount") }
            return
        }

        if (state.paidBy.isBlank()) {
            setState { copy(error = "Please specify who paid") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val input = CreateExpenseInput(
                category = state.category.uppercase(),
                title = state.description.ifBlank { state.category },
                description = state.description,
                amount = amountValue,
                currency = "USD", 
                expenseDate = "${state.date}T12:00:00", // Append time for LocalDateTime parsing
                paymentMethod = "CASH", 
                paidBy = state.paidBy, 
                recurring = false,
                recurrenceRule = null
            )
            
            val result = repository.createExpense(state.tripId, input)
            if (result.isSuccess) {
                setState { copy(isLoading = false) }
                sendEffect(AddExpenseContract.Effect.ExpenseAdded)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
