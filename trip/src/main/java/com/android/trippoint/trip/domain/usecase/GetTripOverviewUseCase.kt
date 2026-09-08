package com.android.trippoint.trip.domain.usecase

import com.android.trippoint.core.common.domain.provider.BudgetStatsProvider
import com.android.trippoint.core.common.domain.provider.ItineraryStatsProvider
import com.android.trippoint.core.common.model.Traveler
import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.trip.domain.repository.TripRepository

data class TripOverviewData(
    val trip: Trip,
    val budgetSummary: String?,
    val totalTasks: Int,
    val completedTasks: Int
)

class GetTripOverviewUseCase(
    private val tripRepository: TripRepository,
    private val budgetStatsProvider: BudgetStatsProvider,
    private val itineraryStatsProvider: ItineraryStatsProvider
) {
    suspend operator fun invoke(tripId: String): Result<TripOverviewData> {
        val tripResult = tripRepository.getTrip(tripId)
        val membersResult = tripRepository.getTripMembers(tripId)
        
        if (tripResult.isFailure) return Result.failure(tripResult.exceptionOrNull()!!)
        
        val trip = tripResult.getOrThrow()
        val members = membersResult.getOrDefault(emptyList())
        
        val budgetSummary = budgetStatsProvider.getTripBudgetSummary(tripId).getOrNull()
        val taskStats = itineraryStatsProvider.getTripTaskStats(tripId).getOrDefault(
            com.android.trippoint.core.common.domain.provider.TripTaskStats(0, 0)
        )
        
        val travelers = members.map { member ->
            Traveler(
                id = member.userId,
                name = member.userName ?: "",
                photoUrl = "",
                role = member.role,
                status = member.status
            )
        }
        
        return Result.success(
            TripOverviewData(
                trip = trip.copy(travelers = travelers),
                budgetSummary = budgetSummary,
                totalTasks = taskStats.totalTasks,
                completedTasks = taskStats.completedTasks
            )
        )
    }
}
