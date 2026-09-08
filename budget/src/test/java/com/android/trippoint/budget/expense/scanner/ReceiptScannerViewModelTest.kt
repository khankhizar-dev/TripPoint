package com.android.trippoint.budget.expense.scanner

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class ReceiptScannerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ReceiptScannerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ReceiptScannerViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default`() {
        val state = viewModel.uiState.value
        assertEquals("", state.budgetId)
        assertEquals(false, state.isProcessing)
        assertNull(state.scannedData)
        assertNull(state.error)
    }

    @Test
    fun `LoadBudgetId intent updates state`() {
        viewModel.onIntent(ReceiptScannerContract.Intent.LoadBudgetId("budget_123"))
        assertEquals("budget_123", viewModel.uiState.value.budgetId)
    }

    @Test
    fun `BackClicked intent sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ReceiptScannerContract.Intent.BackClicked)
            assertEquals(ReceiptScannerContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `CaptureClicked intent simulates AI extraction and navigates`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ReceiptScannerContract.Intent.CaptureClicked)
            
            // Should show processing
            runCurrent()
            assertEquals(true, viewModel.uiState.value.isProcessing)
            assertNull(viewModel.uiState.value.error)
            
            // Wait for simulated delay
            advanceTimeBy(3001)
            runCurrent()
            
            val expectedData = ReceiptScannerContract.ScannedReceipt(
                amount = 42.50,
                currency = "USD",
                date = "2026-09-01T12:00:00",
                category = "Food",
                merchant = "Starlight Cafe"
            )
            
            assertEquals(false, viewModel.uiState.value.isProcessing)
            assertEquals(expectedData, viewModel.uiState.value.scannedData)
            assertEquals(ReceiptScannerContract.Effect.NavigateToConfirmExpense(expectedData), awaitItem())
        }
    }
}
