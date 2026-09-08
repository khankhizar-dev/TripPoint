package com.android.trippoint.budget.expense.add

import app.cash.turbine.test
import com.android.trippoint.budget.domain.model.Expense
import com.android.trippoint.budget.domain.repository.BudgetRepository
import io.mockk.coEvery
import io.mockk.every
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
        every { repository.getCurrentUserId() } returns "u1"
        coEvery { repository.getTripMembers(any()) } returns Result.success(emptyList())
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
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `LoadIds updates state correctly`() = runTest {
        viewModel.onIntent(AddExpenseContract.Intent.LoadIds("t1", "b1"))
        runCurrent()
        assertEquals("t1", viewModel.uiState.value.tripId)
        assertEquals("b1", viewModel.uiState.value.budgetId)
        assertEquals("u1", viewModel.uiState.value.paidBy)
    }

    @Test
    fun `field change intents update state`() {
        viewModel.onIntent(AddExpenseContract.Intent.AmountChanged("42.5"))
        assertEquals("42.5", viewModel.uiState.value.amount)

        viewModel.onIntent(AddExpenseContract.Intent.CategoryChanged("Transport"))
        assertEquals("Transport", viewModel.uiState.value.category)

        viewModel.onIntent(AddExpenseContract.Intent.DateChanged("2026-09-01"))
        assertEquals("2026-09-01", viewModel.uiState.value.date)

        viewModel.onIntent(AddExpenseContract.Intent.DescriptionChanged("Taxi"))
        assertEquals("Taxi", viewModel.uiState.value.description)
    }

    @Test
    fun `SaveClicked with invalid amount shows error`() {
        viewModel.onIntent(AddExpenseContract.Intent.AmountChanged("abc"))
        viewModel.onIntent(AddExpenseContract.Intent.SaveClicked)
        assertEquals("Enter a valid amount", viewModel.uiState.value.error)
    }

    @Test
    fun `SaveClicked success sends ExpenseAdded effect`() = runTest {
        val mockExpense = mockk<Expense>()
        coEvery { repository.createExpense(any(), any()) } returns Result.success(mockExpense)

        viewModel.onIntent(AddExpenseContract.Intent.LoadIds("t1", "b1"))
        runCurrent()
        viewModel.onIntent(AddExpenseContract.Intent.AmountChanged("10.0"))
        viewModel.onIntent(AddExpenseContract.Intent.DateChanged("2026-01-01"))
        
        viewModel.effect.test {
            viewModel.onIntent(AddExpenseContract.Intent.SaveClicked)
            runCurrent()
            
            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(AddExpenseContract.Effect.ExpenseAdded, awaitItem())
        }
    }

    @Test
    fun `SaveClicked failure updates error state`() = runTest {
        coEvery {
            repository.createExpense(any(), any())
        } returns Result.failure(Exception("Creation failed"))

        viewModel.onIntent(AddExpenseContract.Intent.LoadIds("t1", "b1"))
        runCurrent()
        viewModel.onIntent(AddExpenseContract.Intent.AmountChanged("10.0"))
        viewModel.onIntent(AddExpenseContract.Intent.DateChanged("2026-01-01"))
        
        viewModel.onIntent(AddExpenseContract.Intent.SaveClicked)
        runCurrent()
        
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Creation failed", viewModel.uiState.value.error)
    }

    @Test
    fun `BackClicked intent sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(AddExpenseContract.Intent.BackClicked)
            assertEquals(AddExpenseContract.Effect.NavigateBack, awaitItem())
        }
    }
}
