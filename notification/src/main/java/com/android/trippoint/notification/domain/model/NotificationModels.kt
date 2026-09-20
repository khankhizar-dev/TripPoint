package com.android.trippoint.notification.domain.model

data class Notification(
    val id: String,
    val recipientUserId: String,
    val actorUserId: String?,
    val tripId: String?,
    val category: NotificationCategory,
    val type: NotificationType,
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
    val deletedAt: String?,
    val imageUrl: String? = null // For UI display, can be derived or mapped
)

enum class NotificationCategory {
    TRIP, MEMBER, BOOKING, EXPENSE, CHECKLIST, DOCUMENT, CHAT, MENTION, REMINDER, SYSTEM
}

enum class NotificationType {
    TRIP_INVITATION, TRIP_UPDATED, INVITATION_ACCEPTED, INVITATION_DECLINED,
    MEMBER_ADDED, MEMBER_REMOVED, BOOKING_CREATED, BOOKING_CONFIRMED,
    BOOKING_UPDATED, BOOKING_CANCELLED, EXPENSE_ADDED, EXPENSE_UPDATED,
    EXPENSE_SETTLEMENT, CHECKLIST_ASSIGNED, CHECKLIST_DUE, CHECKLIST_OVERDUE,
    CHECKLIST_COMPLETED, DOCUMENT_UPLOADED, DOCUMENT_UPDATED, DOCUMENT_EXPIRING,
    CHAT_MENTION, CHAT_REPLY, REMINDER_DUE, REMINDER_OVERDUE, SYSTEM_ALERT
}

data class NotificationDetail(
    val notification: Notification,
    val detailedMessage: String,
    val keyDetails: List<NotificationKeyDetail> = emptyList(),
    val actionText: String? = null,
    val actionRoute: String? = null
)

data class NotificationKeyDetail(
    val label: String,
    val value: String
)

data class NotificationPreferences(
    val id: String = "",
    val userId: String = "",
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val inAppEnabled: Boolean = true,
    val smsEnabled: Boolean = false,
    val digestEnabled: Boolean = false,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String? = "22:00",
    val quietHoursEnd: String? = "07:00",
    val timezone: String = "UTC",
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class Reminder(
    val id: String,
    val title: String,
    val tripName: String,
    val dueDateTime: String,
    val status: ReminderStatus = ReminderStatus.UPCOMING,
    val priority: ReminderPriority = ReminderPriority.MEDIUM
)

enum class ReminderStatus {
    UPCOMING, OVERDUE, DONE
}

enum class ReminderPriority {
    LOW, MEDIUM, HIGH
}

data class NotificationChannel(
    val id: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean,
    val detail: String? = null
)

data class HistoryLog(
    val dateLabel: String,
    val notifications: List<Notification>
)
