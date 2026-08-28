package com.android.trippoint.trip.invite

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class InvitePeopleContract {
    sealed class Intent : UiIntent {
        data class LoadTrip(val tripId: String) : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class ManualInputChanged(val input: String) : Intent()
        data class InviteClicked(val userId: String) : Intent()
        object ManualInviteClicked : Intent()
        object InviteViaLink : Intent()
        object BackClicked : Intent()
        object NextClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val searchQuery: String = "",
        val manualInput: String = "",
        val people: List<Person> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    data class Person(
        val id: String,
        val name: String,
        val photoUrl: String,
        val isInvited: Boolean = false
    )

    sealed class Effect : UiEffect {
        data class NavigateToSummary(val tripId: String) : Effect()
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
