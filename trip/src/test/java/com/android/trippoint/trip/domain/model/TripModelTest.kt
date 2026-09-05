package com.android.trippoint.trip.domain.model

import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class TripModelTest {

    @Test
    fun `calculatedProgress returns correct percentage`() {
        val trip = Trip(
            id = "1",
            ownerId = "owner1",
            title = "Test",
            location = "Test",
            startDate = "",
            endDate = "",
            status = TripStatus.UPCOMING,
            imageUrl = "",
            tasksCount = 10,
            completedTasksCount = 6
        )
        
        assertEquals(0.6f, trip.calculatedProgress, 0.001f)
    }

    @Test
    fun `calculatedProgress returns base progress if tasksCount is zero`() {
        val trip = Trip(
            id = "1",
            ownerId = "owner1",
            title = "Test",
            location = "Test",
            startDate = "",
            endDate = "",
            status = TripStatus.UPCOMING,
            imageUrl = "",
            progress = 0.5f,
            tasksCount = 0,
            completedTasksCount = 0
        )
        
        assertEquals(0.5f, trip.calculatedProgress, 0.001f)
    }
}
