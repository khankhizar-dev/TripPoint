package com.android.trippoint.core.common.domain.provider

interface BudgetStatsProvider {
    suspend fun getTripBudgetSummary(tripId: String): Result<String>
}
