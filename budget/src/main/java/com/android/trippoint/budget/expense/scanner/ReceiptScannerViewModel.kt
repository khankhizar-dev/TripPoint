package com.android.trippoint.budget.expense.scanner

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReceiptScannerViewModel : BaseViewModel<
    ReceiptScannerContract.State,
    ReceiptScannerContract.Intent,
    ReceiptScannerContract.Effect
>(
    ReceiptScannerContract.State()
) {
    override fun onIntent(intent: ReceiptScannerContract.Intent) {
        when (intent) {
            is ReceiptScannerContract.Intent.LoadBudgetId -> setState { copy(budgetId = intent.budgetId) }
            ReceiptScannerContract.Intent.BackClicked -> sendEffect(ReceiptScannerContract.Effect.NavigateBack)
            ReceiptScannerContract.Intent.CaptureClicked -> simulateAiExtraction()
        }
    }

    private fun simulateAiExtraction() {
        viewModelScope.launch {
            setState { copy(isProcessing = true, error = null) }
            
            // Simulating AI processing delay
            delay(3000)
            
            val mockScannedData = ReceiptScannerContract.ScannedReceipt(
                amount = 42.50,
                currency = "USD",
                date = "2026-09-01T12:00:00",
                category = "Food",
                merchant = "Starlight Cafe"
            )
            
            setState { copy(isProcessing = false, scannedData = mockScannedData) }
            sendEffect(ReceiptScannerContract.Effect.NavigateToConfirmExpense(mockScannedData))
        }
    }
}
