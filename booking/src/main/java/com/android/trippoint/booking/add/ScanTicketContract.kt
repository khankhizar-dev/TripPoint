package com.android.trippoint.booking.add

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ScanTicketContract {
    sealed class Intent : UiIntent {
        data class LoadTripId(val tripId: String) : Intent()
        object BackClicked : Intent()
        object ScanClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object BookingAdded : Effect()
    }
}
