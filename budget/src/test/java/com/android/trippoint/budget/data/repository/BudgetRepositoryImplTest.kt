package com.android.trippoint.budget.data.repository

import com.android.trippoint.core.network.BudgetDto
import com.android.trippoint.core.network.BudgetRemoteDataSource
import com.android.trippoint.core.network.ExpenseDto
import com.android.trippoint.core.network.CreateExpenseInput
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BudgetRepositoryImplTest {

    private val remoteDataSource: BudgetRemoteDataSource = mockk()
    private lateinit var repository: BudgetRepositoryImpl

    private val dummyBudgetDto = BudgetDto(
        id = "b1",
        tripId = "t1",
        totalAmount = 5000.0,
        currency = "USD",
        locked = false,
        createdBy = "u1",
        createdAt = "2024-01-01",
        updatedAt = "2024-01-01"
    )

    private val dummyExpenseDto = ExpenseDto(
        id = "e1",
        tripId = "t1",
        budgetId = "b1",
        bookingId = null,
        category = "FOOD",
        title = "Dinner",
        description = "Tasty food",
        amount = 50.0,
        currency = "USD",
        exchangeRate = 1.0,
        convertedAmount = 50.0,
        expenseDate = "2024-09-05",
        paymentMethod = "CASH",
        paidBy = "u1",
        createdBy = "u1",
        recurring = false,
        recurrenceRule = null,
        archived = false,
        createdAt = "2024-09-05",
        updatedAt = "2024-09-05"
    )

    @Before
    fun setUp() {
        repository = BudgetRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getBudgets returns remote data`() = runTest {
        coEvery { remoteDataSource.getBudgets() } returns listOf(dummyBudgetDto)

        val result = repository.getBudgets()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("b1", result.getOrNull()?.first()?.id)
    }

    @Test
    fun `getBudget returns remote data`() = runTest {
        coEvery { remoteDataSource.getBudget("t1") } returns dummyBudgetDto

        val result = repository.getBudget("t1")

        assertTrue(result.isSuccess)
        assertEquals("b1", result.getOrNull()?.id)
    }

    @Test
    fun `getExpenses returns remote expenses`() = runTest {
        coEvery { remoteDataSource.getExpenses("t1", any()) } returns listOf(dummyExpenseDto)

        val result = repository.getExpenses("t1", null)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("e1", result.getOrNull()?.first()?.id)
    }

    @Test
    fun `createExpense adds remote expense and returns it`() = runTest {
        val input = CreateExpenseInput(
            category = "FOOD", title = "Dinner", description = "Tasty", 
            amount = 50.0, currency = "USD", expenseDate = "now", 
            paymentMethod = "CASH", paidBy = "u1", recurring = false, recurrenceRule = null
        )
        coEvery { remoteDataSource.createExpense("t1", any()) } returns dummyExpenseDto

        val result = repository.createExpense("t1", input)
        
        assertTrue(result.isSuccess)
        assertEquals("e1", result.getOrNull()?.id)
    }

    @Test
    fun `createBudget calls remote and returns success`() = runTest {
        coEvery { remoteDataSource.createBudget("t1", any()) } returns dummyBudgetDto

        val result = repository.createBudget("t1", 1000.0, "USD")
        
        assertTrue(result.isSuccess)
        assertEquals("b1", result.getOrNull()?.id)
    }

    @Test
    fun `repository handles remote exceptions`() = runTest {
        coEvery { remoteDataSource.getBudgets() } throws Exception("API Error")

        val result = repository.getBudgets()

        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }
}
