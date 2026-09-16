package com.android.trippoint.trip.collaboration.discussion

import app.cash.turbine.test
import com.android.trippoint.trip.collaboration.domain.model.ChatMessage
import com.android.trippoint.trip.collaboration.domain.repository.CollaborationRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscussionViewModelTest {

    private val repository: CollaborationRepository = mockk()
    private lateinit var viewModel: DiscussionViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val dummyMessage = ChatMessage(
        id = "1",
        tripId = "trip1",
        senderId = "user1",
        senderName = "Rohan",
        senderPhotoUrl = null,
        content = "Hello",
        timestamp = "10:30 AM",
        isMe = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.observeMessages(any()) } returns emptyFlow()
        viewModel = DiscussionViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMessages success updates state and scrolls to bottom`() = runTest {
        coEvery { repository.getMessages("trip1") } returns Result.success(listOf(dummyMessage))

        viewModel.onIntent(DiscussionContract.Intent.LoadMessages("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.messages.size)
            assertEquals("Hello", state.messages[0].content)
        }

        viewModel.effect.test {
            assertEquals(DiscussionContract.Effect.ScrollToBottom, awaitItem())
        }
    }

    @Test
    fun `sendMessage success clears input and reply`() = runTest {
        coEvery { repository.getMessages(any(), any(), any()) } returns Result.success(emptyList())
        coEvery { repository.sendMessage(any(), any(), any(), any()) } returns Result.success(dummyMessage)

        viewModel.onIntent(DiscussionContract.Intent.LoadMessages("trip1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DiscussionContract.Intent.MessageChanged("New message"))
        viewModel.onIntent(DiscussionContract.Intent.ReplyToMessage(dummyMessage))
        
        viewModel.onIntent(DiscussionContract.Intent.SendMessage)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.currentMessage)
            assertEquals(null, state.replyTo)
        }
    }

    @Test
    fun `cancelReply updates state`() = runTest {
        viewModel.onIntent(DiscussionContract.Intent.ReplyToMessage(dummyMessage))
        viewModel.onIntent(DiscussionContract.Intent.CancelReply)

        viewModel.uiState.test {
            assertEquals(null, awaitItem().replyTo)
        }
    }

    @Test
    fun `backClicked sends NavigateBack effect`() = runTest {
        viewModel.onIntent(DiscussionContract.Intent.BackClicked)

        viewModel.effect.test {
            assertEquals(DiscussionContract.Effect.NavigateBack, awaitItem())
        }
    }
}
