package com.android.trippoint.trip.members

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.trip.domain.repository.TripRepository
import kotlinx.coroutines.launch

class TripMembersViewModel(
    private val repository: TripRepository
) : BaseViewModel<
    TripMembersContract.State,
    TripMembersContract.Intent,
    TripMembersContract.Effect
>(
    TripMembersContract.State()
) {

    override fun onIntent(intent: TripMembersContract.Intent) {
        when (intent) {
            is TripMembersContract.Intent.LoadMembers -> loadMembers(intent.tripId)
            is TripMembersContract.Intent.SearchMembers -> {
                setState { copy(searchQuery = intent.query) }
                filterMembers()
            }
            is TripMembersContract.Intent.RemoveMember -> removeMember(intent.userId)
            is TripMembersContract.Intent.ChangeRole -> { /* TODO: Implement Role Change API */ }
            TripMembersContract.Intent.InviteClicked -> {
                sendEffect(TripMembersContract.Effect.NavigateToInvite(uiState.value.tripId))
            }
            TripMembersContract.Intent.BackClicked -> sendEffect(TripMembersContract.Effect.NavigateBack)
        }
    }

    private fun loadMembers(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId, error = null) }
            val result = repository.getTripMembers(tripId)
            
            if (result.isSuccess) {
                val members = result.getOrDefault(emptyList())
                setState { copy(isLoading = false, members = members) }
                filterMembers()
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun removeMember(userId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.removeTripMember(uiState.value.tripId, userId)
            
            if (result.isSuccess) {
                loadMembers(uiState.value.tripId)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun filterMembers() {
        val query = uiState.value.searchQuery
        val filtered = uiState.value.members.filter {
            it.userName?.contains(query, ignoreCase = true) == true
        }
        setState { copy(filteredMembers = filtered) }
    }
}
