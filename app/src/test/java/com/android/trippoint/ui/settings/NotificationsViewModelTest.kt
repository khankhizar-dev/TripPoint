package com.android.trippoint.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NotificationsViewModelTest {

    private lateinit var viewModel: NotificationsViewModel

    @Before
    fun setUp() {
        viewModel = NotificationsViewModel()
    }

    @Test
    fun `initial state has defaults`() {
        val state = viewModel.uiState.value
        assertEquals(true, state.isPushEnabled)
        assertEquals(true, state.isEmailEnabled)
    }

    @Test
    fun `toggling push updates state`() {
        viewModel.onIntent(NotificationsContract.Intent.PushToggled(false))
        assertEquals(false, viewModel.uiState.value.isPushEnabled)
    }

    @Test
    fun `toggling email updates state`() {
        viewModel.onIntent(NotificationsContract.Intent.EmailToggled(false))
        assertEquals(false, viewModel.uiState.value.isEmailEnabled)
    }
}
