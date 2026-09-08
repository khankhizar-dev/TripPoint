package com.android.trippoint.budget.settlement

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class SettlementViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<
    SettlementContract.State,
    SettlementContract.Intent,
    SettlementContract.Effect
>(
    SettlementContract.State()
) {
    override fun onIntent(intent: SettlementContract.Intent) {
        when (intent) {
            is SettlementContract.Intent.LoadSettlements -> loadSettlements(intent.tripId)
            SettlementContract.Intent.BackClicked -> sendEffect(SettlementContract.Effect.NavigateBack)
        }
    }

    private fun loadSettlements(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId) }
            val result = repository.getSettlementSummary(tripId)
            if (result.isSuccess) {
                setState { copy(isLoading = false, summary = result.getOrNull(), error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
