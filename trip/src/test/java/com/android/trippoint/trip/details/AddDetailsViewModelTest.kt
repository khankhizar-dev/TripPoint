package com.android.trippoint.trip.details

import app.cash.turbine.test
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
class AddDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AddDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddDetailsViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load trip initializes sections`() {
        viewModel.onIntent(AddDetailsContract.Intent.LoadTrip("1"))
        assertEquals("1", viewModel.uiState.value.tripId)
        assertEquals(6, viewModel.uiState.value.sections.size)
    }

    @Test
    fun `toggle section updates state`() {
        viewModel.onIntent(AddDetailsContract.Intent.LoadTrip("1"))
        val firstSectionId = viewModel.uiState.value.sections.first().id
        
        viewModel.onIntent(AddDetailsContract.Intent.ToggleSection(firstSectionId))
        assertTrue(viewModel.uiState.value.sections.first().isAdded)
        
        viewModel.onIntent(AddDetailsContract.Intent.ToggleSection(firstSectionId))
        assertEquals(false, viewModel.uiState.value.sections.first().isAdded)
    }

    @Test
    fun `save and continue sends effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(AddDetailsContract.Intent.SaveAndContinueClicked)
            assertEquals(AddDetailsContract.Effect.NavigateToHome, awaitItem())
        }
    }
}
