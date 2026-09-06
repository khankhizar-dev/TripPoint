package com.android.trippoint.budget.expense.scanner

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ReceiptScannerContract {
    sealed class Intent : UiIntent {
        data class LoadBudgetId(val budgetId: String) : Intent()
        object CaptureClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val budgetId: String = "",
        val isProcessing: Boolean = false,
        val scannedData: ScannedReceipt? = null,
        val error: String? = null
    ) : UiState

    data class ScannedReceipt(
        val amount: Double,
        val currency: String,
        val date: String,
        val category: String,
        val merchant: String
    )

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToConfirmExpense(val receipt: ScannedReceipt) : Effect()
    }
}
