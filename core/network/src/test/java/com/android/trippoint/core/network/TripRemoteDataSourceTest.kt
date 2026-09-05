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

class TripRemoteDataSourceTest {

    private lateinit var api: TripPointApi
    private lateinit var dataSource: TripRemoteDataSource

    @Before
    fun setUp() {
        api = mockk()
        dataSource = TripRemoteDataSource(api)
    }

    @Test
    fun `createTrip returns new trip`() = runBlocking {
        val input = CreateTripInput(
            name = "Summer Trip", 
            destination = "Paris", 
            startDate = "2023-07-01", 
            endDate = "2023-07-10"
        )
        val mockTrip = mapOf<String, Any?>(
            "id" to "t1",
            "ownerId" to "u1",
            "name" to "Summer Trip",
            "destination" to "Paris",
            "startDate" to "2023-07-01",
            "endDate" to "2023-07-10",
            "status" to "PLANNING",
            "progress" to 0.0f,
            "travelers" to 1,
            "createdAt" to "2023-06-01T10:00:00Z",
            "updatedAt" to "2023-06-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("createTrip" to mockTrip),
            errors = null
        )
        val requestSlot = slot<GraphQlRequest>()
        coEvery { api.postGraphQl(capture(requestSlot)) } returns Response.success(mockResponse)

        val result = dataSource.createTrip(input)

        assertNotNull(result)
        assertEquals("t1", result?.id)
        assertTrue(requestSlot.captured.query.contains("mutation CreateTrip"))
    }

    @Test
    fun `getTrips returns list`() = runBlocking {
        val mockTrip = mapOf<String, Any?>(
            "id" to "t1",
            "ownerId" to "u1",
            "name" to "Summer Trip",
            "destination" to "Paris",
            "startDate" to "2023-07-01",
            "endDate" to "2023-07-10",
            "status" to "PLANNING",
            "progress" to 0.0f,
            "travelers" to 1,
            "createdAt" to "2023-06-01T10:00:00Z",
            "updatedAt" to "2023-06-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("trips" to listOf(mockTrip)),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getTrips()

        assertEquals(1, result.size)
        assertEquals("Summer Trip", result[0].name)
    }

    @Test
    fun `getTrip returns trip`() = runBlocking {
        val mockTrip = mapOf<String, Any?>(
            "id" to "t1",
            "ownerId" to "u1",
            "name" to "Summer Trip",
            "destination" to "Paris",
            "startDate" to "2023-07-01",
            "endDate" to "2023-07-10",
            "status" to "PLANNING",
            "progress" to 0.0f,
            "travelers" to 1,
            "createdAt" to "2023-06-01T10:00:00Z",
            "updatedAt" to "2023-06-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("trip" to mockTrip),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getTrip("t1")

        assertNotNull(result)
        assertEquals("Paris", result?.destination)
    }

    @Test
    fun `updateTrip returns updated trip`() = runBlocking {
        val input = UpdateTripInput(name = "Updated Trip Name")
        val mockTrip = mapOf<String, Any?>(
            "id" to "t1",
            "ownerId" to "u1",
            "name" to "Updated Trip Name",
            "destination" to "Paris",
            "startDate" to "2023-07-01",
            "endDate" to "2023-07-10",
            "status" to "PLANNING",
            "progress" to 0.0f,
            "travelers" to 1,
            "createdAt" to "2023-06-01T10:00:00Z",
            "updatedAt" to "2023-06-02T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("updateTrip" to mockTrip),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.updateTrip("t1", input)

        assertNotNull(result)
        assertEquals("Updated Trip Name", result?.name)
    }

    @Test
    fun `archiveTrip returns true`() = runBlocking {
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("archiveTrip" to true),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.archiveTrip("t1")

        assertTrue(result)
    }

    @Test
    fun `restoreTrip returns trip`() = runBlocking {
        val mockTrip = mapOf<String, Any?>(
            "id" to "t1",
            "ownerId" to "u1",
            "name" to "Summer Trip",
            "destination" to "Paris",
            "startDate" to "2023-07-01",
            "endDate" to "2023-07-10",
            "status" to "ACTIVE",
            "progress" to 0.0f,
            "travelers" to 1,
            "createdAt" to "2023-06-01T10:00:00Z",
            "updatedAt" to "2023-06-02T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("restoreTrip" to mockTrip),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.restoreTrip("t1")

        assertNotNull(result)
        assertEquals("ACTIVE", result?.status)
    }

    @Test
    fun `deleteTrip returns true`() = runBlocking {
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("deleteTrip" to true),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.deleteTrip("t1")

        assertTrue(result)
    }

    @Test
    fun `getTripMembers returns list`() = runBlocking {
        val mockMember = mapOf<String, Any?>(
            "id" to "m1",
            "tripId" to "t1",
            "userId" to "u1",
            "role" to "OWNER",
            "status" to "JOINED",
            "invitedAt" to "2023-06-01T10:00:00Z",
            "joinedAt" to "2023-06-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("tripMembers" to listOf(mockMember)),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getTripMembers("t1")

        assertEquals(1, result.size)
        assertEquals("OWNER", result[0].role)
    }

    @Test
    fun `inviteTripMember returns member`() = runBlocking {
        val input = InviteTripMemberInput(email = "friend@example.com")
        val mockMember = mapOf<String, Any?>(
            "id" to "m2",
            "tripId" to "t1",
            "userId" to "u2",
            "role" to "MEMBER",
            "status" to "INVITED",
            "invitedAt" to "2023-06-01T10:00:00Z",
            "joinedAt" to null
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("inviteTripMember" to mockMember),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.inviteTripMember("t1", input)

        assertNotNull(result)
        assertEquals("INVITED", result?.status)
    }

    @Test
    fun `acceptTripInvitation returns member`() = runBlocking {
        val mockMember = mapOf<String, Any?>(
            "id" to "m2",
            "tripId" to "t1",
            "userId" to "u2",
            "role" to "MEMBER",
            "status" to "JOINED",
            "invitedAt" to "2023-06-01T10:00:00Z",
            "joinedAt" to "2023-06-02T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("acceptTripInvitation" to mockMember),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.acceptTripInvitation("t1")

        assertNotNull(result)
        assertEquals("JOINED", result?.status)
    }

    @Test
    fun `declineTripInvitation returns member`() = runBlocking {
        val mockMember = mapOf<String, Any?>(
            "id" to "m2",
            "tripId" to "t1",
            "userId" to "u2",
            "role" to "MEMBER",
            "status" to "DECLINED",
            "invitedAt" to "2023-06-01T10:00:00Z",
            "joinedAt" to null
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("declineTripInvitation" to mockMember),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.declineTripInvitation("t1")

        assertNotNull(result)
        assertEquals("DECLINED", result?.status)
    }

    @Test
    fun `getMyTripInvitations returns list`() = runBlocking {
        val mockInvitation = mapOf<String, Any?>(
            "id" to "m3",
            "tripId" to "t2",
            "userId" to "u1",
            "role" to "MEMBER",
            "status" to "INVITED",
            "invitedAt" to "2023-06-05T10:00:00Z",
            "joinedAt" to null
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("myTripInvitations" to listOf(mockInvitation)),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getMyTripInvitations()

        assertEquals(1, result.size)
        assertEquals("INVITED", result[0].status)
    }
}
