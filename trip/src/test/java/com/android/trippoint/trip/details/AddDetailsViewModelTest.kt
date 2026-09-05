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
        viewModel.onIntent(AddDetailsContract.Intent.LoadTrip("trip123"))
        assertEquals("trip123", viewModel.uiState.value.tripId)
        assertEquals(6, viewModel.uiState.value.sections.size)
    }

    @Test
    fun `clicking itinerary sends NavigateToItinerary effect`() = runTest {
        viewModel.onIntent(AddDetailsContract.Intent.LoadTrip("trip123"))
        viewModel.effect.test {
            viewModel.onIntent(AddDetailsContract.Intent.ToggleSection("itinerary"))
            assertEquals(AddDetailsContract.Effect.NavigateToItinerary("trip123"), awaitItem())
        }
    }

    @Test
    fun `clicking tasks sends NavigateToTasks effect`() = runTest {
        viewModel.onIntent(AddDetailsContract.Intent.LoadTrip("trip123"))
        viewModel.effect.test {
            viewModel.onIntent(AddDetailsContract.Intent.ToggleSection("tasks"))
            assertEquals(AddDetailsContract.Effect.NavigateToTasks("trip123"), awaitItem())
        }
    }

    @Test
    fun `clicking notes sends NavigateToNotes effect`() = runTest {
        viewModel.onIntent(AddDetailsContract.Intent.LoadTrip("trip123"))
        viewModel.effect.test {
            viewModel.onIntent(AddDetailsContract.Intent.ToggleSection("notes"))
            assertEquals(AddDetailsContract.Effect.NavigateToNotes("trip123"), awaitItem())
        }
    }

    @Test
    fun `clicking budget toggles added state`() {
        viewModel.onIntent(AddDetailsContract.Intent.LoadTrip("trip123"))
        
        viewModel.onIntent(AddDetailsContract.Intent.ToggleSection("budget"))
        assertTrue(viewModel.uiState.value.sections.find { it.id == "budget" }?.isAdded == true)
        
        viewModel.onIntent(AddDetailsContract.Intent.ToggleSection("budget"))
        assertEquals(false, viewModel.uiState.value.sections.find { it.id == "budget" }?.isAdded)
    }

    @Test
    fun `save and continue sends NavigateToHome effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(AddDetailsContract.Intent.SaveAndContinueClicked)
            assertEquals(AddDetailsContract.Effect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `back clicked sends NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(AddDetailsContract.Intent.BackClicked)
            assertEquals(AddDetailsContract.Effect.NavigateBack, awaitItem())
        }
    }
}
