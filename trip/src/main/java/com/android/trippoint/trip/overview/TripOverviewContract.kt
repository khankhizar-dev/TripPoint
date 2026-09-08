package com.android.trippoint.trip.overview

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripStatus

class TripOverviewContract {
    sealed class Intent : UiIntent {
        data class LoadTripDetails(val tripId: String) : Intent()
        data class TabSelected(val tabIndex: Int) : Intent()
        object BackClicked : Intent()
        object AddBookingClicked : Intent()
        object AddTaskClicked : Intent()
        object AddExpenseClicked : Intent()
        object AddNoteClicked : Intent()
        data class UpdateStatus(val status: TripStatus) : Intent()
        object ArchiveTrip : Intent()
        object DeleteTrip : Intent()
    }

    data class State(
        val trip: Trip? = null,
        val selectedTab: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null,
        val errorResId: Int? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToTimeline(val tripId: String) : Effect()
        data class NavigateToBookings(val tripId: String) : Effect()
        data class NavigateToAddTask(val tripId: String) : Effect()
        data class NavigateToAddNote(val tripId: String) : Effect()
        data class NavigateToAddBooking(val tripId: String) : Effect()
        data class NavigateToBudgets(val tripId: String) : Effect()
        data class NavigateToAddExpense(val tripId: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
