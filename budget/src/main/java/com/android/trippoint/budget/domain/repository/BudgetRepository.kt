package com.android.trippoint.budget.domain.repository

import com.android.trippoint.budget.domain.model.Budget
import com.android.trippoint.budget.domain.model.Expense
import com.android.trippoint.core.network.BudgetSummaryDto
import com.android.trippoint.core.network.BudgetOverviewDto
import com.android.trippoint.core.network.ExpenseFilterInput
import com.android.trippoint.core.network.CreateExpenseInput
import com.android.trippoint.core.network.UpdateExpenseInput
import com.android.trippoint.core.network.DailyExpenseReportDto
import com.android.trippoint.core.network.CategoryExpenseReportDto
import com.android.trippoint.core.network.SettlementSummaryDto

@Suppress("LongParameterList")
interface BudgetRepository {
    // Budget
    suspend fun getBudgets(): Result<List<Budget>>
    suspend fun getBudget(tripId: String): Result<Budget>
    suspend fun createBudget(tripId: String, totalAmount: Double, currency: String): Result<Budget>
    suspend fun updateBudget(tripId: String, totalAmount: Double?, currency: String?): Result<Budget>
    suspend fun getBudgetSummary(tripId: String): Result<BudgetSummaryDto>
    suspend fun getBudgetOverview(tripId: String): Result<BudgetOverviewDto>

    // Expenses
    suspend fun getExpenses(tripId: String, filter: ExpenseFilterInput? = null): Result<List<Expense>>
    suspend fun getExpense(tripId: String, expenseId: String): Result<Expense>
    suspend fun createExpense(tripId: String, input: CreateExpenseInput): Result<Expense>
    suspend fun updateExpense(tripId: String, expenseId: String, input: UpdateExpenseInput): Result<Expense>
    suspend fun archiveExpense(tripId: String, expenseId: String): Result<Boolean>

    // Reports
    suspend fun getDailyExpenseReport(
        tripId: String, 
        fromDate: String, 
        toDate: String
    ): Result<List<DailyExpenseReportDto>>

    suspend fun getCategoryExpenseReport(
        tripId: String, 
        fromDate: String, 
        toDate: String
    ): Result<List<CategoryExpenseReportDto>>

    suspend fun getSettlementSummary(tripId: String): Result<SettlementSummaryDto>

    suspend fun getTripMembers(tripId: String): Result<List<com.android.trippoint.core.common.model.TripMember>>

    fun getCurrentUserId(): String?
}
