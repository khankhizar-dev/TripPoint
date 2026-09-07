package com.android.trippoint.budget.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BudgetRepositoryImplTest {

    private lateinit var repository: BudgetRepositoryImpl

    @Before
    fun setUp() {
        repository = BudgetRepositoryImpl()
    }

    @Test
    fun `getBudgets without tripId returns all budgets`() = runTest {
        val result = repository.getBudgets(null)
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `getBudgets with tripId returns filtered budgets`() = runTest {
        val result = repository.getBudgets("t1")
        assertTrue(result.isSuccess)
        val budgets = result.getOrNull()
        assertEquals(1, budgets?.size)
        assertEquals("b1", budgets?.first()?.id)
    }

    @Test
    fun `getBudget with valid id returns budget`() = runTest {
        val result = repository.getBudget("t1", "b1")
        assertTrue(result.isSuccess)
        assertEquals("Bali Summer Trip", result.getOrNull()?.title)
    }

    @Test
    fun `getBudget with invalid id returns failure`() = runTest {
        val result = repository.getBudget("t1", "invalid")
        assertTrue(result.isFailure)
        assertEquals("Budget not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExpenses returns expenses for budget`() = runTest {
        val result = repository.getExpenses("b1")
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `addExpense adds new expense and returns it`() = runTest {
        val budgetId = "b1"
        val amount = 50.0
        val category = "Food"
        val date = "2024-09-05"
        val description = "Dinner"

        val result = repository.addExpense(budgetId, amount, category, date, description)
        
        assertTrue(result.isSuccess)
        val expense = result.getOrNull()
        assertEquals(budgetId, expense?.budgetId)
        assertEquals(amount, expense?.amount)
        assertEquals(category, expense?.category)
        assertEquals(date, expense?.date)
        assertEquals(description, expense?.description)
        
        // Verify it was added to the mock list
        val expenses = repository.getExpenses(budgetId).getOrNull()
        assertTrue(expenses?.any { it.id == expense?.id } == true)
    }

    @Test
    fun `createBudget returns failure as not implemented`() = runTest {
        val result = repository.createBudget("t1", "New Budget", 1000.0, "USD")
        assertTrue(result.isFailure)
        assertEquals("Not implemented", result.exceptionOrNull()?.message)
    }
}
