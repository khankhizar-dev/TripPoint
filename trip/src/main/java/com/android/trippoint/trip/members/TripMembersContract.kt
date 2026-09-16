package com.android.trippoint.trip.members

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.core.common.model.TripMember

class TripMembersContract {
    sealed class Intent : UiIntent {
        data class LoadMembers(val tripId: String) : Intent()
        data class SearchMembers(val query: String) : Intent()
        data class RemoveMember(val userId: String) : Intent()
        data class ChangeRole(val userId: String, val role: String) : Intent()
        object InviteClicked : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val members: List<TripMember> = emptyList(),
        val filteredMembers: List<TripMember> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToInvite(val tripId: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
