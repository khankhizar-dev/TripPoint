package com.android.trippoint.itinerary.domain.repository

import com.android.trippoint.itinerary.domain.model.CreateActivityInput
import com.android.trippoint.itinerary.domain.model.TimelineEvent
import com.android.trippoint.itinerary.domain.model.TripDay
import com.android.trippoint.itinerary.domain.model.UpdateActivityInput

interface ItineraryRepository {
    suspend fun getItineraryDays(tripId: String): Result<List<TripDay>>
    suspend fun getItineraryDay(tripId: String, dayNumber: Int): Result<TripDay>
    suspend fun createItineraryDay(
        tripId: String, 
        dayNumber: Int, 
        date: String, 
        title: String?, 
        notes: String?
    ): Result<TripDay>
    suspend fun updateItineraryDay(
        tripId: String, 
        dayNumber: Int, 
        title: String?, 
        notes: String?
    ): Result<TripDay>
    suspend fun deleteItineraryDay(tripId: String, dayNumber: Int): Result<Boolean>

    suspend fun getItineraryActivities(tripId: String, itineraryDayId: String): Result<List<TimelineEvent>>
    suspend fun getItineraryActivity(tripId: String, itineraryDayId: String, activityId: String): Result<TimelineEvent>
    suspend fun createItineraryActivity(
        tripId: String, 
        itineraryDayId: String, 
        input: CreateActivityInput
    ): Result<TimelineEvent>
    suspend fun updateItineraryActivity(
        tripId: String, 
        itineraryDayId: String, 
        activityId: String, 
        input: UpdateActivityInput
    ): Result<TimelineEvent>
    suspend fun markItineraryActivityCompleted(
        tripId: String, 
        itineraryDayId: String, 
        activityId: String, 
        completed: Boolean
    ): Result<Boolean>
    suspend fun deleteItineraryActivity(tripId: String, itineraryDayId: String, activityId: String): Result<Boolean>
}
