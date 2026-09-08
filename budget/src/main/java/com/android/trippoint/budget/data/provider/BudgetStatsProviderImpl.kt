package com.android.trippoint.budget.data.provider

import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.domain.provider.BudgetStatsProvider

class BudgetStatsProviderImpl(
    private val repository: BudgetRepository
) : BudgetStatsProvider {
    override suspend fun getTripBudgetSummary(tripId: String): Result<String> {
        return repository.getBudgetSummary(tripId).map { 
            "${it.budget.currency} ${it.budget.totalAmount}"
        }
    }
}
