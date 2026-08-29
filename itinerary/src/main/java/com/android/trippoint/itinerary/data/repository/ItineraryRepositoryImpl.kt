package com.android.trippoint.itinerary.data.repository

import com.android.trippoint.core.network.CreateItineraryActivityInput as NetCreateActivityInput
import com.android.trippoint.core.network.CreateItineraryDayInput
import com.android.trippoint.core.network.ItineraryActivityDto
import com.android.trippoint.core.network.ItineraryDayDto
import com.android.trippoint.core.network.ItineraryRemoteDataSource
import com.android.trippoint.core.network.UpdateItineraryActivityInput as NetUpdateActivityInput
import com.android.trippoint.core.network.UpdateItineraryDayInput
import com.android.trippoint.itinerary.domain.model.CreateActivityInput
import com.android.trippoint.itinerary.domain.model.EventType
import com.android.trippoint.itinerary.domain.model.TimelineEvent
import com.android.trippoint.itinerary.domain.model.TripDay
import com.android.trippoint.itinerary.domain.model.UpdateActivityInput
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository

class ItineraryRepositoryImpl(
    private val remoteDataSource: ItineraryRemoteDataSource
) : ItineraryRepository {

    override suspend fun getItineraryDays(tripId: String): Result<List<TripDay>> {
        return try {
            val dtos = remoteDataSource.getItineraryDays(tripId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getItineraryDay(tripId: String, dayNumber: Int): Result<TripDay> {
        return try {
            val dto = remoteDataSource.getItineraryDay(tripId, dayNumber)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Day not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createItineraryDay(
        tripId: String,
        dayNumber: Int,
        date: String,
        title: String?,
        notes: String?
    ): Result<TripDay> {
        return try {
            val input = CreateItineraryDayInput(dayNumber, date, title, notes)
            val dto = remoteDataSource.createItineraryDay(tripId, input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to create itinerary day"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateItineraryDay(
        tripId: String,
        dayNumber: Int,
        title: String?,
        notes: String?
    ): Result<TripDay> {
        return try {
            val input = UpdateItineraryDayInput(title, notes)
            val dto = remoteDataSource.updateItineraryDay(tripId, dayNumber, input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to update itinerary day"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteItineraryDay(tripId: String, dayNumber: Int): Result<Boolean> {
        return try {
            val success = remoteDataSource.deleteItineraryDay(tripId, dayNumber)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getItineraryActivities(tripId: String, itineraryDayId: String): Result<List<TimelineEvent>> {
        return try {
            val dtos = remoteDataSource.getItineraryActivities(tripId, itineraryDayId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getItineraryActivity(
        tripId: String,
        itineraryDayId: String,
        activityId: String
    ): Result<TimelineEvent> {
        return try {
            val dto = remoteDataSource.getItineraryActivity(tripId, itineraryDayId, activityId)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Activity not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createItineraryActivity(
        tripId: String,
        itineraryDayId: String,
        input: CreateActivityInput
    ): Result<TimelineEvent> {
        return try {
            val netInput = NetCreateActivityInput(
                input.title, input.description, input.type, input.startTime,
                input.endTime, input.location, input.latitude, input.longitude, input.sortOrder
            )
            val dto = remoteDataSource.createItineraryActivity(tripId, itineraryDayId, netInput)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to create activity"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateItineraryActivity(
        tripId: String,
        itineraryDayId: String,
        activityId: String,
        input: UpdateActivityInput
    ): Result<TimelineEvent> {
        return try {
            val netInput = NetUpdateActivityInput(
                input.title, input.description, input.type, input.startTime, input.endTime,
                input.location, input.latitude, input.longitude, input.sortOrder, input.completed
            )
            val dto = remoteDataSource.updateItineraryActivity(tripId, itineraryDayId, activityId, netInput)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to update activity"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markItineraryActivityCompleted(
        tripId: String,
        itineraryDayId: String,
        activityId: String,
        completed: Boolean
    ): Result<Boolean> {
        return try {
            val dto = remoteDataSource.markItineraryActivityCompleted(tripId, itineraryDayId, activityId, completed)
            Result.success(dto != null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteItineraryActivity(
        tripId: String,
        itineraryDayId: String,
        activityId: String
    ): Result<Boolean> {
        return try {
            val success = remoteDataSource.deleteItineraryActivity(tripId, itineraryDayId, activityId)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ItineraryDayDto.toDomain(): TripDay {
        return TripDay(
            id = id,
            tripId = tripId,
            dayNumber = dayNumber,
            date = date,
            title = title,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun ItineraryActivityDto.toDomain(): TimelineEvent {
        return TimelineEvent(
            id = id,
            itineraryDayId = itineraryDayId,
            title = title,
            description = description,
            type = try {
                EventType.valueOf(type)
            } catch (@Suppress("SwallowedException") e: Exception) {
                EventType.ACTIVITY
            },
            startTime = startTime,
            endTime = endTime,
            location = location,
            latitude = latitude,
            longitude = longitude,
            sortOrder = sortOrder,
            completed = completed,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
