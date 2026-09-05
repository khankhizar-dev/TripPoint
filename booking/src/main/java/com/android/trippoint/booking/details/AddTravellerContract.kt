package com.android.trippoint.booking.details

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class AddTravellerContract {
    sealed class Intent : UiIntent {
        data class LoadIds(val tripId: String, val bookingId: String) : Intent()
        data class FirstNameChanged(val value: String) : Intent()
        data class LastNameChanged(val value: String) : Intent()
        data class EmailChanged(val value: String) : Intent()
        data class PhoneChanged(val value: String) : Intent()
        data class DobChanged(val value: String) : Intent()
        data class TicketChanged(val value: String) : Intent()
        data class SeatChanged(val value: String) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val bookingId: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val email: String = "",
        val phone: String = "",
        val dob: String = "",
        val ticket: String = "",
        val seat: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object TravellerAdded : Effect()
    }
}
