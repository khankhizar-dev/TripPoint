package com.android.trippoint.itinerary.data.provider

import com.android.trippoint.core.common.domain.provider.ItineraryStatsProvider
import com.android.trippoint.core.common.domain.provider.TripTaskStats
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository

class ItineraryStatsProviderImpl(
    private val repository: ItineraryRepository
) : ItineraryStatsProvider {
    override suspend fun getTripTaskStats(tripId: String): Result<TripTaskStats> {
        return try {
            val daysResult = repository.getItineraryDays(tripId)
            if (daysResult.isFailure) return Result.failure(daysResult.exceptionOrNull()!!)
            
            val days = daysResult.getOrThrow()
            var total = 0
            var completed = 0
            
            days.forEach { day ->
                val activitiesResult = repository.getItineraryActivities(tripId, day.id)
                activitiesResult.getOrNull()?.let { activities ->
                    total += activities.size
                    completed += activities.count { it.completed }
                }
            }
            
            Result.success(TripTaskStats(total, completed))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
