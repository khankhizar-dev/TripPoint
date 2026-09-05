package com.android.trippoint.trip.create

import app.cash.turbine.test
import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.trip.domain.repository.TripRepository
import io.mockk.coEvery
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
class CreateTripViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TripRepository = mockk()
    private lateinit var viewModel: CreateTripViewModel

    private val dummyTrip = Trip(
        id = "1",
        ownerId = "owner1",
        title = "Bali",
        location = "Indonesia",
        startDate = "2025-01-01",
        endDate = "2025-01-10",
        status = TripStatus.DRAFT,
        imageUrl = "",
        progress = 0f
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateTripViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `form updates state`() {
        viewModel.onIntent(CreateTripContract.Intent.NameChanged("Dubai"))
        viewModel.onIntent(CreateTripContract.Intent.DestinationChanged("UAE"))
        viewModel.onIntent(CreateTripContract.Intent.StartDateChanged("2026-01-01"))
        viewModel.onIntent(CreateTripContract.Intent.EndDateChanged("2026-01-05"))

        val state = viewModel.uiState.value
        assertEquals("Dubai", state.name)
        assertEquals("UAE", state.destination)
        assertEquals("2026-01-01", state.startDate)
        assertEquals("2026-01-05", state.endDate)
    }

    @Test
    fun `next click creates trip and navigates`() = runTest {
        coEvery { repository.createTrip(any(), any(), any(), any()) } returns Result.success(dummyTrip)

        viewModel.effect.test {
            viewModel.onIntent(CreateTripContract.Intent.NextClicked)
            assertEquals(CreateTripContract.Effect.NavigateToAddDetails("1"), awaitItem())
        }
    }

    @Test
    fun `back click sends effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(CreateTripContract.Intent.BackClicked)
            assertEquals(CreateTripContract.Effect.NavigateBack, awaitItem())
        }
    }
}
