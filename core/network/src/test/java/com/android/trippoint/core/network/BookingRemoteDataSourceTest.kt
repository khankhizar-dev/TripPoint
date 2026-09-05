package com.android.trippoint.core.network

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BookingRemoteDataSourceTest {

    private lateinit var api: TripPointApi
    private lateinit var dataSource: BookingRemoteDataSource

    @Before
    fun setUp() {
        api = mockk()
        dataSource = BookingRemoteDataSource(api)
    }

    @Test
    fun `getBookings sends correct query and returns list`() = runBlocking {
        val tripId = "trip123"
        val filter = BookingFilterInput(type = "FLIGHT")
        val mockBooking = mapOf<String, Any?>(
            "id" to "b1",
            "tripId" to tripId,
            "createdBy" to "u1",
            "type" to "FLIGHT",
            "status" to "CONFIRMED",
            "title" to "Flight to NYC",
            "startAt" to "2023-10-01T10:00:00Z",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("bookings" to listOf(mockBooking)),
            errors = null
        )
        val requestSlot = slot<GraphQlRequest>()
        coEvery { api.postGraphQl(capture(requestSlot)) } returns Response.success(mockResponse)

        val result = dataSource.getBookings(tripId, filter)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("b1", result[0].id)
        assertEquals("FLIGHT", result[0].type)

        assertTrue(requestSlot.captured.query.contains("query GetBookings"))
        assertEquals(tripId, requestSlot.captured.variables["tripId"])
        assertEquals(filter, requestSlot.captured.variables["filter"])
    }

    @Test
    fun `getBooking returns booking for valid ids`() = runBlocking {
        val tripId = "trip123"
        val bookingId = "b1"
        val mockBooking = mapOf<String, Any?>(
            "id" to bookingId,
            "tripId" to tripId,
            "createdBy" to "u1",
            "type" to "HOTEL",
            "status" to "CONFIRMED",
            "title" to "Grand Hotel",
            "startAt" to "2023-10-01T10:00:00Z",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("booking" to mockBooking),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getBooking(tripId, bookingId)

        assertNotNull(result)
        assertEquals(bookingId, result?.id)
        assertEquals("HOTEL", result?.type)
    }

    @Test
    fun `getBooking returns null for blank ids`() = runBlocking {
        val result = dataSource.getBooking("", "b1")
        assertEquals(null, result)
        
        val result2 = dataSource.getBooking("t1", "")
        assertEquals(null, result2)
    }

    @Test
    fun `createBooking sends mutation and returns booking`() = runBlocking {
        val tripId = "trip123"
        val input = CreateBookingInput(type = "CAR_RENTAL", title = "Hertz Rental", startAt = "2023-10-01T10:00:00Z")
        val mockBooking = mapOf<String, Any?>(
            "id" to "b2",
            "tripId" to tripId,
            "createdBy" to "u1",
            "type" to "CAR_RENTAL",
            "status" to "PENDING",
            "title" to "Hertz Rental",
            "startAt" to "2023-10-01T10:00:00Z",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("createBooking" to mockBooking),
            errors = null
        )
        val requestSlot = slot<GraphQlRequest>()
        coEvery { api.postGraphQl(capture(requestSlot)) } returns Response.success(mockResponse)

        val result = dataSource.createBooking(tripId, input)

        assertNotNull(result)
        assertEquals("b2", result?.id)
        assertTrue(requestSlot.captured.query.contains("mutation CreateBooking"))
        assertEquals(input, requestSlot.captured.variables["input"])
    }

    @Test
    fun `updateBooking sends mutation and returns updated booking`() = runBlocking {
        val tripId = "trip123"
        val bookingId = "b1"
        val input = UpdateBookingInput(title = "Updated Hotel Name")
        val mockBooking = mapOf<String, Any?>(
            "id" to bookingId,
            "tripId" to tripId,
            "createdBy" to "u1",
            "type" to "HOTEL",
            "status" to "CONFIRMED",
            "title" to "Updated Hotel Name",
            "startAt" to "2023-10-01T10:00:00Z",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-02T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("updateBooking" to mockBooking),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.updateBooking(tripId, bookingId, input)

        assertNotNull(result)
        assertEquals("Updated Hotel Name", result?.title)
    }

    @Test
    fun `deleteBooking returns true on success`() = runBlocking {
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("deleteBooking" to true),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.deleteBooking("t1", "b1")

        assertTrue(result)
    }

    @Test
    fun `getBookingTravellers returns list`() = runBlocking {
        val tripId = "trip123"
        val bookingId = "b1"
        val mockTraveller = mapOf<String, Any?>(
            "id" to "tr1",
            "bookingId" to bookingId,
            "firstName" to "John",
            "lastName" to "Doe",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("bookingTravellers" to listOf(mockTraveller)),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getBookingTravellers(tripId, bookingId)

        assertEquals(1, result.size)
        assertEquals("John", result[0].firstName)
    }

    @Test
    fun `getBookingTravellers returns empty for blank ids`() = runBlocking {
        val result = dataSource.getBookingTravellers("", "b1")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `addBookingTraveller returns traveller`() = runBlocking {
        val input = CreateBookingTravellerInput(firstName = "Jane", lastName = "Doe")
        val mockTraveller = mapOf<String, Any?>(
            "id" to "tr2",
            "bookingId" to "b1",
            "firstName" to "Jane",
            "lastName" to "Doe",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("addBookingTraveller" to mockTraveller),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.addBookingTraveller("t1", "b1", input)

        assertNotNull(result)
        assertEquals("Jane", result?.firstName)
    }

    @Test
    fun `updateBookingTraveller returns updated traveller`() = runBlocking {
        val input = UpdateBookingTravellerInput(firstName = "Janet")
        val mockTraveller = mapOf<String, Any?>(
            "id" to "tr2",
            "bookingId" to "b1",
            "firstName" to "Janet",
            "lastName" to "Doe",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-02T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("updateBookingTraveller" to mockTraveller),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.updateBookingTraveller("t1", "b1", "tr2", input)

        assertNotNull(result)
        assertEquals("Janet", result?.firstName)
    }

    @Test
    fun `deleteBookingTraveller returns true on success`() = runBlocking {
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("deleteBookingTraveller" to true),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.deleteBookingTraveller("t1", "b1", "tr2")

        assertTrue(result)
    }

    @Test
    fun `getBookingEvents returns list`() = runBlocking {
        val mockEvent = mapOf<String, Any?>(
            "id" to "e1",
            "bookingId" to "b1",
            "eventType" to "STATUS_CHANGE",
            "description" to "Confirmed",
            "createdBy" to "u1",
            "createdAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("bookingEvents" to listOf(mockEvent)),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getBookingEvents("t1", "b1")

        assertEquals(1, result.size)
        assertEquals("STATUS_CHANGE", result[0].eventType)
    }

    @Test
    fun `importBooking returns booking`() = runBlocking {
        val mockBooking = mapOf<String, Any?>(
            "id" to "b3",
            "tripId" to "t1",
            "createdBy" to "u1",
            "type" to "FLIGHT",
            "status" to "CONFIRMED",
            "title" to "Imported Flight",
            "startAt" to "2023-10-01T10:00:00Z",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("importBooking" to mockBooking),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.importBooking("t1", "REF123")

        assertNotNull(result)
        assertEquals("b3", result?.id)
    }
}
