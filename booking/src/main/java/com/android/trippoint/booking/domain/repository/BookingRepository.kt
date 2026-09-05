package com.android.trippoint.booking.domain.repository

import com.android.trippoint.booking.domain.model.Booking
import com.android.trippoint.booking.domain.model.BookingEvent
import com.android.trippoint.booking.domain.model.BookingTraveller
import com.android.trippoint.core.network.BookingFilterInput
import com.android.trippoint.core.network.CreateBookingInput
import com.android.trippoint.core.network.CreateBookingTravellerInput
import com.android.trippoint.core.network.UpdateBookingInput
import com.android.trippoint.core.network.UpdateBookingTravellerInput

interface BookingRepository {
    suspend fun getBookings(tripId: String? = null, filter: BookingFilterInput? = null): Result<List<Booking>>
    suspend fun getBooking(tripId: String, bookingId: String): Result<Booking>
    suspend fun createBooking(tripId: String, input: CreateBookingInput): Result<Booking>
    suspend fun updateBooking(tripId: String, bookingId: String, input: UpdateBookingInput): Result<Booking>
    suspend fun deleteBooking(tripId: String, bookingId: String): Result<Boolean>
    
    suspend fun getBookingTravellers(tripId: String, bookingId: String): Result<List<BookingTraveller>>
    suspend fun addBookingTraveller(
        tripId: String, 
        bookingId: String, 
        input: CreateBookingTravellerInput
    ): Result<BookingTraveller>
    suspend fun updateBookingTraveller(
        tripId: String, 
        bookingId: String, 
        travellerId: String, 
        input: UpdateBookingTravellerInput
    ): Result<BookingTraveller>
    suspend fun deleteBookingTraveller(tripId: String, bookingId: String, travellerId: String): Result<Boolean>
    
    suspend fun getBookingEvents(tripId: String, bookingId: String): Result<List<BookingEvent>>

    suspend fun importBooking(tripId: String, reference: String): Result<Booking>
}
