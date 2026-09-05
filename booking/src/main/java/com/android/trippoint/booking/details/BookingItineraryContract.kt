package com.android.trippoint.booking.details

import com.android.trippoint.booking.domain.model.Booking
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class BookingItineraryContract {
    sealed class Intent : UiIntent {
        data class LoadItinerary(val tripId: String, val bookingId: String) : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val bookingId: String = "",
        val booking: Booking? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
    }
}
