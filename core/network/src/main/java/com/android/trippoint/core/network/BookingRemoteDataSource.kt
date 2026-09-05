package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class BookingRemoteDataSource(
    private val api: TripPointApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    suspend fun getBookings(tripId: String, filter: BookingFilterInput? = null): List<BookingDto> {
        val query = """
            query GetBookings(${'$'}tripId: ID!, ${'$'}filter: BookingFilterInput) {
              bookings(tripId: ${'$'}tripId, filter: ${'$'}filter) {
                id tripId createdBy type status title provider
                bookingReference startAt endAt location amount currency source
                details createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mutableMapOf<String, Any>("tripId" to tripId)
        filter?.let { variables["filter"] = it }
        
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("bookings") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(BookingDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    suspend fun getBooking(tripId: String, bookingId: String): BookingDto? {
        if (tripId.isBlank() || bookingId.isBlank()) return null
        val query = """
            query GetBooking(${'$'}tripId: ID!, ${'$'}bookingId: ID!) {
              booking(tripId: ${'$'}tripId, id: ${'$'}bookingId) {
                id tripId createdBy type status title provider
                bookingReference startAt endAt location amount currency source
                details createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "bookingId" to bookingId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("booking") ?: return null
        return moshi.adapter(BookingDto::class.java).fromJsonValue(data)
    }

    suspend fun createBooking(tripId: String, input: CreateBookingInput): BookingDto? {
        val query = """
            mutation CreateBooking(${'$'}tripId: ID!, ${'$'}input: CreateBookingInput!) {
              createBooking(tripId: ${'$'}tripId, input: ${'$'}input) {
                id tripId itineraryDayId createdBy type status title provider
                bookingReference startAt endAt location amount currency source
                notes details createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createBooking") ?: return null
        return moshi.adapter(BookingDto::class.java).fromJsonValue(data)
    }

    suspend fun updateBooking(tripId: String, bookingId: String, input: UpdateBookingInput): BookingDto? {
        val query = """
            mutation UpdateBooking(${'$'}tripId: ID!, ${'$'}bookingId: ID!, ${'$'}input: UpdateBookingInput!) {
              updateBooking(tripId: ${'$'}tripId, bookingId: ${'$'}bookingId, input: ${'$'}input) {
                id tripId itineraryDayId type status title provider
                bookingReference startAt endAt location amount currency source
                notes details updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "bookingId" to bookingId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateBooking") ?: return null
        return moshi.adapter(BookingDto::class.java).fromJsonValue(data)
    }

    suspend fun deleteBooking(tripId: String, bookingId: String): Boolean {
        val query = """
            mutation DeleteBooking(${'$'}tripId: ID!, ${'$'}bookingId: ID!) {
              deleteBooking(tripId: ${'$'}tripId, bookingId: ${'$'}bookingId)
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "bookingId" to bookingId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteBooking") as? Boolean ?: false
    }

    suspend fun getBookingTravellers(tripId: String, bookingId: String): List<BookingTravellerDto> {
        if (tripId.isBlank() || bookingId.isBlank()) return emptyList()
        val query = """
            query GetBookingTravellers(${'$'}tripId: ID!, ${'$'}bookingId: ID!) {
              bookingTravellers(tripId: ${'$'}tripId, bookingId: ${'$'}bookingId) {
                id bookingId firstName lastName email phoneNumber dateOfBirth
                ticketNumber seatNumber createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "bookingId" to bookingId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("bookingTravellers") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(BookingTravellerDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    suspend fun addBookingTraveller(
        tripId: String, 
        bookingId: String, 
        input: CreateBookingTravellerInput
    ): BookingTravellerDto? {
        val query = """
            mutation AddBookingTraveller(${'$'}tripId: ID!, ${'$'}bookingId: ID!, ${'$'}input: CreateBookingTravellerInput!) {
              addBookingTraveller(tripId: ${'$'}tripId, bookingId: ${'$'}bookingId, input: ${'$'}input) {
                id bookingId firstName lastName email phoneNumber dateOfBirth
                ticketNumber seatNumber createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "bookingId" to bookingId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("addBookingTraveller") ?: return null
        return moshi.adapter(BookingTravellerDto::class.java).fromJsonValue(data)
    }

    suspend fun updateBookingTraveller(
        tripId: String, 
        bookingId: String, 
        travellerId: String, 
        input: UpdateBookingTravellerInput
    ): BookingTravellerDto? {
        val query = """
            mutation UpdateBookingTraveller(
                ${'$'}tripId: ID!, ${'$'}bookingId: ID!, ${'$'}travellerId: ID!, 
                ${'$'}input: UpdateBookingTravellerInput!
            ) {
              updateBookingTraveller(
                  tripId: ${'$'}tripId, bookingId: ${'$'}bookingId, 
                  travellerId: ${'$'}travellerId, input: ${'$'}input
              ) {
                id bookingId firstName lastName email phoneNumber dateOfBirth
                ticketNumber seatNumber updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf(
            "tripId" to tripId, 
            "bookingId" to bookingId, 
            "travellerId" to travellerId, 
            "input" to input
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateBookingTraveller") ?: return null
        return moshi.adapter(BookingTravellerDto::class.java).fromJsonValue(data)
    }

    suspend fun deleteBookingTraveller(tripId: String, bookingId: String, travellerId: String): Boolean {
        val query = """
            mutation DeleteBookingTraveller(${'$'}tripId: ID!, ${'$'}bookingId: ID!, ${'$'}travellerId: ID!) {
              deleteBookingTraveller(tripId: ${'$'}tripId, bookingId: ${'$'}bookingId, travellerId: ${'$'}travellerId)
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "bookingId" to bookingId, "travellerId" to travellerId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteBookingTraveller") as? Boolean ?: false
    }

    suspend fun getBookingEvents(tripId: String, bookingId: String): List<BookingEventDto> {
        val query = """
            query GetBookingEvents(${'$'}tripId: ID!, ${'$'}bookingId: ID!) {
              bookingEvents(tripId: ${'$'}tripId, bookingId: ${'$'}bookingId) {
                id bookingId eventType description metadata createdBy createdAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "bookingId" to bookingId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("bookingEvents") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(BookingEventDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    suspend fun importBooking(tripId: String, reference: String): BookingDto? {
        val query = """
            mutation ImportBooking(${'$'}tripId: ID!, ${'$'}reference: String!) {
              importBooking(tripId: ${'$'}tripId, reference: ${'$'}reference) {
                id tripId itineraryDayId createdBy type status title provider
                bookingReference startAt endAt location amount currency source
                notes details createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "reference" to reference)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("importBooking") ?: return null
        return moshi.adapter(BookingDto::class.java).fromJsonValue(data)
    }
}

data class BookingDto(
    val id: String,
    val tripId: String,
    val itineraryDayId: String?,
    val createdBy: String,
    val type: String,
    val status: String,
    val title: String,
    val provider: String?,
    val bookingReference: String?,
    val startAt: String,
    val endAt: String?,
    val location: String?,
    val amount: Double?,
    val currency: String?,
    val source: String?,
    val notes: String?,
    val details: Map<String, Any>?,
    val createdAt: String,
    val updatedAt: String
)

data class BookingTravellerDto(
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

data class BookingEventDto(
    val id: String,
    val bookingId: String,
    val eventType: String,
    val description: String,
    val metadata: Map<String, Any>?,
    val createdBy: String,
    val createdAt: String
)

data class BookingFilterInput(
    val type: String? = null,
    val status: String? = null,
    val search: String? = null
)

data class CreateBookingInput(
    val itineraryDayId: String? = null,
    val type: String,
    val title: String,
    val provider: String? = null,
    val bookingReference: String? = null,
    val startAt: String,
    val endAt: String? = null,
    val location: String? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val source: String? = null,
    val notes: String? = null,
    val details: Map<String, Any?>? = null
)

data class UpdateBookingInput(
    val itineraryDayId: String? = null,
    val type: String? = null,
    val title: String? = null,
    val provider: String? = null,
    val bookingReference: String? = null,
    val startAt: String? = null,
    val endAt: String? = null,
    val location: String? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val source: String? = null,
    val notes: String? = null,
    val details: Map<String, Any>? = null
)

data class CreateBookingTravellerInput(
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val ticketNumber: String? = null,
    val seatNumber: String? = null
)

data class UpdateBookingTravellerInput(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val ticketNumber: String? = null,
    val seatNumber: String? = null
)
