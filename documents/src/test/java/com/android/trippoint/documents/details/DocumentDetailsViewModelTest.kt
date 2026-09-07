package com.android.trippoint.documents.details

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
class DocumentDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: DocumentRepository = mockk()
    private lateinit var viewModel: DocumentDetailsViewModel

    private val mockDoc = Document(
        id = "d1",
        userId = "u1",
        title = "Passport",
        type = DocumentType.PASSPORT_VISA,
        fileUrl = "url",
        expiryDate = "2030-01-01",
        referenceNumber = "REF1",
        notes = "Note",
        isFavorite = false,
        createdAt = "now",
        updatedAt = "now"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DocumentDetailsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadDocument success updates state`() = runTest {
        coEvery { repository.getDocument("d1") } returns Result.success(mockDoc)

        viewModel.onIntent(DocumentDetailsContract.Intent.LoadDocument("d1"))
        
        runCurrent()
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(mockDoc, state.document)
        assertNull(state.error)
    }

    @Test
    fun `LoadDocument failure updates error state`() = runTest {
        coEvery { repository.getDocument("d1") } returns Result.failure(Exception("Not found"))

        viewModel.onIntent(DocumentDetailsContract.Intent.LoadDocument("d1"))
        
        runCurrent()
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Not found", viewModel.uiState.value.error)
    }

    @Test
    fun `FavoriteClicked toggles favorite status`() = runTest {
        coEvery { repository.getDocument("d1") } returns Result.success(mockDoc)
        coEvery { repository.toggleFavorite("d1") } returns Result.success(true)

        viewModel.onIntent(DocumentDetailsContract.Intent.LoadDocument("d1"))
        runCurrent()

        viewModel.effect.test {
            viewModel.onIntent(DocumentDetailsContract.Intent.FavoriteClicked)
            runCurrent()
            
            assertEquals(true, viewModel.uiState.value.document?.isFavorite)
            assertEquals(DocumentDetailsContract.Effect.ShowMessage("Added to favorites"), awaitItem())
        }
    }

    @Test
    fun `DeleteClicked deletes and navigates back`() = runTest {
        coEvery { repository.getDocument("d1") } returns Result.success(mockDoc)
        coEvery { repository.deleteDocument("d1") } returns Result.success(true)

        viewModel.onIntent(DocumentDetailsContract.Intent.LoadDocument("d1"))
        runCurrent()

        viewModel.effect.test {
            viewModel.onIntent(DocumentDetailsContract.Intent.DeleteClicked)
            runCurrent()
            assertEquals(DocumentDetailsContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `BackClicked intent sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(DocumentDetailsContract.Intent.BackClicked)
            assertEquals(DocumentDetailsContract.Effect.NavigateBack, awaitItem())
        }
    }
}
