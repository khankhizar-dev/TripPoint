package com.android.trippoint.trip.collaboration.domain.model

data class ChatMessage(
    val id: String,
    val tripId: String,
    val senderId: String,
    val senderName: String,
    val senderPhotoUrl: String?,
    val content: String,
    val type: MessageType = MessageType.TEXT,
    val attachments: List<ChatAttachment> = emptyList(),
    val timestamp: String,
    val isMe: Boolean = false,
    val replyToId: String? = null,
    val replyToContent: String? = null,
    val deleted: Boolean = false
)

enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    SYSTEM
}

data class ChatAttachment(
    val id: String,
    val name: String,
    val url: String,
    val mimeType: String,
    val fileSize: String? = null
)

data class ActivityLog(
    val id: String,
    val tripId: String,
    val userId: String,
    val userName: String,
    val userPhotoUrl: String?,
    val action: String,
    val targetType: ActivityTarget,
    val targetName: String,
    val timestamp: String,
    val metadata: Map<String, String>? = null
)

enum class ActivityTarget {
    TRIP,
    TASK,
    BOOKING,
    EXPENSE,
    DOCUMENT,
    MEMBER,
    CHAT
}
