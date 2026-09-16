package com.android.trippoint.trip.members

import app.cash.turbine.test
import com.android.trippoint.core.common.model.InvitationStatus
import com.android.trippoint.core.common.model.TravelerRole
import com.android.trippoint.core.common.model.TripMember
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripMembersViewModelTest {

    private val repository: TripRepository = mockk()
    private lateinit var viewModel: TripMembersViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val dummyMember = TripMember(
        id = "1",
        tripId = "trip1",
        userId = "user1",
        userName = "Rohan",
        role = TravelerRole.ORGANIZER,
        status = InvitationStatus.ACCEPTED,
        invitedAt = "2026-09-13T10:30:00Z",
        joinedAt = "2026-09-13T10:35:00Z"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TripMembersViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMembers success updates state`() = runTest {
        coEvery { repository.getTripMembers("trip1") } returns Result.success(listOf(dummyMember))

        viewModel.onIntent(TripMembersContract.Intent.LoadMembers("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertEquals(1, state.members.size)
            assertEquals("Rohan", state.members[0].userName)
        }
    }

    @Test
    fun `loadMembers failure updates error`() = runTest {
        coEvery { repository.getTripMembers("trip1") } returns Result.failure(Exception("Error"))

        viewModel.onIntent(TripMembersContract.Intent.LoadMembers("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertEquals("Error", state.error)
        }
    }

    @Test
    fun `removeMember success reloads members`() = runTest {
        coEvery { repository.removeTripMember("trip1", "user1") } returns Result.success(true)
        coEvery { repository.getTripMembers("trip1") } returns Result.success(emptyList())

        viewModel.onIntent(TripMembersContract.Intent.LoadMembers("trip1")) // To set tripId
        viewModel.onIntent(TripMembersContract.Intent.RemoveMember("user1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0, state.members.size)
        }
    }

    @Test
    fun `searchMembers filters the list`() = runTest {
        val members = listOf(
            dummyMember,
            dummyMember.copy(id = "2", userId = "user2", userName = "Anita")
        )
        coEvery { repository.getTripMembers("trip1") } returns Result.success(members)

        viewModel.onIntent(TripMembersContract.Intent.LoadMembers("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(TripMembersContract.Intent.SearchMembers("Anita"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.filteredMembers.size)
            assertEquals("Anita", state.filteredMembers[0].userName)
        }
    }

    @Test
    fun `removeMember failure updates error`() = runTest {
        coEvery { repository.getTripMembers("trip1") } returns Result.success(listOf(dummyMember))
        coEvery { repository.removeTripMember("trip1", "user1") } returns Result.failure(Exception("Delete Failed"))

        viewModel.onIntent(TripMembersContract.Intent.LoadMembers("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(TripMembersContract.Intent.RemoveMember("user1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals("Delete Failed", awaitItem().error)
        }
    }

    @Test
    fun `backClicked sends NavigateBack effect`() = runTest {
        viewModel.onIntent(TripMembersContract.Intent.BackClicked)

        viewModel.effect.test {
            assertEquals(TripMembersContract.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `inviteClicked sends NavigateToInvite effect`() = runTest {
        coEvery { repository.getTripMembers("trip1") } returns Result.success(listOf(dummyMember))
        
        viewModel.onIntent(TripMembersContract.Intent.LoadMembers("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(TripMembersContract.Intent.InviteClicked)

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is TripMembersContract.Effect.NavigateToInvite)
            assertEquals("trip1", (effect as TripMembersContract.Effect.NavigateToInvite).tripId)
        }
    }
}
