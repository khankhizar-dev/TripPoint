package com.android.trippoint.budget.expense.list

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
class ExpenseListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BudgetRepository = mockk()
    private lateinit var viewModel: ExpenseListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ExpenseListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default`() {
        val state = viewModel.uiState.value
        assertEquals("", state.budgetId)
        assertEquals(emptyList<Expense>(), state.expenses)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `LoadExpenses success updates state with expenses`() = runTest {
        val budgetId = "b1"
        val expenses = listOf(
            Expense("e1", budgetId, 10.0, "USD", "Food", "2024-01-01", "Lunch", createdAt = "2024-01-01")
        )
        coEvery { repository.getExpenses(budgetId) } returns Result.success(expenses)

        viewModel.onIntent(ExpenseListContract.Intent.LoadExpenses(budgetId))
        
        runCurrent()
        
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(expenses, viewModel.uiState.value.expenses)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `LoadExpenses failure updates state with error`() = runTest {
        val budgetId = "b1"
        val errorMessage = "Network Error"
        coEvery { repository.getExpenses(budgetId) } returns Result.failure(Exception(errorMessage))

        viewModel.onIntent(ExpenseListContract.Intent.LoadExpenses(budgetId))
        runCurrent()
        
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }

    @Test
    fun `AddExpenseClicked sends NavigateToAddExpense effect`() = runTest {
        val budgetId = "b1"
        coEvery { repository.getExpenses(budgetId) } returns Result.success(emptyList())
        viewModel.onIntent(ExpenseListContract.Intent.LoadExpenses(budgetId))
        runCurrent()

        viewModel.effect.test {
            viewModel.onIntent(ExpenseListContract.Intent.AddExpenseClicked)
            assertEquals(ExpenseListContract.Effect.NavigateToAddExpense(budgetId), awaitItem())
        }
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ExpenseListContract.Intent.BackClicked)
            assertEquals(ExpenseListContract.Effect.NavigateBack, awaitItem())
        }
    }
}
