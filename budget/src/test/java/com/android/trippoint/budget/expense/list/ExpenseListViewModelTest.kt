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
    fun `LoadExpenses success updates state`() = runTest {
        val mockExpenses = listOf(mockk<Expense>())
        coEvery { repository.getExpenses("t1", any()) } returns Result.success(mockExpenses)

        viewModel.onIntent(ExpenseListContract.Intent.LoadExpenses("t1", "b1"))
        
        runCurrent()
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(mockExpenses, state.expenses)
        assertEquals("t1", state.tripId)
        assertEquals("b1", state.budgetId)
        assertNull(state.error)
    }

    @Test
    fun `LoadExpenses failure updates error state`() = runTest {
        coEvery { repository.getExpenses("t1", any()) } returns Result.failure(Exception("Network error"))

        viewModel.onIntent(ExpenseListContract.Intent.LoadExpenses("t1", "b1"))
        
        runCurrent()
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Network error", viewModel.uiState.value.error)
    }

    @Test
    fun `AddExpenseClicked intent sends NavigateToAddExpense effect`() = runTest {
        coEvery { repository.getExpenses("t1", any()) } returns Result.success(emptyList())
        viewModel.onIntent(ExpenseListContract.Intent.LoadExpenses("t1", "b1"))
        runCurrent()
        
        viewModel.effect.test {
            viewModel.onIntent(ExpenseListContract.Intent.AddExpenseClicked)
            assertEquals(ExpenseListContract.Effect.NavigateToAddExpense("b1"), awaitItem())
        }
    }

    @Test
    fun `BackClicked intent sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ExpenseListContract.Intent.BackClicked)
            assertEquals(ExpenseListContract.Effect.NavigateBack, awaitItem())
        }
    }
}
