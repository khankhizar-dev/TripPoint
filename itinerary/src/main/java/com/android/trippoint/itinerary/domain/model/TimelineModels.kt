package com.android.trippoint.itinerary.domain.model

data class TimelineEvent(
    val id: String,
    val itineraryDayId: String,
    val title: String,
    val description: String?,
    val type: EventType,
    val startTime: String,
    val endTime: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val sortOrder: Int,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String
)

enum class EventStatus {
    ON_TIME,
    CONFIRMED,
    DELAYED,
    CANCELLED
}

enum class EventType {
    FLIGHT,
    CHECK_IN,
    LUNCH,
    TOUR,
    DINNER,
    TRANSPORT,
    ACTIVITY,
    SIGHTSEEING,
    HOTEL,
    DINING
}

data class TripDay(
    val id: String,
    val tripId: String,
    val dayNumber: Int,
    val date: String,
    val title: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)

data class TripTask(
    val id: String,
    val name: String,
    val date: String,
    val time: String,
    val priority: Priority,
    val hasReminder: Boolean = false,
    val isCompleted: Boolean = false
)

data class TripNote(
    val id: String,
    val title: String,
    val content: String,
    val date: String,
    val priority: Priority = Priority.MEDIUM
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

data class CreateActivityInput(
    val title: String,
    val description: String?,
    val type: String,
    val startTime: String,
    val endTime: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val sortOrder: Int
)

data class UpdateActivityInput(
    val title: String? = null,
    val description: String? = null,
    val type: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val sortOrder: Int? = null,
    val completed: Boolean? = null
)
