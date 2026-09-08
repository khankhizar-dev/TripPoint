package com.android.trippoint.documents.list

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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DocumentListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: DocumentRepository = mockk()
    private lateinit var viewModel: DocumentListViewModel

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
            isFavorite = true,
            createdAt = "now",
            updatedAt = "now"
        ),
        Document(
            id = "d2",
            userId = "u1",
            title = "Flight Ticket",
            type = DocumentType.TICKETS_BOARDING,
            fileUrl = "url2",
            expiryDate = null,
            referenceNumber = "REF2",
            notes = null,
            isFavorite = false,
            createdAt = "now",
            updatedAt = "now",
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getDocuments(any(), any()) } returns Result.success(mockDocuments)
        viewModel = DocumentListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading false and empty list`() {
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(emptyList<Document>(), state.documents)
        assertEquals(emptyList<Document>(), state.recentDocuments)
        assertNull(state.error)
    }

    @Test
    fun `LoadDocuments success updates state`() = runTest {
        coEvery { repository.getDocuments("t1", any()) } returns Result.success(mockDocuments)

        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments("t1"))
        
        runCurrent()
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(mockDocuments, state.documents)
        assertEquals(2, state.recentDocuments.size)
        assertNull(state.error)
    }

    @Test
    fun `LoadDocuments failure updates error state`() = runTest {
        coEvery { repository.getDocuments("t1", any()) } returns Result.failure(Exception("Network error"))

        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments("t1"))
        
        runCurrent()
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Network error", viewModel.uiState.value.error)
    }

    @Test
    fun `SearchQueryChanged filters documents`() = runTest {
        coEvery { repository.getDocuments("t1", any()) } returns Result.success(mockDocuments)
        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments("t1"))
        runCurrent()
        
        viewModel.onIntent(DocumentListContract.Intent.SearchQueryChanged("Pass"))
        
        val state = viewModel.uiState.value
        assertEquals("Pass", state.searchQuery)
        assertEquals(1, state.documents.size)
        assertEquals("Passport", state.documents[0].title)
    }

    @Test
    fun `TabSelected to Favorites filters documents`() = runTest {
        coEvery { repository.getDocuments("t1", any()) } returns Result.success(mockDocuments)
        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments("t1"))
        runCurrent()
        
        viewModel.onIntent(DocumentListContract.Intent.TabSelected(2)) // Favorites
        
        val state = viewModel.uiState.value
        assertEquals(2, state.selectedTab)
        assertEquals(1, state.documents.size)
        assertEquals("Passport", state.documents[0].title)
        assertTrue(state.documents[0].isFavorite)
    }

    @Test
    fun `Search and Tab combined filtering`() = runTest {
        coEvery { repository.getDocuments("t1", any()) } returns Result.success(mockDocuments)
        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments("t1"))
        runCurrent()
        
        viewModel.onIntent(DocumentListContract.Intent.TabSelected(2)) // Favorites
        viewModel.onIntent(DocumentListContract.Intent.SearchQueryChanged("Flight"))
        
        val state = viewModel.uiState.value
        assertEquals(0, state.documents.size) // "Flight" is not favorite
    }

    @Test
    fun `ToggleFavorite success reloads documents`() = runTest {
        coEvery { repository.getDocuments("t1", any()) } returns Result.success(mockDocuments)
        val toggledDoc = mockDocuments[0].copy(isFavorite = false)
        coEvery { repository.toggleFavorite("t1", "d1", false) } returns Result.success(toggledDoc)
        
        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments("t1"))
        runCurrent()

        viewModel.onIntent(DocumentListContract.Intent.ToggleFavorite("d1"))
        
        runCurrent()
        // Verify repository calls
        io.mockk.coVerify { repository.toggleFavorite("t1", "d1", false) }
        io.mockk.coVerify(atLeast = 1) { repository.getDocuments("t1", any()) }
    }

    @Test
    fun `DocumentClicked intent sends NavigateToDetails effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(DocumentListContract.Intent.DocumentClicked("d1"))
            assertEquals(DocumentListContract.Effect.NavigateToDetails("d1"), awaitItem())
        }
    }

    @Test
    fun `ViewAllRecentClicked intent sends NavigateToCategories effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(DocumentListContract.Intent.ViewAllRecentClicked)
            assertEquals(DocumentListContract.Effect.NavigateToCategories, awaitItem())
        }
    }

    @Test
    fun `CategoryClicked intent sends NavigateToCategories effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(DocumentListContract.Intent.CategoryClicked(DocumentType.PASSPORT_VISA))
            assertEquals(DocumentListContract.Effect.NavigateToCategories, awaitItem())
        }
    }

    @Test
    fun `AddDocumentClicked intent sends NavigateToAddDocument effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(DocumentListContract.Intent.AddDocumentClicked)
            assertEquals(DocumentListContract.Effect.NavigateToAddDocument, awaitItem())
        }
    }

    @Test
    fun `DocumentLongClicked toggles selection and enables selection mode`() = runTest {
        coEvery { repository.getDocuments("t1", any()) } returns Result.success(mockDocuments)
        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments("t1"))
        runCurrent()

        // Toggle on
        viewModel.onIntent(DocumentListContract.Intent.DocumentLongClicked("d1"))
        
        var state = viewModel.uiState.value
        assertTrue(state.isSelectionMode)
        assertTrue(state.selectedDocumentIds.contains("d1"))
        assertEquals(1, state.selectedDocumentIds.size)

        // Toggle off
        viewModel.onIntent(DocumentListContract.Intent.DocumentLongClicked("d1"))
        
        state = viewModel.uiState.value
        assertEquals(false, state.isSelectionMode)
        assertTrue(state.selectedDocumentIds.isEmpty())
    }

    @Test
    fun `ClearSelection clears selection and disables selection mode`() = runTest {
        coEvery { repository.getDocuments("t1", any()) } returns Result.success(mockDocuments)
        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments("t1"))
        runCurrent()

        // Select two
        viewModel.onIntent(DocumentListContract.Intent.DocumentLongClicked("d1"))
        viewModel.onIntent(DocumentListContract.Intent.DocumentLongClicked("d2"))
        
        assertTrue(viewModel.uiState.value.isSelectionMode)
        assertEquals(2, viewModel.uiState.value.selectedDocumentIds.size)

        // Clear
        viewModel.onIntent(DocumentListContract.Intent.ClearSelection)
        
        val state = viewModel.uiState.value
        assertEquals(false, state.isSelectionMode)
        assertTrue(state.selectedDocumentIds.isEmpty())
    }
    
    @Test
    fun `BackClicked in selection mode clears selection`() = runTest {
        viewModel.onIntent(DocumentListContract.Intent.DocumentLongClicked("d1"))
        assertTrue(viewModel.uiState.value.isSelectionMode)

        viewModel.onIntent(DocumentListContract.Intent.BackClicked)
        
        assertEquals(false, viewModel.uiState.value.isSelectionMode)
        assertTrue(viewModel.uiState.value.selectedDocumentIds.isEmpty())
    }

    @Test
    fun `BackClicked intent sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(DocumentListContract.Intent.BackClicked)
            assertEquals(DocumentListContract.Effect.NavigateBack, awaitItem())
        }
    }
}
