package com.android.trippoint.booking.data.repository

import com.android.trippoint.booking.domain.model.BookingType
import com.android.trippoint.core.network.BookingDto
import com.android.trippoint.core.network.BookingEventDto
import com.android.trippoint.core.network.BookingRemoteDataSource
import com.android.trippoint.core.network.BookingTravellerDto
import com.android.trippoint.core.network.CreateBookingInput
import com.android.trippoint.core.network.CreateBookingTravellerInput
import com.android.trippoint.core.network.TripDto
import com.android.trippoint.core.network.TripRemoteDataSource
import com.android.trippoint.core.network.UpdateBookingInput
import com.android.trippoint.core.network.UpdateBookingTravellerInput
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BookingRepositoryImplTest {

    private val remoteDataSource: BookingRemoteDataSource = mockk()
    private val tripRemoteDataSource: TripRemoteDataSource = mockk()
    private lateinit var repository: BookingRepositoryImpl

    private val dummyBookingDto = BookingDto(
        id = "b1",
        tripId = "t1",
        itineraryDayId = null,
        createdBy = "u1",
        type = "FLIGHT",
        status = "CONFIRMED",
        title = "Test Flight",
        provider = "Airline",
        bookingReference = "REF123",
        startAt = "2025-01-01T10:00:00",
        endAt = "2025-01-01T12:00:00",
        location = "Airport",
        amount = 100.0,
        currency = "USD",
        source = "Email",
        notes = "No notes",
        details = null,
        createdAt = "2024-08-28T12:00:00",
        updatedAt = "2024-08-28T12:00:00"
    )

    private val dummyTravellerDto = BookingTravellerDto(
        id = "tr1",
        bookingId = "b1",
        firstName = "John",
        lastName = "Doe",
        email = "john@example.com",
        phoneNumber = "123456789",
        dateOfBirth = "1990-01-01",
        ticketNumber = "TKT123",
        seatNumber = "12A",
        createdAt = "2024-08-28T12:00:00",
        updatedAt = "2024-08-28T12:00:00"
    )

    private val dummyEventDto = BookingEventDto(
        id = "e1",
        bookingId = "b1",
        eventType = "UPDATE",
        description = "Booking updated",
        metadata = null,
        createdBy = "u1",
        createdAt = "2024-08-28T12:00:00"
    )

    private val dummyTripDto = TripDto(
        id = "t1",
        ownerId = "u1",
        name = "Trip 1",
        destination = "Dest 1",
        startDate = "2025-01-01",
        endDate = "2025-01-10",
        status = "UPCOMING",
        progress = 0f,
        travelers = 1,
        createdAt = "",
        updatedAt = ""
    )

    @Before
    fun setUp() {
        repository = BookingRepositoryImpl(remoteDataSource, tripRemoteDataSource)
    }

    @Test
    fun `getBookings with tripId returns success domain model list`() = runTest {
        coEvery { remoteDataSource.getBookings("t1", any()) } returns listOf(dummyBookingDto)

        val result = repository.getBookings("t1", null)

        assertTrue(result.isSuccess)
        val bookings = result.getOrNull()
        assertEquals(1, bookings?.size)
        assertEquals("Test Flight", bookings?.first()?.title)
        assertEquals(BookingType.FLIGHT, bookings?.first()?.type)
    }

    @Test
    fun `getBookings without tripId fetches all trips and bookings`() = runTest {
        coEvery { tripRemoteDataSource.getTrips() } returns listOf(dummyTripDto)
        coEvery { remoteDataSource.getBookings("t1", any()) } returns listOf(dummyBookingDto)

        val result = repository.getBookings(null, null)

        assertTrue(result.isSuccess)
        val bookings = result.getOrNull()
        assertEquals(1, bookings?.size)
    }

    @Test
    fun `getBooking returns success domain model`() = runTest {
        coEvery { remoteDataSource.getBooking("t1", "b1") } returns dummyBookingDto

        val result = repository.getBooking("t1", "b1")

        assertTrue(result.isSuccess)
        assertEquals("b1", result.getOrNull()?.id)
    }

    @Test
    fun `getBooking returns failure when not found`() = runTest {
        coEvery { remoteDataSource.getBooking("t1", "b2") } returns null

        val result = repository.getBooking("t1", "b2")

        assertTrue(result.isFailure)
    }

    @Test
    fun `createBooking returns success domain model`() = runTest {
        val input = CreateBookingInput(type = "FLIGHT", title = "Flight", startAt = "now")
        coEvery { remoteDataSource.createBooking("t1", input) } returns dummyBookingDto

        val result = repository.createBooking("t1", input)

        assertTrue(result.isSuccess)
        assertEquals("b1", result.getOrNull()?.id)
    }

    @Test
    fun `updateBooking returns success domain model`() = runTest {
        val input = UpdateBookingInput(title = "Updated Flight")
        coEvery { remoteDataSource.updateBooking("t1", "b1", input) } returns dummyBookingDto

        val result = repository.updateBooking("t1", "b1", input)

        assertTrue(result.isSuccess)
        assertEquals("b1", result.getOrNull()?.id)
    }

    @Test
    fun `deleteBooking returns success`() = runTest {
        coEvery { remoteDataSource.deleteBooking("t1", "b1") } returns true

        val result = repository.deleteBooking("t1", "b1")

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `getBookingTravellers returns success domain model list`() = runTest {
        coEvery { remoteDataSource.getBookingTravellers("t1", "b1") } returns listOf(dummyTravellerDto)

        val result = repository.getBookingTravellers("t1", "b1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("John", result.getOrNull()?.first()?.firstName)
    }

    @Test
    fun `addBookingTraveller returns success domain model`() = runTest {
        val input = CreateBookingTravellerInput(firstName = "John", lastName = "Doe")
        coEvery { remoteDataSource.addBookingTraveller("t1", "b1", input) } returns dummyTravellerDto

        val result = repository.addBookingTraveller("t1", "b1", input)

        assertTrue(result.isSuccess)
        assertEquals("tr1", result.getOrNull()?.id)
    }

    @Test
    fun `updateBookingTraveller returns success domain model`() = runTest {
        val input = UpdateBookingTravellerInput(firstName = "Jane")
        coEvery { remoteDataSource.updateBookingTraveller("t1", "b1", "tr1", input) } returns dummyTravellerDto

        val result = repository.updateBookingTraveller("t1", "b1", "tr1", input)

        assertTrue(result.isSuccess)
        assertEquals("tr1", result.getOrNull()?.id)
    }

    @Test
    fun `deleteBookingTraveller returns success`() = runTest {
        coEvery { remoteDataSource.deleteBookingTraveller("t1", "b1", "tr1") } returns true

        val result = repository.deleteBookingTraveller("t1", "b1", "tr1")

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `getBookingEvents returns success domain model list`() = runTest {
        coEvery { remoteDataSource.getBookingEvents("t1", "b1") } returns listOf(dummyEventDto)

        val result = repository.getBookingEvents("t1", "b1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("UPDATE", result.getOrNull()?.first()?.eventType)
    }

    @Test
    fun `importBooking returns success domain model`() = runTest {
        coEvery { remoteDataSource.importBooking("t1", "REF123") } returns dummyBookingDto

        val result = repository.importBooking("t1", "REF123")

        assertTrue(result.isSuccess)
        assertEquals("b1", result.getOrNull()?.id)
    }

    @Test
    fun `repository handles network exceptions`() = runTest {
        coEvery { remoteDataSource.getBooking("t1", "b1") } throws Exception("Network error")

        val result = repository.getBooking("t1", "b1")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
