package com.android.trippoint.documents.add

import app.cash.turbine.test
import com.android.trippoint.documents.domain.model.Document
import com.android.trippoint.documents.domain.model.DocumentType
import com.android.trippoint.documents.domain.repository.DocumentRepository
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
class AddDocumentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: DocumentRepository = mockk()
    private lateinit var viewModel: AddDocumentViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddDocumentViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default`() {
        val state = viewModel.uiState.value
        assertEquals("", state.title)
        assertEquals(DocumentType.PASSPORT_VISA, state.type)
        assertEquals("", state.expiryDate)
        assertEquals("", state.referenceNumber)
        assertNull(state.fileUrl)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `field change intents update state`() {
        viewModel.onIntent(AddDocumentContract.Intent.TitleChanged("My Passport"))
        assertEquals("My Passport", viewModel.uiState.value.title)

        viewModel.onIntent(AddDocumentContract.Intent.TypeChanged(DocumentType.INSURANCE))
        assertEquals(DocumentType.INSURANCE, viewModel.uiState.value.type)

        viewModel.onIntent(AddDocumentContract.Intent.ExpiryChanged("2026-12-31"))
        assertEquals("2026-12-31", viewModel.uiState.value.expiryDate)

        viewModel.onIntent(AddDocumentContract.Intent.RefChanged("REF123"))
        assertEquals("REF123", viewModel.uiState.value.referenceNumber)

        viewModel.onIntent(AddDocumentContract.Intent.FileSelected("content://file"))
        assertEquals("content://file", viewModel.uiState.value.fileUrl)
    }

    @Test
    fun `SaveClicked with missing title shows error`() {
        viewModel.onIntent(AddDocumentContract.Intent.FileSelected("url"))
        viewModel.onIntent(AddDocumentContract.Intent.SaveClicked)
        assertEquals("Title and file are required", viewModel.uiState.value.error)
    }

    @Test
    fun `SaveClicked with missing file shows error`() {
        viewModel.onIntent(AddDocumentContract.Intent.TitleChanged("Title"))
        viewModel.onIntent(AddDocumentContract.Intent.SaveClicked)
        assertEquals("Title and file are required", viewModel.uiState.value.error)
    }

    @Test
    fun `SaveClicked success sends DocumentAdded effect`() = runTest {
        val mockDoc = mockk<Document>()
        coEvery { repository.uploadDocument(any(), any(), any(), any()) } returns Result.success(mockDoc)

        viewModel.onIntent(AddDocumentContract.Intent.TitleChanged("Passport"))
        viewModel.onIntent(AddDocumentContract.Intent.FileSelected("url"))
        
        viewModel.effect.test {
            viewModel.onIntent(AddDocumentContract.Intent.SaveClicked)
            runCurrent()
            
            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(AddDocumentContract.Effect.DocumentAdded, awaitItem())
        }
    }

    @Test
    fun `SaveClicked failure updates error state`() = runTest {
        coEvery {
            repository.uploadDocument(any(), any(), any(), any())
        } returns Result.failure(Exception("Upload failed"))

        viewModel.onIntent(AddDocumentContract.Intent.TitleChanged("Passport"))
        viewModel.onIntent(AddDocumentContract.Intent.FileSelected("url"))
        
        viewModel.onIntent(AddDocumentContract.Intent.SaveClicked)
        runCurrent()
        
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Upload failed", viewModel.uiState.value.error)
    }

    @Test
    fun `BackClicked intent sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(AddDocumentContract.Intent.BackClicked)
            assertEquals(AddDocumentContract.Effect.NavigateBack, awaitItem())
        }
    }
}
