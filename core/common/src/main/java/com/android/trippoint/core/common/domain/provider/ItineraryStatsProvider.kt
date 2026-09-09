package com.android.trippoint.core.common.domain.provider

data class TripTaskStats(
    val totalTasks: Int,
    val completedTasks: Int
)

interface ItineraryStatsProvider {
    suspend fun getTripTaskStats(tripId: String): Result<TripTaskStats>
}
