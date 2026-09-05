package com.android.trippoint.budget.expense.add

import app.cash.turbine.test
import com.android.trippoint.budget.domain.model.Expense
import com.android.trippoint.budget.domain.repository.BudgetRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddExpenseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BudgetRepository = mockk()
    private lateinit var viewModel: AddExpenseViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddExpenseViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default`() {
        val state = viewModel.uiState.value
        assertEquals("", state.budgetId)
        assertEquals("", state.amount)
        assertEquals("Food", state.category)
        assertEquals("", state.date)
        assertEquals("", state.description)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `LoadBudgetId updates state`() {
        viewModel.onIntent(AddExpenseContract.Intent.LoadBudgetId("b1"))
        assertEquals("b1", viewModel.uiState.value.budgetId)
    }

    @Test
    fun `AmountChanged updates state`() {
        viewModel.onIntent(AddExpenseContract.Intent.AmountChanged("50.5"))
        assertEquals("50.5", viewModel.uiState.value.amount)
    }

    @Test
    fun `CategoryChanged updates state`() {
        viewModel.onIntent(AddExpenseContract.Intent.CategoryChanged("Transport"))
        assertEquals("Transport", viewModel.uiState.value.category)
    }

    @Test
    fun `DateChanged updates state`() {
        viewModel.onIntent(AddExpenseContract.Intent.DateChanged("2024-09-05"))
        assertEquals("2024-09-05", viewModel.uiState.value.date)
    }

    @Test
    fun `DescriptionChanged updates state`() {
        viewModel.onIntent(AddExpenseContract.Intent.DescriptionChanged("Taxi to airport"))
        assertEquals("Taxi to airport", viewModel.uiState.value.description)
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(AddExpenseContract.Intent.BackClicked)
            assertEquals(AddExpenseContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `SaveClicked with empty amount shows error`() {
        viewModel.onIntent(AddExpenseContract.Intent.SaveClicked)
        assertEquals("Enter a valid amount", viewModel.uiState.value.error)
    }

    @Test
    fun `SaveClicked with invalid amount shows error`() {
        viewModel.onIntent(AddExpenseContract.Intent.AmountChanged("abc"))
        viewModel.onIntent(AddExpenseContract.Intent.SaveClicked)
        assertEquals("Enter a valid amount", viewModel.uiState.value.error)
    }

    @Test
    fun `SaveClicked with zero amount shows error`() {
        viewModel.onIntent(AddExpenseContract.Intent.AmountChanged("0"))
        viewModel.onIntent(AddExpenseContract.Intent.SaveClicked)
        assertEquals("Enter a valid amount", viewModel.uiState.value.error)
    }

    @Test
    fun `successful save updates state and sends ExpenseAdded effect`() = runTest {
        val budgetId = "b1"
        val amount = 50.5
        val category = "Food"
        val date = "2024-09-05"
        val description = "Lunch"
        
        val expense = Expense(
            "e1", budgetId, amount, "USD", category, date, description, createdAt = date
        )
        
        coEvery { 
            repository.addExpense(budgetId, amount, category, date, description) 
        } returns Result.success(expense)

        viewModel.onIntent(AddExpenseContract.Intent.LoadBudgetId(budgetId))
        viewModel.onIntent(AddExpenseContract.Intent.AmountChanged(amount.toString()))
        viewModel.onIntent(AddExpenseContract.Intent.CategoryChanged(category))
        viewModel.onIntent(AddExpenseContract.Intent.DateChanged(date))
        viewModel.onIntent(AddExpenseContract.Intent.DescriptionChanged(description))
        
        viewModel.effect.test {
            viewModel.onIntent(AddExpenseContract.Intent.SaveClicked)
            
            runCurrent()
            
            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(AddExpenseContract.Effect.ExpenseAdded, awaitItem())
        }
    }

    @Test
    fun `failed save updates state with error`() = runTest {
        val errorMessage = "Save failed"
        coEvery { 
            repository.addExpense(any(), any(), any(), any(), any()) 
        } returns Result.failure(Exception(errorMessage))

        viewModel.onIntent(AddExpenseContract.Intent.AmountChanged("10"))
        viewModel.onIntent(AddExpenseContract.Intent.SaveClicked)
        
        runCurrent()
        
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }
}
