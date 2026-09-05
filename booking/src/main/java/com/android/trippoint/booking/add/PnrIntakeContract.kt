package com.android.trippoint.booking.add

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class PnrIntakeContract {
    sealed class Intent : UiIntent {
        data class LoadTripId(val tripId: String) : Intent()
        data class PnrChanged(val pnr: String) : Intent()
        object FetchClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val pnr: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object BookingAdded : Effect()
    }
}
