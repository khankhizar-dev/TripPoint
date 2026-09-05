package com.android.trippoint.trip.invite

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.trip.domain.repository.TripRepository
import kotlinx.coroutines.launch

class InvitePeopleViewModel(
    private val repository: TripRepository
) : BaseViewModel<
    InvitePeopleContract.State,
    InvitePeopleContract.Intent,
    InvitePeopleContract.Effect
>(
    InvitePeopleContract.State()
) {

    override fun onIntent(intent: InvitePeopleContract.Intent) {
        when (intent) {
            is InvitePeopleContract.Intent.LoadTrip -> setState { copy(tripId = intent.tripId) }
            is InvitePeopleContract.Intent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
            }
            is InvitePeopleContract.Intent.ManualInputChanged -> {
                setState { copy(manualInput = intent.input) }
            }
            is InvitePeopleContract.Intent.InviteClicked -> inviteUser(intent.userId)
            InvitePeopleContract.Intent.ManualInviteClicked -> inviteManual()
            InvitePeopleContract.Intent.InviteViaLink -> { /* Handle */ }
            InvitePeopleContract.Intent.BackClicked -> sendEffect(InvitePeopleContract.Effect.NavigateBack)
            InvitePeopleContract.Intent.NextClicked -> {
                sendEffect(InvitePeopleContract.Effect.NavigateToSummary(uiState.value.tripId))
            }
        }
    }

    private fun inviteManual() {
        val input = uiState.value.manualInput
        if (input.isBlank()) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // The API takes an email, we'll try to use the input as an email.
            val result = repository.inviteTripMember(uiState.value.tripId, input)
            setState {
                copy(isLoading = false, manualInput = if (result.isSuccess) "" else manualInput)
            }
            
            if (result.isFailure) {
                val error = result.exceptionOrNull()?.message ?: "Failed to invite"
                sendEffect(InvitePeopleContract.Effect.ShowError(error))
            }
        }
    }

    private fun inviteUser(userId: String) {
        viewModelScope.launch {
            // The provided API is mutation InviteTripMember($tripId: ID!, $input: InviteTripMemberInput!)
            // which takes an email. For now, we'll mock the email or use userId as email if possible.
            val result = repository.inviteTripMember(uiState.value.tripId, "user_$userId@example.com")
            
            if (result.isSuccess) {
                setState {
                    copy(
                        people = people.map {
                            if (it.id == userId) it.copy(isInvited = true) else it
                        }
                    )
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Failed to invite"
                sendEffect(InvitePeopleContract.Effect.ShowError(error))
            }
        }
    }
}
