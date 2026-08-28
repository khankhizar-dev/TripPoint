package com.android.trippoint.trip.domain.repository

import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripMember
import com.android.trippoint.core.common.model.TripStatus

interface TripRepository {
    suspend fun createTrip(name: String, destination: String, startDate: String, endDate: String): Result<Trip>
    suspend fun getTrips(status: TripStatus? = null, search: String? = null): Result<List<Trip>>
    suspend fun getTrip(id: String): Result<Trip>
    suspend fun updateTrip(
        id: String,
        name: String? = null,
        destination: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        status: TripStatus? = null
    ): Result<Trip>
    suspend fun archiveTrip(id: String): Result<Boolean>
    suspend fun restoreTrip(id: String): Result<Trip>
    suspend fun deleteTrip(id: String): Result<Boolean>
    suspend fun getTripMembers(tripId: String): Result<List<TripMember>>
    suspend fun inviteTripMember(tripId: String, email: String): Result<TripMember>
    suspend fun acceptTripInvitation(tripId: String): Result<TripMember>
    suspend fun declineTripInvitation(tripId: String): Result<TripMember>
    suspend fun getMyTripInvitations(): Result<List<TripMember>>
}
