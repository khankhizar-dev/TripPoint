package com.android.trippoint.budget.reports

import androidx.lifecycle.viewModelScope
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class BudgetReportsViewModel(
    private val repository: BudgetRepository
) : BaseViewModel<BudgetReportsContract.State, BudgetReportsContract.Intent, BudgetReportsContract.Effect>(
    BudgetReportsContract.State()
) {
    override fun onIntent(intent: BudgetReportsContract.Intent) {
        when (intent) {
            is BudgetReportsContract.Intent.LoadReports -> {
                setState { copy(tripId = intent.tripId, budgetId = intent.budgetId) }
            }
            BudgetReportsContract.Intent.BackClicked -> sendEffect(BudgetReportsContract.Effect.NavigateBack)
            is BudgetReportsContract.Intent.DownloadReport -> generateReport(intent.format, "Downloading")
            is BudgetReportsContract.Intent.ShareReport -> generateReport(intent.format, "Sharing")
        }
    }

    private fun generateReport(format: String, action: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // Real logic for report generation could go here
            setState { copy(isLoading = false) }
            sendEffect(BudgetReportsContract.Effect.ShowSuccess("$action $format report successful"))
        }
    }
}
