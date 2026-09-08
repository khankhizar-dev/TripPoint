package com.android.trippoint.core.common.model

data class Trip(
    val id: String,
    val ownerId: String,
    val title: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val status: TripStatus,
    val imageUrl: String,
    val travelersCount: Int = 0,
    val travelers: List<Traveler> = emptyList(),
    val progress: Float = 0f,
    val budget: String = "",
    val tasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    val calculatedProgress: Float
        get() = if (tasksCount > 0) completedTasksCount.toFloat() / tasksCount else progress
}

data class Traveler(
    val id: String,
    val name: String,
    val photoUrl: String,
    val role: TravelerRole = TravelerRole.MEMBER,
    val status: InvitationStatus = InvitationStatus.ACCEPTED
)

enum class TripStatus {
    DRAFT,
    UPCOMING,
    IN_PROGRESS,
    COMPLETED,
    ARCHIVED
}

enum class TravelerRole {
    OWNER,
    MEMBER
}

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}

data class TripMember(
    val id: String,
    val tripId: String,
    val userId: String,
    val userName: String? = null,
    val role: TravelerRole,
    val status: InvitationStatus,
    val invitedAt: String,
    val joinedAt: String?
)
