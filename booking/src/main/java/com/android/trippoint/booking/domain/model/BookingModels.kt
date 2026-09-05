package com.android.trippoint.booking.domain.model

/**
 * High-fidelity Booking Model (Aligned with Section 05 & API).
 */
data class Booking(
    val id: String,
    val tripId: String,
    val itineraryDayId: String?,
    val createdBy: String,
    val type: BookingType,
    val status: BookingStatus,
    val title: String,
    val provider: String?,
    val providerLogoUrl: String? = null,
    val bookingReference: String?,
    val startAt: String,
    val endAt: String?,
    val startLocation: String? = null,
    val startLocationCode: String? = null,
    val endLocation: String? = null,
    val endLocationCode: String? = null,
    val location: String?,
    val amount: Double?,
    val currency: String?,
    val source: String?,
    val notes: String?,
    val duration: String? = null,
    val isNonStop: Boolean = true,
    val baggageAllowance: String? = null,
    val cabinBaggage: String? = null,
    val aircraft: String? = null,
    val details: Map<String, Any>?,
    val createdAt: String,
    val updatedAt: String
)

data class BookingTraveller(
    val id: String,
    val bookingId: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phoneNumber: String?,
    val dateOfBirth: String?,
    val ticketNumber: String?,
    val seatNumber: String?,
    val createdAt: String,
    val updatedAt: String
)

data class BookingEvent(
    val id: String,
    val bookingId: String,
    val eventType: String,
    val description: String,
    val metadata: Map<String, Any>?,
    val createdBy: String,
    val createdAt: String
)

enum class BookingType {
    FLIGHT,
    HOTEL,
    TRANSPORTATION,
    ACTIVITY,
    OTHER,
    CAR,
    TRAIN,
    BUS
}

enum class BookingStatus {
    CONFIRMED,
    PENDING,
    CANCELLED,
    REFUNDED,
    DRAFT,
    SCHEDULED
}
