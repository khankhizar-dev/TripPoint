package com.android.trippoint.core.common

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    sealed class TestState : UiState {
        object Initial : TestState()
        object Changed : TestState()
    }

    sealed class TestIntent : UiIntent {
        object ChangeState : TestIntent()
        object EmitEffect : TestIntent()
    }

    sealed class TestEffect : UiEffect {
        object Effect : TestEffect()
    }

    class TestViewModel : BaseViewModel<TestState, TestIntent, TestEffect>(TestState.Initial) {
        override fun onIntent(intent: TestIntent) {
            when (intent) {
                TestIntent.ChangeState -> setState { TestState.Changed }
                TestIntent.EmitEffect -> sendEffect(TestEffect.Effect)
            }
        }
    }

    private lateinit var viewModel: TestViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TestViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        assertEquals(TestState.Initial, viewModel.uiState.value)
    }

    @Test
    fun `setState updates state correctly`() = runTest {
        viewModel.onIntent(TestIntent.ChangeState)
        assertEquals(TestState.Changed, viewModel.uiState.value)
    }

    @Test
    fun `sendEffect emits effect correctly`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(TestIntent.EmitEffect)
            assertEquals(TestEffect.Effect, awaitItem())
        }
    }
}
