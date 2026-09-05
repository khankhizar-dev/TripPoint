package com.android.trippoint.itinerary.data.repository

import com.android.trippoint.core.network.ItineraryActivityDto
import com.android.trippoint.core.network.ItineraryDayDto
import com.android.trippoint.core.network.ItineraryRemoteDataSource
import com.android.trippoint.itinerary.domain.model.CreateActivityInput
import com.android.trippoint.itinerary.domain.model.EventType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ItineraryRepositoryImplTest {

    private val remoteDataSource: ItineraryRemoteDataSource = mockk()
    private lateinit var repository: ItineraryRepositoryImpl

    @Before
    fun setUp() {
        repository = ItineraryRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getItineraryDays returns success when data source returns data`() = runTest {
        val tripId = "trip1"
        val dtos = listOf(
            ItineraryDayDto("1", tripId, 1, "2026-08-29", "Day 1", null, "now", "now")
        )
        coEvery { remoteDataSource.getItineraryDays(tripId) } returns dtos

        val result = repository.getItineraryDays(tripId)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Day 1", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `getItineraryDays returns failure when data source throws`() = runTest {
        val tripId = "trip1"
        coEvery { remoteDataSource.getItineraryDays(tripId) } throws Exception("Network error")

        val result = repository.getItineraryDays(tripId)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getItineraryActivities returns success when data source returns data`() = runTest {
        val tripId = "trip1"
        val dayId = "day1"
        val dtos = listOf(
            ItineraryActivityDto(
                "a1", dayId, "Flight", null, "FLIGHT", "08:00", "10:00",
                "LHR", null, null, 1, false, "now", "now"
            )
        )
        coEvery { remoteDataSource.getItineraryActivities(tripId, dayId) } returns dtos

        val result = repository.getItineraryActivities(tripId, dayId)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals(EventType.FLIGHT, result.getOrNull()?.first()?.type)
    }

    @Test
    fun `createItineraryDay returns success`() = runTest {
        val tripId = "trip1"
        val dto = ItineraryDayDto("1", tripId, 1, "2026-08-29", "Day 1", null, "now", "now")
        coEvery { remoteDataSource.createItineraryDay(tripId, any()) } returns dto

        val result = repository.createItineraryDay(tripId, 1, "2026-08-29", "Day 1", null)

        assertTrue(result.isSuccess)
        assertEquals("Day 1", result.getOrNull()?.title)
    }

    @Test
    fun `createItineraryActivity returns success`() = runTest {
        val tripId = "trip1"
        val dayId = "day1"
        val input = CreateActivityInput(
            "Flight", null, "FLIGHT", "08:00", "10:00", "LHR", null, null, 1
        )
        val dto = ItineraryActivityDto(
            "a1", dayId, "Flight", null, "FLIGHT", "08:00", "10:00",
            "LHR", null, null, 1, false, "now", "now"
        )
        coEvery { remoteDataSource.createItineraryActivity(tripId, dayId, any()) } returns dto

        val result = repository.createItineraryActivity(tripId, dayId, input)

        assertTrue(result.isSuccess)
        assertEquals("Flight", result.getOrNull()?.title)
    }

    @Test
    fun `deleteItineraryActivity returns success`() = runTest {
        coEvery { remoteDataSource.deleteItineraryActivity(any(), any(), any()) } returns true

        val result = repository.deleteItineraryActivity("t1", "d1", "a1")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }
}
