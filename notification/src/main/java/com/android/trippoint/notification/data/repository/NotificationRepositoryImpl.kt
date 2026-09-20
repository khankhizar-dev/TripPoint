package com.android.trippoint.notification.data.repository

import com.android.trippoint.core.network.NotificationDto
import com.android.trippoint.core.network.NotificationPreferenceDto
import com.android.trippoint.core.network.NotificationRemoteDataSource
import com.android.trippoint.core.network.UpdateNotificationPreferenceInput
import com.android.trippoint.notification.domain.model.HistoryLog
import com.android.trippoint.notification.domain.model.Notification
import com.android.trippoint.notification.domain.model.NotificationCategory
import com.android.trippoint.notification.domain.model.NotificationChannel
import com.android.trippoint.notification.domain.model.NotificationDetail
import com.android.trippoint.notification.domain.model.NotificationKeyDetail
import com.android.trippoint.notification.domain.model.NotificationPreferences
import com.android.trippoint.notification.domain.model.NotificationType
import com.android.trippoint.notification.domain.model.Reminder
import com.android.trippoint.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationRepositoryImpl(
    private val remoteDataSource: NotificationRemoteDataSource
) : NotificationRepository {

    private val _incomingNotifications = MutableSharedFlow<Notification>()

    override suspend fun getNotifications(
        limit: Int,
        category: NotificationCategory?,
        beforeCursor: String?
    ): Result<List<Notification>> = runCatching {
        remoteDataSource.getNotifications(limit, category?.name, beforeCursor).map { it.toDomain() }
    }

    override suspend fun getNotificationDetail(notificationId: String): Result<NotificationDetail> = runCatching {
        val dto = remoteDataSource.getNotification(notificationId) ?: throw Exception("Not found")
        val notification = dto.toDomain()
        NotificationDetail(
            notification = notification,
            detailedMessage = notification.message,
            keyDetails = when(notification.type) {
                NotificationType.BOOKING_UPDATED -> listOf(
                    NotificationKeyDetail("New Time", "12 May 2025, 10:30 AM"),
                    NotificationKeyDetail("Location", "Dubai (DXB)")
                )
                else -> emptyList()
            },
            actionText = "View Trip"
        )
    }

    override suspend fun markAsRead(notificationId: String): Result<Notification> = runCatching {
        remoteDataSource.markAsRead(notificationId)?.toDomain() ?: throw Exception("Update failed")
    }

    override suspend fun markAllAsRead(): Result<Int> = runCatching {
        remoteDataSource.markAllAsRead()?.count ?: 0
    }

    override suspend fun archiveNotification(notificationId: String): Result<Notification> = runCatching {
        remoteDataSource.archiveNotification(notificationId)?.toDomain() ?: throw Exception("Archive failed")
    }

    override suspend fun snoozeNotification(notificationId: String, until: String): Result<Notification> = runCatching {
        remoteDataSource.snoozeNotification(notificationId, until)?.toDomain() ?: throw Exception("Snooze failed")
    }

    override suspend fun deleteNotification(notificationId: String): Result<Notification> = runCatching {
        remoteDataSource.deleteNotification(notificationId)?.toDomain() ?: throw Exception("Delete failed")
    }

    override suspend fun clearAll(): Result<Int> = runCatching {
        remoteDataSource.clearAllNotifications()?.count ?: 0
    }

    override suspend fun getUnreadCount(): Result<Int> = runCatching {
        remoteDataSource.getUnreadCount()
    }

    override fun observeIncomingNotifications(): Flow<Notification> = _incomingNotifications.asSharedFlow()

    override suspend fun getPreferences(): Result<NotificationPreferences> = runCatching {
        remoteDataSource.getPreferences()?.toDomain() ?: NotificationPreferences()
    }

    override suspend fun updatePreferences(
        preferences: NotificationPreferences
    ): Result<NotificationPreferences> = runCatching {
        val input = UpdateNotificationPreferenceInput(
            pushEnabled = preferences.pushEnabled,
            emailEnabled = preferences.emailEnabled,
            inAppEnabled = preferences.inAppEnabled,
            smsEnabled = preferences.smsEnabled,
            digestEnabled = preferences.digestEnabled,
            quietHoursEnabled = preferences.quietHoursEnabled,
            quietHoursStart = preferences.quietHoursStart,
            quietHoursEnd = preferences.quietHoursEnd,
            timezone = preferences.timezone
        )
        remoteDataSource.updatePreferences(input)?.toDomain() ?: throw Exception("Update failed")
    }

    override suspend fun getReminders(): Result<List<Reminder>> = runCatching {
        emptyList()
    }

    override suspend fun markReminderAsDone(reminderId: String): Result<Boolean> = runCatching {
        true
    }

    override suspend fun getChannels(): Result<List<NotificationChannel>> = runCatching {
        val prefs = remoteDataSource.getPreferences() ?: throw Exception("Failed to load")
        listOf(
            NotificationChannel(
                "1", "Push Notifications", "Receive notifications on this device", 
                prefs.pushEnabled, "Enabled"
            ),
            NotificationChannel("2", "Email", " khizar.khan@email.com", prefs.emailEnabled, "Enabled"),
            NotificationChannel("3", "In-app Messages", "Receive in-app messages", prefs.inAppEnabled, "Enabled"),
            NotificationChannel("4", "SMS (Optional)", "+91 98765 43210", prefs.smsEnabled, "Disabled")
        )
    }

    override suspend fun updateChannelStatus(channelId: String, isEnabled: Boolean): Result<Boolean> = runCatching {
        val input = when(channelId) {
            "1" -> UpdateNotificationPreferenceInput(pushEnabled = isEnabled)
            "2" -> UpdateNotificationPreferenceInput(emailEnabled = isEnabled)
            "3" -> UpdateNotificationPreferenceInput(inAppEnabled = isEnabled)
            "4" -> UpdateNotificationPreferenceInput(smsEnabled = isEnabled)
            else -> UpdateNotificationPreferenceInput()
        }
        remoteDataSource.updatePreferences(input)
        true
    }

    override suspend fun getHistoryLogs(query: String?): Result<List<HistoryLog>> = runCatching {
        val notifications = remoteDataSource.getNotifications(limit = 100).map { it.toDomain() }
        listOf(HistoryLog("Recent", notifications))
    }

    override suspend fun clearHistory(): Result<Boolean> = runCatching {
        remoteDataSource.clearAllNotifications()
        true
    }

    private fun NotificationDto.toDomain() = Notification(
        id = id,
        recipientUserId = recipientUserId,
        actorUserId = actorUserId,
        tripId = tripId,
        category = try { 
            NotificationCategory.valueOf(category) 
        } catch(_: Exception) { 
            NotificationCategory.SYSTEM 
        },
        type = try { 
            NotificationType.valueOf(type) 
        } catch(_: Exception) { 
            NotificationType.SYSTEM_ALERT 
        },
        title = title,
        message = message,
        targetType = targetType,
        targetId = targetId,
        targetName = targetName,
        isRead = isRead,
        isArchived = isArchived,
        isDeleted = isDeleted,
        snoozedUntil = snoozedUntil,
        createdAt = createdAt,
        readAt = readAt,
        archivedAt = archivedAt,
        deletedAt = deletedAt
    )

    private fun NotificationPreferenceDto.toDomain() = NotificationPreferences(
        id = id,
        userId = userId,
        pushEnabled = pushEnabled,
        emailEnabled = emailEnabled,
        inAppEnabled = inAppEnabled,
        smsEnabled = smsEnabled,
        digestEnabled = digestEnabled,
        quietHoursEnabled = quietHoursEnabled,
        quietHoursStart = quietHoursStart,
        quietHoursEnd = quietHoursEnd,
        timezone = timezone,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
