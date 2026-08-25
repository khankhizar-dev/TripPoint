package com.android.trippoint.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SupportViewModelTest {

    private lateinit var viewModel: SupportViewModel

    @Before
    fun setUp() {
        viewModel = SupportViewModel()
    }

    @Test
    fun `initial state has defaults`() {
        assertEquals(false, viewModel.uiState.value.isLoading)
    }
}
