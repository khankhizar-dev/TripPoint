package com.android.trippoint.documents.search

import app.cash.turbine.test
import com.android.trippoint.documents.domain.model.DocumentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchFilterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SearchFilterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchFilterViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertNull(state.selectedCategory)
        assertEquals("All", state.selectedExpiryPeriod)
        assertEquals("", state.issuedBy)
        assertFalse(state.isLoading)
    }

    @Test
    fun `SearchQueryChanged updates state`() = runTest {
        viewModel.onIntent(SearchFilterContract.Intent.SearchQueryChanged("Passport"))
        runCurrent()
        assertEquals("Passport", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `CategorySelected updates state`() = runTest {
        viewModel.onIntent(SearchFilterContract.Intent.CategorySelected(DocumentType.PASSPORT_VISA))
        runCurrent()
        assertEquals(DocumentType.PASSPORT_VISA, viewModel.uiState.value.selectedCategory)
        
        viewModel.onIntent(SearchFilterContract.Intent.CategorySelected(null))
        runCurrent()
        assertNull(viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `ExpirySelected updates state`() = runTest {
        viewModel.onIntent(SearchFilterContract.Intent.ExpirySelected("30 Days"))
        runCurrent()
        assertEquals("30 Days", viewModel.uiState.value.selectedExpiryPeriod)
    }

    @Test
    fun `IssuerChanged updates state`() = runTest {
        viewModel.onIntent(SearchFilterContract.Intent.IssuerChanged("Gov"))
        runCurrent()
        assertEquals("Gov", viewModel.uiState.value.issuedBy)
    }

    @Test
    fun `ResetFilters resets state to default`() = runTest {
        viewModel.onIntent(SearchFilterContract.Intent.SearchQueryChanged("Passport"))
        viewModel.onIntent(SearchFilterContract.Intent.CategorySelected(DocumentType.PASSPORT_VISA))
        viewModel.onIntent(SearchFilterContract.Intent.ResetFilters)
        runCurrent()
        
        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertNull(state.selectedCategory)
        assertEquals("All", state.selectedExpiryPeriod)
    }

    @Test
    fun `BackClicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(SearchFilterContract.Intent.BackClicked)
            assertEquals(SearchFilterContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `ApplyFilters sends FiltersApplied effect with current state`() = runTest {
        viewModel.onIntent(SearchFilterContract.Intent.SearchQueryChanged("Passport"))
        viewModel.onIntent(SearchFilterContract.Intent.CategorySelected(DocumentType.PASSPORT_VISA))
        
        viewModel.effect.test {
            viewModel.onIntent(SearchFilterContract.Intent.ApplyFilters)
            val effect = awaitItem() as SearchFilterContract.Effect.FiltersApplied
            assertEquals("Passport", effect.state.searchQuery)
            assertEquals(DocumentType.PASSPORT_VISA, effect.state.selectedCategory)
        }
    }
}
