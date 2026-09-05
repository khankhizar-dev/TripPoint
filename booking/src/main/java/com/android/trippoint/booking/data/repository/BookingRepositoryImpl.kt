package com.android.trippoint.booking.data.repository

import com.android.trippoint.booking.domain.model.Booking
import com.android.trippoint.booking.domain.model.BookingEvent
import com.android.trippoint.booking.domain.model.BookingStatus
import com.android.trippoint.booking.domain.model.BookingTraveller
import com.android.trippoint.booking.domain.model.BookingType
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.core.network.BookingDto
import com.android.trippoint.core.network.BookingEventDto
import com.android.trippoint.core.network.BookingFilterInput
import com.android.trippoint.core.network.BookingRemoteDataSource
import com.android.trippoint.core.network.TripRemoteDataSource
import com.android.trippoint.core.network.BookingTravellerDto
import com.android.trippoint.core.network.CreateBookingInput
import com.android.trippoint.core.network.CreateBookingTravellerInput
import com.android.trippoint.core.network.UpdateBookingInput
import com.android.trippoint.core.network.UpdateBookingTravellerInput
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class BookingRepositoryImpl(
    private val remoteDataSource: BookingRemoteDataSource,
    private val tripRemoteDataSource: TripRemoteDataSource
) : BookingRepository {

    override suspend fun getBookings(tripId: String?, filter: BookingFilterInput?): Result<List<Booking>> {
        return try {
            if (!tripId.isNullOrBlank()) {
                val dtos = remoteDataSource.getBookings(tripId, filter)
                Result.success(dtos.map { it.toDomain(tripId) })
            } else {
                // Global "My Bookings" case
                coroutineScope {
                    val trips = tripRemoteDataSource.getTrips()
                    val deferredBookings = trips.map { trip ->
                        async {
                            try {
                                remoteDataSource.getBookings(trip.id, filter).map { it.toDomain(trip.id) }
                            } catch (e: Exception) {
                                emptyList<Booking>()
                            }
                        }
                    }
                    val allBookings = deferredBookings.awaitAll().flatten()
                    Result.success(allBookings)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBooking(tripId: String, bookingId: String): Result<Booking> {
        return try {
            val dto = remoteDataSource.getBooking(tripId, bookingId)
            if (dto != null) {
                Result.success(dto.toDomain(tripId))
            } else {
                Result.failure(Exception("Booking not found in trip: $tripId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBooking(tripId: String, input: CreateBookingInput): Result<Booking> {
        return try {
            val dto = remoteDataSource.createBooking(tripId, input)
            if (dto != null) Result.success(dto.toDomain(tripId))
            else Result.failure(Exception("Failed to create booking"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBooking(tripId: String, bookingId: String, input: UpdateBookingInput): Result<Booking> {
        return try {
            val dto = remoteDataSource.updateBooking(tripId, bookingId, input)
            if (dto != null) Result.success(dto.toDomain(tripId))
            else Result.failure(Exception("Failed to update booking"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBooking(tripId: String, bookingId: String): Result<Boolean> {
        return try {
            val success = remoteDataSource.deleteBooking(tripId, bookingId)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookingTravellers(tripId: String, bookingId: String): Result<List<BookingTraveller>> {
        return try {
            val dtos = remoteDataSource.getBookingTravellers(tripId, bookingId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addBookingTraveller(
        tripId: String, 
        bookingId: String, 
        input: CreateBookingTravellerInput
    ): Result<BookingTraveller> {
        return try {
            val dto = remoteDataSource.addBookingTraveller(tripId, bookingId, input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to add traveller"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBookingTraveller(
        tripId: String, 
        bookingId: String, 
        travellerId: String, 
        input: UpdateBookingTravellerInput
    ): Result<BookingTraveller> {
        return try {
            val dto = remoteDataSource.updateBookingTraveller(tripId, bookingId, travellerId, input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to update traveller"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBookingTraveller(
        tripId: String, 
        bookingId: String, 
        travellerId: String
    ): Result<Boolean> {
        return try {
            val success = remoteDataSource.deleteBookingTraveller(tripId, bookingId, travellerId)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookingEvents(tripId: String, bookingId: String): Result<List<BookingEvent>> {
        return try {
            val dtos = remoteDataSource.getBookingEvents(tripId, bookingId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importBooking(tripId: String, reference: String): Result<Booking> {
        return try {
            val dto = remoteDataSource.importBooking(tripId, reference)
            if (dto != null) Result.success(dto.toDomain(tripId))
            else Result.failure(Exception("Failed to import booking"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun BookingDto.toDomain(knownTripId: String? = null): Booking {
        val finalTripId = when {
            !tripId.isNullOrBlank() && tripId != "null" -> tripId
            !knownTripId.isNullOrBlank() -> knownTripId
            else -> ""
        }
        return Booking(
            id = id,
            tripId = finalTripId,
            itineraryDayId = itineraryDayId,
            createdBy = createdBy,
            type = try { 
                if (type == "TRANSPORTATION" || type == "TRANSPORT") BookingType.TRANSPORTATION 
                else BookingType.valueOf(type) 
            } catch (e: Exception) { 
                BookingType.OTHER 
            },
            status = try { BookingStatus.valueOf(status) } catch (e: Exception) { BookingStatus.DRAFT },
            title = title,
            provider = provider,
            bookingReference = bookingReference,
            startAt = startAt,
            endAt = endAt,
            location = location,
            amount = amount,
            currency = currency,
            source = source,
            notes = notes,
            details = details,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun BookingTravellerDto.toDomain(): BookingTraveller = BookingTraveller(
        id = id,
        bookingId = bookingId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        dateOfBirth = dateOfBirth,
        ticketNumber = ticketNumber,
        seatNumber = seatNumber,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun BookingEventDto.toDomain(): BookingEvent = BookingEvent(
        id = id,
        bookingId = bookingId,
        eventType = eventType,
        description = description,
        metadata = metadata,
        createdBy = createdBy,
        createdAt = createdAt
    )
}
