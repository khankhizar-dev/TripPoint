package com.android.trippoint.booking.details

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ManageBookingContract {
    sealed class Intent : UiIntent {
        data class LoadBooking(val tripId: String, val bookingId: String) : Intent()
        object BackClicked : Intent()
        object ChangeFlightClicked : Intent()
        object CancelBookingClicked : Intent()
        object UpgradeClicked : Intent()
        object SelectSeatsClicked : Intent()
        object AddBaggageClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val bookingId: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
    }
}
