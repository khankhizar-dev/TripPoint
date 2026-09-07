package com.android.trippoint.budget.data.repository

import com.android.trippoint.budget.domain.model.Budget
import com.android.trippoint.budget.domain.model.BudgetCategory
import com.android.trippoint.budget.domain.model.Expense
import com.android.trippoint.budget.domain.repository.BudgetRepository

class BudgetRepositoryImpl : BudgetRepository {
    
    private val mockBudgets = listOf(
        Budget(
            id = "b1",
            tripId = "t1",
            totalAmount = 5000.0,
            spentAmount = 3200.0,
            title = "Bali Summer Trip",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            categories = listOf(
                BudgetCategory("c1", "Food", 1000.0, 800.0),
                BudgetCategory("c2", "Transport", 2000.0, 1500.0),
                BudgetCategory("c3", "Activities", 2000.0, 900.0)
            )
        ),
        Budget(
            id = "b2",
            tripId = "t2",
            totalAmount = 2500.0,
            spentAmount = 1200.0,
            title = "London Business",
            createdAt = "2024-02-01",
            updatedAt = "2024-02-01"
        )
    )

    private val mockExpenses = mutableListOf(
        Expense("e1", "b1", 45.0, "USD", "Food", "2024-05-12", "Lunch at Beach Club", createdAt = "2024-05-12"),
        Expense("e2", "b1", 120.0, "USD", "Transport", "2024-05-13", "Private Cab", createdAt = "2024-05-13"),
        Expense("e3", "b2", 80.0, "GBP", "Food", "2024-06-01", "Dinner meeting", createdAt = "2024-06-01")
    )

    override suspend fun getBudgets(tripId: String?): Result<List<Budget>> {
        return if (tripId == null) Result.success(mockBudgets)
        else Result.success(mockBudgets.filter { it.tripId == tripId })
    }

    override suspend fun getBudget(tripId: String, budgetId: String): Result<Budget> {
        val budget = mockBudgets.find { it.id == budgetId }
        return if (budget != null) Result.success(budget)
        else Result.failure(Exception("Budget not found"))
    }

    override suspend fun createBudget(
        tripId: String,
        title: String,
        totalAmount: Double,
        currency: String
    ): Result<Budget> {
        return Result.failure(Exception("Not implemented"))
    }

    override suspend fun getExpenses(budgetId: String): Result<List<Expense>> {
        return Result.success(mockExpenses.filter { it.budgetId == budgetId })
    }

    override suspend fun addExpense(
        budgetId: String,
        amount: Double,
        category: String,
        date: String,
        description: String
    ): Result<Expense> {
        val newExpense = Expense(
            id = "e${mockExpenses.size + 1}",
            budgetId = budgetId,
            amount = amount,
            currency = "USD", // Default
            category = category,
            date = date,
            description = description,
            createdAt = date
        )
        mockExpenses.add(newExpense)
        return Result.success(newExpense)
    }
}
