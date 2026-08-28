package com.android.trippoint.trip.invite

import app.cash.turbine.test
import com.android.trippoint.trip.domain.repository.TripRepository
import io.mockk.mockk
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
class InvitePeopleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TripRepository = mockk()
    private lateinit var viewModel: InvitePeopleViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = InvitePeopleViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load trip updates state`() {
        viewModel.onIntent(InvitePeopleContract.Intent.LoadTrip("1"))
        assertEquals("1", viewModel.uiState.value.tripId)
    }

    @Test
    fun `manual input updates state`() {
        viewModel.onIntent(InvitePeopleContract.Intent.ManualInputChanged("test@test.com"))
        assertEquals("test@test.com", viewModel.uiState.value.manualInput)
    }

    @Test
    fun `next click sends effect`() = runTest {
        viewModel.onIntent(InvitePeopleContract.Intent.LoadTrip("1"))
        viewModel.effect.test {
            viewModel.onIntent(InvitePeopleContract.Intent.NextClicked)
            assertEquals(InvitePeopleContract.Effect.NavigateToSummary("1"), awaitItem())
        }
    }
}
