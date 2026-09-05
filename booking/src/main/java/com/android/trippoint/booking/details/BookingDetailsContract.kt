package com.android.trippoint.booking.details

import com.android.trippoint.booking.domain.model.Booking
import com.android.trippoint.booking.domain.model.BookingTraveller
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class BookingDetailsContract {
    sealed class Intent : UiIntent {
        data class LoadBookingDetails(val tripId: String, val bookingId: String) : Intent()
        object BackClicked : Intent()
        object DeleteClicked : Intent()
        object ShareClicked : Intent()
        object EditClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val bookingId: String = "",
        val booking: Booking? = null,
        val travellers: List<BookingTraveller> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
