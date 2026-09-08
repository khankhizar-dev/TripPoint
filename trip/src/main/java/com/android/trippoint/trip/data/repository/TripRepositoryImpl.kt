package com.android.trippoint.trip.data.repository

import com.android.trippoint.core.common.model.InvitationStatus
import com.android.trippoint.core.common.model.TravelerRole
import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripMember
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.core.network.CreateTripInput
import com.android.trippoint.core.network.InviteTripMemberInput
import com.android.trippoint.core.network.TripDto
import com.android.trippoint.core.network.TripFilterInput
import com.android.trippoint.core.network.TripMemberDto
import com.android.trippoint.core.network.TripRemoteDataSource
import com.android.trippoint.core.network.UpdateTripInput
import com.android.trippoint.trip.domain.repository.TripRepository

class TripRepositoryImpl(
    private val remoteDataSource: TripRemoteDataSource
) : TripRepository {

    override suspend fun createTrip(
        name: String,
        destination: String,
        startDate: String,
        endDate: String
    ): Result<Trip> {
        return try {
            val input = CreateTripInput(name, destination, startDate, endDate)
            val dto = remoteDataSource.createTrip(input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to create trip"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrips(status: TripStatus?, search: String?): Result<List<Trip>> {
        return try {
            val filter = if (status != null || search != null) {
                TripFilterInput(status = status?.name, search = search)
            } else null
            val dtos = remoteDataSource.getTrips(filter)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrip(id: String): Result<Trip> {
        return try {
            val dto = remoteDataSource.getTrip(id)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Trip not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTrip(
        id: String,
        name: String?,
        destination: String?,
        startDate: String?,
        endDate: String?,
        status: TripStatus?
    ): Result<Trip> {
        return try {
            val input = UpdateTripInput(name, destination, startDate, endDate, status?.name)
            val dto = remoteDataSource.updateTrip(id, input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to update trip"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun archiveTrip(id: String): Result<Boolean> {
        return try {
            val success = remoteDataSource.archiveTrip(id)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreTrip(id: String): Result<Trip> {
        return try {
            val dto = remoteDataSource.restoreTrip(id)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to restore trip"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTrip(id: String): Result<Boolean> {
        return try {
            val success = remoteDataSource.deleteTrip(id)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTripMembers(tripId: String): Result<List<TripMember>> {
        return try {
            val dtos = remoteDataSource.getTripMembers(tripId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun inviteTripMember(tripId: String, email: String): Result<TripMember> {
        return try {
            val input = InviteTripMemberInput(email)
            val dto = remoteDataSource.inviteTripMember(tripId, input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to invite member"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptTripInvitation(tripId: String): Result<TripMember> {
        return try {
            val dto = remoteDataSource.acceptTripInvitation(tripId)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to accept invitation"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun declineTripInvitation(tripId: String): Result<TripMember> {
        return try {
            val dto = remoteDataSource.declineTripInvitation(tripId)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to decline invitation"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyTripInvitations(): Result<List<TripMember>> {
        return try {
            val dtos = remoteDataSource.getMyTripInvitations()
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun TripDto.toDomain(): Trip {
        return Trip(
            id = id,
            ownerId = ownerId,
            title = name,
            location = destination,
            startDate = startDate,
            endDate = endDate,
            status = try { TripStatus.valueOf(status) } catch (_: Exception) { TripStatus.DRAFT },
            imageUrl = "", // Removed from API for now
            progress = progress,
            travelersCount = travelers,
            tasksCount = 0, // Fallback since API field is undefined
            completedTasksCount = 0, // Fallback since API field is undefined
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun TripMemberDto.toDomain(): TripMember {
        val name = user?.fullName ?: "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim()
        return TripMember(
            id = id,
            tripId = tripId,
            userId = userId,
            userName = name.ifBlank { null },
            role = try { TravelerRole.valueOf(role) } catch (_: Exception) { TravelerRole.MEMBER },
            status = try { InvitationStatus.valueOf(status) } catch (_: Exception) { InvitationStatus.PENDING },
            invitedAt = invitedAt,
            joinedAt = joinedAt
        )
    }
}
