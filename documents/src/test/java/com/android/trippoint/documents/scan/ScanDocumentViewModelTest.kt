package com.android.trippoint.documents.scan

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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScanDocumentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ScanDocumentViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScanDocumentViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        assertTrue(state.isAutoCaptureEnabled)
        assertFalse(state.isFlashEnabled)
        assertFalse(state.isProcessing)
        assertNull(state.error)
    }

    @Test
    fun `AutoCaptureToggled updates state`() = runTest {
        viewModel.onIntent(ScanDocumentContract.Intent.AutoCaptureToggled(false))
        runCurrent()
        assertFalse(viewModel.uiState.value.isAutoCaptureEnabled)

        viewModel.onIntent(ScanDocumentContract.Intent.AutoCaptureToggled(true))
        runCurrent()
        assertTrue(viewModel.uiState.value.isAutoCaptureEnabled)
    }

    @Test
    fun `FlashToggled toggles state`() = runTest {
        viewModel.onIntent(ScanDocumentContract.Intent.FlashToggled)
        runCurrent()
        assertTrue(viewModel.uiState.value.isFlashEnabled)

        viewModel.onIntent(ScanDocumentContract.Intent.FlashToggled)
        runCurrent()
        assertFalse(viewModel.uiState.value.isFlashEnabled)
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ScanDocumentContract.Intent.BackClicked)
            assertEquals(ScanDocumentContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `CaptureClicked processes and sends DocumentCaptured effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(ScanDocumentContract.Intent.CaptureClicked)
            
            // Advance enough to trigger the first setState
            runCurrent()
            assertTrue(viewModel.uiState.value.isProcessing)
            
            // Advance time to skip delay(1500)
            advanceTimeBy(1501)
            runCurrent()
            
            assertFalse(viewModel.uiState.value.isProcessing)
            assertEquals(ScanDocumentContract.Effect.DocumentCaptured("mock://document_uri"), awaitItem())
        }
    }
}
