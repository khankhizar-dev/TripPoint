package com.android.trippoint.trip.collaboration.data.repository

import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.trip.collaboration.domain.model.ActivityLog
import com.android.trippoint.trip.collaboration.domain.model.ActivityTarget
import com.android.trippoint.trip.collaboration.domain.model.ChatAttachment
import com.android.trippoint.trip.collaboration.domain.model.ChatMessage
import com.android.trippoint.trip.collaboration.domain.model.MessageType
import com.android.trippoint.trip.collaboration.domain.repository.CollaborationRepository
import com.android.trippoint.core.network.ActivityLogDto
import com.android.trippoint.core.network.ChatAttachmentDto
import com.android.trippoint.core.network.ChatMessageDto
import com.android.trippoint.core.network.CollaborationRemoteDataSource
import com.android.trippoint.core.network.MessagePaginationInput
import com.android.trippoint.core.network.SendMessageInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class CollaborationRepositoryImpl(
    private val remoteDataSource: CollaborationRemoteDataSource,
    private val authRepository: AuthRepository
) : CollaborationRepository {

    private val _messageFlow = MutableSharedFlow<ChatMessage>()
    private val _activityFlow = MutableSharedFlow<ActivityLog>()

    override suspend fun getMessages(
        tripId: String,
        limit: Int?,
        beforeCursor: String?
    ): Result<List<ChatMessage>> = runCatching {
        val pagination = MessagePaginationInput(limit = limit, beforeCursor = beforeCursor)
        remoteDataSource.getMessages(tripId, pagination).map { it.toDomain() }
    }

    override fun observeMessages(tripId: String): Flow<ChatMessage> = _messageFlow.asSharedFlow()

    override suspend fun sendMessage(
        tripId: String,
        content: String,
        type: String,
        replyToId: String?
    ): Result<ChatMessage> = runCatching {
        val input = SendMessageInput(content = content, type = type, replyToId = replyToId)
        val dto = remoteDataSource.sendMessage(tripId, input) ?: throw Exception("Failed to send message")
        val domain = dto.toDomain()
        _messageFlow.emit(domain)
        domain
    }

    override suspend fun deleteMessage(tripId: String, messageId: String): Result<Boolean> = runCatching {
        remoteDataSource.deleteMessage(tripId, messageId)
    }

    override suspend fun getActivityLogs(
        tripId: String,
        limit: Int?,
        beforeCursor: String?
    ): Result<List<ActivityLog>> = runCatching {
        remoteDataSource.getActivityLogs(tripId, limit, beforeCursor).map { it.toDomain() }
    }

    override fun observeActivityFeed(tripId: String): Flow<ActivityLog> = _activityFlow.asSharedFlow()

    private fun ChatMessageDto.toDomain() = ChatMessage(
        id = id,
        tripId = tripId,
        senderId = senderId,
        senderName = senderName,
        senderPhotoUrl = senderPhotoUrl,
        content = content,
        type = try { MessageType.valueOf(type) } catch (_: Exception) { MessageType.TEXT },
        attachments = attachments?.map { it.toDomain() } ?: emptyList(),
        timestamp = timestamp,
        isMe = isMe,
        replyToId = replyToId,
        replyToContent = replyToContent,
        deleted = deleted
    )

    private fun ChatAttachmentDto.toDomain() = ChatAttachment(
        id = id,
        name = name,
        url = url,
        mimeType = mimeType,
        fileSize = fileSize
    )

    private fun ActivityLogDto.toDomain() = ActivityLog(
        id = id,
        tripId = tripId,
        userId = userId,
        userName = userName,
        userPhotoUrl = userPhotoUrl,
        action = action,
        targetType = try { ActivityTarget.valueOf(targetType) } catch (_: Exception) { ActivityTarget.TRIP },
        targetName = targetName,
        timestamp = timestamp
    )
}
