package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class NotificationRemoteDataSource(
    private val api: TripPointApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // 1. Get Notifications
    suspend fun getNotifications(
        limit: Int? = 20, 
        category: String? = null, 
        beforeCursor: String? = null
    ): List<NotificationDto> {
        val query = """
            query GetNotifications(${'$'}limit: Int, ${'$'}category: NotificationCategory, ${'$'}beforeCursor: ID) {
              notifications(limit: ${'$'}limit, category: ${'$'}category, beforeCursor: ${'$'}beforeCursor) {
                id recipientUserId actorUserId tripId category type title message
                targetType targetId targetName isRead isArchived isDeleted
                snoozedUntil createdAt readAt archivedAt deletedAt
              }
            }
        """.trimIndent()
        val variables = mutableMapOf<String, Any?>(
            "limit" to limit,
            "category" to category,
            "beforeCursor" to beforeCursor
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("notifications") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(NotificationDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    // 2. Get Single Notification
    suspend fun getNotification(id: String): NotificationDto? {
        val query = """
            query GetNotification(${'$'}id: ID!) {
              notification(id: ${'$'}id) {
                id recipientUserId actorUserId tripId category type title message
                targetType targetId targetName isRead isArchived isDeleted
                snoozedUntil createdAt readAt archivedAt deletedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("id" to id)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("notification") ?: return null
        return moshi.adapter(NotificationDto::class.java).fromJsonValue(data)
    }

    // 3. Get Unread Count
    suspend fun getUnreadCount(): Int {
        val query = "query { unreadNotificationCount }"
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        return (response.body()?.data?.get("unreadNotificationCount") as? Double)?.toInt() ?: 0
    }

    // 4. Mutations
    suspend fun markAsRead(id: String): NotificationDto? {
        val query = """
            mutation MarkAsRead(${'$'}id: ID!) {
              markNotificationRead(id: ${'$'}id) {
                id isRead readAt
              }
            }
        """.trimIndent()
        return performMutation("markNotificationRead", mapOf("id" to id))
    }

    suspend fun markAllAsRead(): NotificationActionResultDto? {
        val query = "mutation { markAllNotificationsRead { success count } }"
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("markAllNotificationsRead") ?: return null
        return moshi.adapter(NotificationActionResultDto::class.java).fromJsonValue(data)
    }

    suspend fun archiveNotification(id: String): NotificationDto? {
        val query = """
            mutation Archive(${'$'}id: ID!) {
              archiveNotification(id: ${'$'}id) {
                id isArchived archivedAt
              }
            }
        """.trimIndent()
        return performMutation("archiveNotification", mapOf("id" to id))
    }

    suspend fun snoozeNotification(id: String, until: String): NotificationDto? {
        val query = """
            mutation Snooze(${'$'}id: ID!, ${'$'}until: String!) {
              snoozeNotification(id: ${'$'}id, snoozedUntil: ${'$'}until) {
                id snoozedUntil
              }
            }
        """.trimIndent()
        return performMutation("snoozeNotification", mapOf("id" to id, "until" to until))
    }

    suspend fun deleteNotification(id: String): NotificationDto? {
        val query = """
            mutation Delete(${'$'}id: ID!) {
              deleteNotification(id: ${'$'}id) {
                id isDeleted deletedAt
              }
            }
        """.trimIndent()
        return performMutation("deleteNotification", mapOf("id" to id))
    }

    suspend fun clearAllNotifications(): NotificationActionResultDto? {
        val query = "mutation { clearAllNotifications { success count } }"
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("clearAllNotifications") ?: return null
        return moshi.adapter(NotificationActionResultDto::class.java).fromJsonValue(data)
    }

    // 5. Preferences
    suspend fun getPreferences(): NotificationPreferenceDto? {
        val query = """
            query {
              notificationPreferences {
                id userId pushEnabled emailEnabled inAppEnabled smsEnabled
                digestEnabled quietHoursEnabled quietHoursStart quietHoursEnd
                timezone createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("notificationPreferences") ?: return null
        return moshi.adapter(NotificationPreferenceDto::class.java).fromJsonValue(data)
    }

    suspend fun updatePreferences(input: UpdateNotificationPreferenceInput): NotificationPreferenceDto? {
        val query = """
            mutation UpdatePrefs(${'$'}input: UpdateNotificationPreferenceInput!) {
              updateNotificationPreferences(input: ${'$'}input) {
                id pushEnabled emailEnabled inAppEnabled smsEnabled
                digestEnabled quietHoursEnabled quietHoursStart quietHoursEnd
                timezone
              }
            }
        """.trimIndent()
        val variables = mapOf("input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateNotificationPreferences") ?: return null
        return moshi.adapter(NotificationPreferenceDto::class.java).fromJsonValue(data)
    }

    private suspend fun performMutation(operation: String, variables: Map<String, Any?>): NotificationDto? {
        val query = when(operation) {
            "markNotificationRead" -> "mutation(${'$'}id: ID!) { $operation(id: ${'$'}id) { id isRead readAt } }"
            "archiveNotification" -> "mutation(${'$'}id: ID!) { $operation(id: ${'$'}id) { id isArchived archivedAt } }"
            "deleteNotification" -> "mutation(${'$'}id: ID!) { $operation(id: ${'$'}id) { id isDeleted deletedAt } }"
            else -> ""
        }
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get(operation) ?: return null
        return moshi.adapter(NotificationDto::class.java).fromJsonValue(data)
    }
}

data class NotificationDto(
    val id: String,
    val recipientUserId: String,
    val actorUserId: String?,
    val tripId: String?,
    val category: String,
    val type: String,
    val title: String,
    val message: String,
    val targetType: String?,
    val targetId: String?,
    val targetName: String?,
    val isRead: Boolean,
    val isArchived: Boolean,
    val isDeleted: Boolean,
    val snoozedUntil: String?,
    val createdAt: String,
    val readAt: String?,
    val archivedAt: String?,
    val deletedAt: String?
)

data class NotificationActionResultDto(
    val success: Boolean,
    val count: Int
)

data class NotificationPreferenceDto(
    val id: String,
    val userId: String,
    val pushEnabled: Boolean,
    val emailEnabled: Boolean,
    val inAppEnabled: Boolean,
    val smsEnabled: Boolean,
    val digestEnabled: Boolean,
    val quietHoursEnabled: Boolean,
    val quietHoursStart: String?,
    val quietHoursEnd: String?,
    val timezone: String,
    val createdAt: String,
    val updatedAt: String
)

data class UpdateNotificationPreferenceInput(
    val pushEnabled: Boolean? = null,
    val emailEnabled: Boolean? = null,
    val inAppEnabled: Boolean? = null,
    val smsEnabled: Boolean? = null,
    val digestEnabled: Boolean? = null,
    val quietHoursEnabled: Boolean? = null,
    val quietHoursStart: String? = null,
    val quietHoursEnd: String? = null,
    val timezone: String? = null
)
