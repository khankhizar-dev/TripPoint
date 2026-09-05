package com.android.trippoint.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class MviTest {

    @Test
    fun `verify mvi interfaces can be implemented`() {
        data class TestState(val value: Int) : UiState
        class TestIntent : UiIntent
        class TestEffect : UiEffect

        class TestMviViewModel : MviViewModel<TestState, TestIntent, TestEffect> {
            override val uiState: TestState = TestState(1)
            override fun onIntent(intent: TestIntent) {}
        }

        val viewModel = TestMviViewModel()
        assertEquals(1, viewModel.uiState.value)
    }
}
