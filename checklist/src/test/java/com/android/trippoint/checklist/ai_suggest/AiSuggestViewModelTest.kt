package com.android.trippoint.checklist.ai_suggest

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
class AiSuggestViewModelTest {

    private lateinit var viewModel: AiSuggestViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AiSuggestViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSuggestions updates state with mock data`() = runTest {
        viewModel.onIntent(AiSuggestContract.Intent.LoadSuggestions("trip1"))
        
        viewModel.uiState.test {
            // The first state might be the initial one or the loading one depending on timing
            var state = awaitItem()
            if (!state.isLoading) {
                state = awaitItem()
            }
            assertTrue(state.isLoading)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            val successState = awaitItem()
            assertEquals(false, successState.isLoading)
            assertEquals(4, successState.suggestions.size)
            assertEquals("Thailand", successState.location)
        }
    }

    @Test
    fun `itemToggled updates selection`() = runTest {
        viewModel.onIntent(AiSuggestContract.Intent.LoadSuggestions("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AiSuggestContract.Intent.ItemToggled("s1"))
        
        viewModel.uiState.test {
            val state = awaitItem()
            val item = state.suggestions.find { it.id == "s1" }
            assertEquals(false, item?.isSelected) // Mock data starts as true? Let's check
        }
    }

    @Test
    fun `addAllClicked sends ItemsAdded effect`() = runTest {
        viewModel.onIntent(AiSuggestContract.Intent.AddAllClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            assertEquals(AiSuggestContract.Effect.ItemsAdded, awaitItem())
        }
    }
}
