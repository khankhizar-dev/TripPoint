package com.android.trippoint.itinerary.filter

import app.cash.turbine.test
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FilterSortViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ItineraryRepository = mockk()
    private lateinit var viewModel: FilterSortViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FilterSortViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleCategory updates state`() = runTest {
        viewModel.onIntent(FilterSortContract.Intent.ToggleCategory("Events"))
        assertTrue(viewModel.uiState.value.selectedCategories.contains("Events"))
        
        viewModel.onIntent(FilterSortContract.Intent.ToggleCategory("Events"))
        assertTrue(!viewModel.uiState.value.selectedCategories.contains("Events"))
    }

    @Test
    fun `sortByChanged updates state`() = runTest {
        viewModel.onIntent(FilterSortContract.Intent.SortByChanged("Priority"))
        assertEquals("Priority", viewModel.uiState.value.sortBy)
    }

    @Test
    fun `applyClicked sends back effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(FilterSortContract.Intent.ApplyClicked)
            assertEquals(FilterSortContract.Effect.NavigateBack, awaitItem())
        }
    }
}
