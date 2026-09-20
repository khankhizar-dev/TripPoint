package com.android.trippoint.notification.domain.repository

import com.android.trippoint.notification.domain.model.HistoryLog
import com.android.trippoint.notification.domain.model.Notification
import com.android.trippoint.notification.domain.model.NotificationCategory
import com.android.trippoint.notification.domain.model.NotificationChannel
import com.android.trippoint.notification.domain.model.NotificationDetail
import com.android.trippoint.notification.domain.model.NotificationPreferences
import com.android.trippoint.notification.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotifications(
        limit: Int = 20, 
        category: NotificationCategory? = null, 
        beforeCursor: String? = null
    ): Result<List<Notification>>
    
    suspend fun getNotificationDetail(notificationId: String): Result<NotificationDetail>
    suspend fun markAsRead(notificationId: String): Result<Notification>
    suspend fun markAllAsRead(): Result<Int>
    suspend fun archiveNotification(notificationId: String): Result<Notification>
    suspend fun snoozeNotification(notificationId: String, until: String): Result<Notification>
    suspend fun deleteNotification(notificationId: String): Result<Notification>
    suspend fun clearAll(): Result<Int>
    suspend fun getUnreadCount(): Result<Int>
    
    fun observeIncomingNotifications(): Flow<Notification>
    
    // Preferences
    suspend fun getPreferences(): Result<NotificationPreferences>
    suspend fun updatePreferences(preferences: NotificationPreferences): Result<NotificationPreferences>
    
    // Reminders (Can be derived from notifications or a separate endpoint)
    suspend fun getReminders(): Result<List<Reminder>>
    suspend fun markReminderAsDone(reminderId: String): Result<Boolean>

    // Channels
    suspend fun getChannels(): Result<List<NotificationChannel>>
    suspend fun updateChannelStatus(channelId: String, isEnabled: Boolean): Result<Boolean>

    // History
    suspend fun getHistoryLogs(query: String? = null): Result<List<HistoryLog>>
    suspend fun clearHistory(): Result<Boolean>
}
