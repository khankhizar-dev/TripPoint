package com.android.trippoint.booking.list

import com.android.trippoint.booking.domain.model.Booking
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class BookingListContract {
    sealed class Intent : UiIntent {
        data class LoadBookings(val tripId: String) : Intent()
        data class BookingClicked(val tripId: String, val bookingId: String) : Intent()
        object AddBookingClicked : Intent()
        object BackClicked : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class TabSelected(val index: Int) : Intent()
    }

    data class State(
        val tripId: String = "",
        val bookings: List<Booking> = emptyList(),
        val filteredBookings: List<Booking> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val searchQuery: String = "",
        val selectedTab: Int = 0
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToBookingDetails(val tripId: String, val bookingId: String) : Effect()
        data class NavigateToCreateBooking(val tripId: String) : Effect()
    }
}
