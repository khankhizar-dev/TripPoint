package com.android.trippoint.documents.categories

import app.cash.turbine.test
import com.android.trippoint.documents.domain.model.CategoryInfo
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
class DocumentCategoriesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: DocumentRepository = mockk()
    private lateinit var viewModel: DocumentCategoriesViewModel

    private val mockDocuments = listOf(
        Document(
            id = "d1",
            userId = "u1",
            title = "Passport",
            type = DocumentType.PASSPORT_VISA,
            fileUrl = "url1",
            expiryDate = "2030-01-01",
            referenceNumber = "REF1",
            notes = null,
            createdAt = "now",
            updatedAt = "now"
        ),
        Document(
            id = "d2",
            userId = "u1",
            title = "Ticket",
            type = DocumentType.TICKET_BOARDING,
            fileUrl = "url2",
            expiryDate = null,
            referenceNumber = "REF2",
            notes = null,
            createdAt = "now",
            updatedAt = "now"
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DocumentCategoriesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading false and empty categories`() {
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(emptyList<CategoryInfo>(), state.categories)
        assertNull(state.error)
    }

    @Test
    fun `LoadCategories success updates state with correct counts`() = runTest {
        coEvery { repository.getDocuments() } returns Result.success(mockDocuments)

        viewModel.onIntent(DocumentCategoriesContract.Intent.LoadCategories)
        
        runCurrent()
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(6, state.categories.size)
        assertEquals(1, state.categories.find { it.type == DocumentType.PASSPORT_VISA }?.count)
        assertEquals(1, state.categories.find { it.type == DocumentType.TICKET_BOARDING }?.count)
        assertEquals(0, state.categories.find { it.type == DocumentType.ID_PROOFS }?.count)
        assertNull(state.error)
    }

    @Test
    fun `LoadCategories failure updates error state`() = runTest {
        coEvery { repository.getDocuments() } returns Result.failure(Exception("Error loading"))

        viewModel.onIntent(DocumentCategoriesContract.Intent.LoadCategories)
        
        runCurrent()
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Error loading", viewModel.uiState.value.error)
    }

    @Test
    fun `CategoryClicked intent sends NavigateToDocumentsByType effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(DocumentCategoriesContract.Intent.CategoryClicked(DocumentType.PASSPORT_VISA))
            val expectedEffect = DocumentCategoriesContract.Effect.NavigateToDocumentsByType(DocumentType.PASSPORT_VISA)
            assertEquals(expectedEffect, awaitItem())
        }
    }

    @Test
    fun `BackClicked intent sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(DocumentCategoriesContract.Intent.BackClicked)
            assertEquals(DocumentCategoriesContract.Effect.NavigateBack, awaitItem())
        }
    }
}
