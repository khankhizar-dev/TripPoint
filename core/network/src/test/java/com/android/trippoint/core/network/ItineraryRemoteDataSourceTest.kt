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

class ItineraryRemoteDataSourceTest {

    private lateinit var api: TripPointApi
    private lateinit var dataSource: ItineraryRemoteDataSource

    @Before
    fun setUp() {
        api = mockk()
        dataSource = ItineraryRemoteDataSource(api)
    }

    @Test
    fun `getItineraryDays returns list`() = runBlocking {
        val tripId = "t1"
        val mockDay = mapOf<String, Any?>(
            "id" to "d1",
            "tripId" to tripId,
            "dayNumber" to 1,
            "date" to "2023-10-01",
            "title" to "Day 1",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("itineraryDays" to listOf(mockDay)),
            errors = null
        )
        val requestSlot = slot<GraphQlRequest>()
        coEvery { api.postGraphQl(capture(requestSlot)) } returns Response.success(mockResponse)

        val result = dataSource.getItineraryDays(tripId)

        assertEquals(1, result.size)
        assertEquals("d1", result[0].id)
        assertTrue(requestSlot.captured.query.contains("query GetItineraryDays"))
        assertEquals(tripId, requestSlot.captured.variables["tripId"])
    }

    @Test
    fun `getItineraryDay returns day`() = runBlocking {
        val mockDay = mapOf<String, Any?>(
            "id" to "d1",
            "tripId" to "t1",
            "dayNumber" to 1,
            "date" to "2023-10-01",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("itineraryDay" to mockDay),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getItineraryDay("t1", 1)

        assertNotNull(result)
        assertEquals(1, result?.dayNumber)
    }

    @Test
    fun `createItineraryDay returns new day`() = runBlocking {
        val input = CreateItineraryDayInput(dayNumber = 2, date = "2023-10-02", title = "Day 2", notes = null)
        val mockDay = mapOf<String, Any?>(
            "id" to "d2",
            "tripId" to "t1",
            "dayNumber" to 2,
            "date" to "2023-10-02",
            "title" to "Day 2",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("createItineraryDay" to mockDay),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.createItineraryDay("t1", input)

        assertNotNull(result)
        assertEquals(2, result?.dayNumber)
    }

    @Test
    fun `updateItineraryDay returns updated day`() = runBlocking {
        val input = UpdateItineraryDayInput(title = "Updated Title", notes = "Updated Notes")
        val mockDay = mapOf<String, Any?>(
            "id" to "d1",
            "tripId" to "t1",
            "dayNumber" to 1,
            "date" to "2023-10-01",
            "title" to "Updated Title",
            "notes" to "Updated Notes",
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-02T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("updateItineraryDay" to mockDay),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.updateItineraryDay("t1", 1, input)

        assertNotNull(result)
        assertEquals("Updated Title", result?.title)
    }

    @Test
    fun `deleteItineraryDay returns true`() = runBlocking {
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("deleteItineraryDay" to true),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.deleteItineraryDay("t1", 1)

        assertTrue(result)
    }

    @Test
    fun `getItineraryActivities returns list`() = runBlocking {
        val mockActivity = mapOf<String, Any?>(
            "id" to "a1",
            "itineraryDayId" to "d1",
            "title" to "Breakfast",
            "type" to "MEAL",
            "startTime" to "2023-10-01T08:00:00Z",
            "sortOrder" to 1,
            "completed" to false,
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("itineraryActivities" to listOf(mockActivity)),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getItineraryActivities("t1", "d1")

        assertEquals(1, result.size)
        assertEquals("a1", result[0].id)
    }

    @Test
    fun `getItineraryActivity returns activity`() = runBlocking {
        val mockActivity = mapOf<String, Any?>(
            "id" to "a1",
            "itineraryDayId" to "d1",
            "title" to "Breakfast",
            "type" to "MEAL",
            "startTime" to "2023-10-01T08:00:00Z",
            "sortOrder" to 1,
            "completed" to false,
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("itineraryActivity" to mockActivity),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.getItineraryActivity("t1", "d1", "a1")

        assertNotNull(result)
        assertEquals("Breakfast", result?.title)
    }

    @Test
    fun `createItineraryActivity returns activity`() = runBlocking {
        val input = CreateItineraryActivityInput(
            title = "Lunch", type = "MEAL", startTime = "2023-10-01T12:00:00Z",
            description = null, endTime = null, location = null, latitude = null, longitude = null, sortOrder = 2
        )
        val mockActivity = mapOf<String, Any?>(
            "id" to "a2",
            "itineraryDayId" to "d1",
            "title" to "Lunch",
            "type" to "MEAL",
            "startTime" to "2023-10-01T12:00:00Z",
            "sortOrder" to 2,
            "completed" to false,
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-01T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("createItineraryActivity" to mockActivity),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.createItineraryActivity("t1", "d1", input)

        assertNotNull(result)
        assertEquals("Lunch", result?.title)
    }

    @Test
    fun `updateItineraryActivity returns updated activity`() = runBlocking {
        val input = UpdateItineraryActivityInput(title = "Dinner")
        val mockActivity = mapOf<String, Any?>(
            "id" to "a1",
            "itineraryDayId" to "d1",
            "title" to "Dinner",
            "type" to "MEAL",
            "startTime" to "2023-10-01T20:00:00Z",
            "sortOrder" to 3,
            "completed" to false,
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-02T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("updateItineraryActivity" to mockActivity),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.updateItineraryActivity("t1", "d1", "a1", input)

        assertNotNull(result)
        assertEquals("Dinner", result?.title)
    }

    @Test
    fun `markItineraryActivityCompleted returns updated status`() = runBlocking {
        val mockActivity = mapOf<String, Any?>(
            "id" to "a1",
            "itineraryDayId" to "d1",
            "title" to "Breakfast",
            "type" to "MEAL",
            "startTime" to "2023-10-01T08:00:00Z",
            "sortOrder" to 1,
            "completed" to true,
            "createdAt" to "2023-09-01T10:00:00Z",
            "updatedAt" to "2023-09-02T10:00:00Z"
        )
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("markItineraryActivityCompleted" to mockActivity),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.markItineraryActivityCompleted("t1", "d1", "a1", true)

        assertNotNull(result)
        assertTrue(result?.completed == true)
    }

    @Test
    fun `deleteItineraryActivity returns true`() = runBlocking {
        val mockResponse: GraphQlResponse<Map<String, Any?>> = GraphQlResponse(
            data = mapOf("deleteItineraryActivity" to true),
            errors = null
        )
        coEvery { api.postGraphQl(any()) } returns Response.success(mockResponse)

        val result = dataSource.deleteItineraryActivity("t1", "d1", "a1")

        assertTrue(result)
    }
}
