package com.android.trippoint.booking.create

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class CreateBookingContract {
    sealed class Intent : UiIntent {
        data class LoadTripId(val tripId: String) : Intent()
        data class TitleChanged(val title: String) : Intent()
        data class TypeChanged(val type: String) : Intent()
        data class DateChanged(val date: String) : Intent()
        data class TimeChanged(val time: String) : Intent()
        data class EndDateChanged(val date: String) : Intent()
        data class EndTimeChanged(val time: String) : Intent()
        data class ProviderChanged(val provider: String) : Intent()
        data class ReferenceChanged(val reference: String) : Intent()
        data class LocationChanged(val location: String) : Intent()
        data class AmountChanged(val amount: String) : Intent()
        data class CurrencyChanged(val currency: String) : Intent()
        data class NotesChanged(val notes: String) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val availableTrips: List<com.android.trippoint.core.common.model.Trip> = emptyList(),
        val title: String = "",
        val type: String = "FLIGHT",
        val date: String = "",
        val time: String = "",
        val endDate: String = "",
        val endTime: String = "",
        val provider: String = "",
        val reference: String = "",
        val location: String = "",
        val amount: String = "",
        val currency: String = "USD",
        val notes: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object BookingCreated : Effect()
    }
}
