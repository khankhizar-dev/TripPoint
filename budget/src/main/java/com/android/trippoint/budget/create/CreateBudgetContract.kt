package com.android.trippoint.budget.create

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.budget.domain.model.Budget

class CreateBudgetContract {
    sealed class Intent : UiIntent {
        data class LoadTripId(val tripId: String) : Intent()
        data class AmountChanged(val value: String) : Intent()
        data class CurrencyChanged(val value: String) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val amount: String = "",
        val currency: String = "INR",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class BudgetCreated(val budget: Budget) : Effect()
    }
}
