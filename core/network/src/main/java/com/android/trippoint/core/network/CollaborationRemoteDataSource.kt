package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class CollaborationRemoteDataSource(
    private val api: TripPointApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // 1. Get Activity Logs
    suspend fun getActivityLogs(tripId: String, limit: Int?, beforeCursor: String?): List<ActivityLogDto> {
        val query = """
            query ActivityLogs(${'$'}tripId: ID!, ${'$'}limit: Int, ${'$'}beforeCursor: ID) {
              activityLogs(tripId: ${'$'}tripId, limit: ${'$'}limit, beforeCursor: ${'$'}beforeCursor) {
                id
                tripId
                userId
                userName
                userPhotoUrl
                action
                targetType
                targetName
                timestamp
              }
            }
        """.trimIndent()
        val variables = mutableMapOf<String, Any?>(
            "tripId" to tripId,
            "limit" to limit,
            "beforeCursor" to beforeCursor
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("activityLogs") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(ActivityLogDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    // 2. Get Messages
    suspend fun getMessages(tripId: String, pagination: MessagePaginationInput?): List<ChatMessageDto> {
        val query = """
            query Messages(${'$'}tripId: ID!, ${'$'}pagination: MessagePaginationInput) {
              messages(tripId: ${'$'}tripId, pagination: ${'$'}pagination) {
                id
                tripId
                senderId
                senderName
                senderPhotoUrl
                content
                type
                attachments {
                  id
                  name
                  url
                  mimeType
                  fileSize
                }
                replyToId
                replyToContent
                timestamp
                isMe
                deleted
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "pagination" to pagination)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("messages") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(ChatMessageDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    // 3. Send Message
    suspend fun sendMessage(tripId: String, input: SendMessageInput): ChatMessageDto? {
        val query = """
            mutation SendMessage(${'$'}tripId: ID!, ${'$'}input: SendMessageInput!) {
              sendMessage(tripId: ${'$'}tripId, input: ${'$'}input) {
                id
                tripId
                senderId
                senderName
                senderPhotoUrl
                content
                type
                attachments {
                  id
                  name
                  url
                  mimeType
                  fileSize
                }
                replyToId
                replyToContent
                timestamp
                isMe
                deleted
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("sendMessage") ?: return null
        return moshi.adapter(ChatMessageDto::class.java).fromJsonValue(data)
    }

    // 4. Delete Message
    suspend fun deleteMessage(tripId: String, messageId: String): Boolean {
        val query = """
            mutation DeleteMessage(${'$'}tripId: ID!, ${'$'}messageId: ID!) {
              deleteMessage(tripId: ${'$'}tripId, messageId: ${'$'}messageId)
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "messageId" to messageId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteMessage") as? Boolean ?: false
    }
}

data class ActivityLogDto(
    val id: String,
    val tripId: String,
    val userId: String,
    val userName: String,
    val userPhotoUrl: String?,
    val action: String,
    val targetType: String,
    val targetName: String,
    val timestamp: String
)

data class ChatMessageDto(
    val id: String,
    val tripId: String,
    val senderId: String,
    val senderName: String,
    val senderPhotoUrl: String?,
    val content: String,
    val type: String,
    val attachments: List<ChatAttachmentDto>?,
    val replyToId: String?,
    val replyToContent: String?,
    val timestamp: String,
    val isMe: Boolean,
    val deleted: Boolean
)

data class ChatAttachmentDto(
    val id: String,
    val name: String,
    val url: String,
    val mimeType: String,
    val fileSize: String?
)

data class MessagePaginationInput(
    val limit: Int? = 50,
    val beforeCursor: String? = null
)

data class SendMessageInput(
    val content: String,
    val type: String = "TEXT",
    val replyToId: String? = null
)
