package com.android.trippoint.trip.collaboration.data.repository

import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.network.ActivityLogDto
import com.android.trippoint.core.network.ChatMessageDto
import com.android.trippoint.core.network.CollaborationRemoteDataSource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CollaborationRepositoryImplTest {

    private val remoteDataSource: CollaborationRemoteDataSource = mockk()
    private val authRepository: AuthRepository = mockk()
    private lateinit var repository: CollaborationRepositoryImpl

    private val dummyMessageDto = ChatMessageDto(
        id = "1",
        tripId = "trip1",
        senderId = "user1",
        senderName = "Rohan",
        senderPhotoUrl = null,
        content = "Hello",
        type = "TEXT",
        attachments = emptyList(),
        replyToId = null,
        replyToContent = null,
        timestamp = "10:30 AM",
        isMe = true,
        deleted = false
    )

    private val dummyActivityDto = ActivityLogDto(
        id = "1",
        tripId = "trip1",
        userId = "user1",
        userName = "Rohan",
        userPhotoUrl = null,
        action = "updated task",
        targetType = "TASK",
        targetName = "Book flights",
        timestamp = "10:30 AM"
    )

    @Before
    fun setUp() {
        every { authRepository.getUserId() } returns "user1"
        repository = CollaborationRepositoryImpl(remoteDataSource, authRepository)
    }

    @Test
    fun `getMessages returns success domain list`() = runTest {
        coEvery { remoteDataSource.getMessages("trip1", any()) } returns listOf(dummyMessageDto)

        val result = repository.getMessages("trip1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Hello", result.getOrNull()?.first()?.content)
    }

    @Test
    fun `sendMessage returns success domain model`() = runTest {
        coEvery { remoteDataSource.sendMessage("trip1", any()) } returns dummyMessageDto

        val result = repository.sendMessage("trip1", "Hello")

        assertTrue(result.isSuccess)
        assertEquals("Hello", result.getOrNull()?.content)
    }

    @Test
    fun `deleteMessage returns success boolean`() = runTest {
        coEvery { remoteDataSource.deleteMessage("trip1", "1") } returns true

        val result = repository.deleteMessage("trip1", "1")

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `getActivityLogs returns success domain list`() = runTest {
        coEvery { remoteDataSource.getActivityLogs("trip1", any(), any()) } returns listOf(dummyActivityDto)

        val result = repository.getActivityLogs("trip1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("updated task", result.getOrNull()?.first()?.action)
    }
}
