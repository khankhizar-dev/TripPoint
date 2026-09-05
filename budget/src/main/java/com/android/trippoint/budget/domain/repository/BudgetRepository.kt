package com.android.trippoint.budget.domain.repository

import com.android.trippoint.budget.domain.model.Budget
import com.android.trippoint.budget.domain.model.Expense

interface BudgetRepository {
    suspend fun getBudgets(tripId: String? = null): Result<List<Budget>>
    suspend fun getBudget(tripId: String, budgetId: String): Result<Budget>
    suspend fun createBudget(tripId: String, title: String, totalAmount: Double, currency: String): Result<Budget>
    suspend fun getExpenses(budgetId: String): Result<List<Expense>>
    suspend fun addExpense(
        budgetId: String,
        amount: Double,
        category: String,
        date: String,
        description: String
    ): Result<Expense>
}
