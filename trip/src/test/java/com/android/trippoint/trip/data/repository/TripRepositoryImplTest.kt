package com.android.trippoint.trip.data.repository

import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.core.network.TripDto
import com.android.trippoint.core.network.TripMemberDto
import com.android.trippoint.core.network.TripRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TripRepositoryImplTest {

    private val remoteDataSource: TripRemoteDataSource = mockk()
    private lateinit var repository: TripRepositoryImpl

    private val dummyTripDto = TripDto(
        id = "1",
        ownerId = "owner1",
        name = "Test Trip",
        destination = "Test Location",
        startDate = "2025-01-01",
        endDate = "2025-01-10",
        status = "UPCOMING",
        progress = 0.5f,
        travelers = 2,
        createdAt = "2024-08-28T12:00:00",
        updatedAt = "2024-08-28T12:00:00"
    )

    private val dummyMemberDto = TripMemberDto(
        id = "m1",
        tripId = "1",
        userId = "u1",
        role = "OWNER",
        status = "ACCEPTED",
        invitedAt = "2024-08-28T12:00:00",
        joinedAt = "2024-08-28T12:05:00"
    )

    @Before
    fun setUp() {
        repository = TripRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `createTrip returns success domain model`() = runTest {
        coEvery { remoteDataSource.createTrip(any()) } returns dummyTripDto

        val result = repository.createTrip("Name", "Dest", "start", "end")

        assertTrue(result.isSuccess)
        val trip = result.getOrNull()
        assertEquals("Test Trip", trip?.title)
        assertEquals(TripStatus.UPCOMING, trip?.status)
    }

    @Test
    fun `getTrips returns list of domain models`() = runTest {
        coEvery { remoteDataSource.getTrips(any()) } returns listOf(dummyTripDto)

        val result = repository.getTrips(TripStatus.UPCOMING, "search")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Test Trip", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `getTrip returns domain model`() = runTest {
        coEvery { remoteDataSource.getTrip("1") } returns dummyTripDto

        val result = repository.getTrip("1")

        assertTrue(result.isSuccess)
        assertEquals("1", result.getOrNull()?.id)
    }

    @Test
    fun `updateTrip returns updated domain model`() = runTest {
        coEvery { remoteDataSource.updateTrip("1", any()) } returns dummyTripDto

        val result = repository.updateTrip("1", name = "New Name")

        assertTrue(result.isSuccess)
        assertEquals("Test Trip", result.getOrNull()?.title)
    }

    @Test
    fun `archiveTrip returns boolean`() = runTest {
        coEvery { remoteDataSource.archiveTrip("1") } returns true

        val result = repository.archiveTrip("1")

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `restoreTrip returns restored domain model`() = runTest {
        coEvery { remoteDataSource.restoreTrip("1") } returns dummyTripDto

        val result = repository.restoreTrip("1")

        assertTrue(result.isSuccess)
        assertEquals("Test Trip", result.getOrNull()?.title)
    }

    @Test
    fun `deleteTrip returns boolean`() = runTest {
        coEvery { remoteDataSource.deleteTrip("1") } returns true

        val result = repository.deleteTrip("1")

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `getTripMembers returns domain list`() = runTest {
        coEvery { remoteDataSource.getTripMembers("1") } returns listOf(dummyMemberDto)

        val result = repository.getTripMembers("1")

        assertTrue(result.isSuccess)
        val member = result.getOrNull()?.first()
        assertEquals("OWNER", member?.role?.name)
    }

    @Test
    fun `inviteTripMember returns member domain`() = runTest {
        coEvery { remoteDataSource.inviteTripMember("1", any()) } returns dummyMemberDto

        val result = repository.inviteTripMember("1", "email@test.com")

        assertTrue(result.isSuccess)
        assertEquals("m1", result.getOrNull()?.id)
    }

    @Test
    fun `acceptTripInvitation returns member domain`() = runTest {
        coEvery { remoteDataSource.acceptTripInvitation("1") } returns dummyMemberDto

        val result = repository.acceptTripInvitation("1")

        assertTrue(result.isSuccess)
        assertEquals("ACCEPTED", result.getOrNull()?.status?.name)
    }

    @Test
    fun `declineTripInvitation returns member domain`() = runTest {
        coEvery { remoteDataSource.declineTripInvitation("1") } returns dummyMemberDto

        val result = repository.declineTripInvitation("1")

        assertTrue(result.isSuccess)
        assertEquals("ACCEPTED", result.getOrNull()?.status?.name)
    }

    @Test
    fun `getMyTripInvitations returns list`() = runTest {
        coEvery { remoteDataSource.getMyTripInvitations() } returns listOf(dummyMemberDto)

        val result = repository.getMyTripInvitations()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `repository handles exceptions`() = runTest {
        coEvery { remoteDataSource.getTrip(any()) } throws Exception("Network Error")

        val result = repository.getTrip("1")

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }
}
