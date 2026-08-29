package com.android.trippoint.itinerary.add

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class AddEventContract {
    sealed class Intent : UiIntent {
        data class LoadTripInfo(val tripId: String, val date: String) : Intent()
        data class NameChanged(val name: String) : Intent()
        data class DateChanged(val date: String) : Intent()
        data class TimeChanged(val time: String) : Intent()
        data class LocationChanged(val location: String) : Intent()
        data class CategoryChanged(val category: String) : Intent()
        object SaveClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val name: String = "",
        val date: String = "",
        val time: String = "",
        val location: String = "",
        val category: String = "Flight",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        object EventAdded : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
