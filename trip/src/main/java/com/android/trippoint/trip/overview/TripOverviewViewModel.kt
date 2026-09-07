package com.android.trippoint.trip.overview

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.common.model.Traveler
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.trip.domain.repository.TripRepository
import kotlinx.coroutines.launch

class TripOverviewViewModel(
    private val repository: TripRepository
) : BaseViewModel<TripOverviewContract.State, TripOverviewContract.Intent, TripOverviewContract.Effect>(
    TripOverviewContract.State()
) {

    override fun onIntent(intent: TripOverviewContract.Intent) {
        when (intent) {
            is TripOverviewContract.Intent.LoadTripDetails -> loadTripDetails(intent.tripId)
            is TripOverviewContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.tabIndex) }
                val tripId = uiState.value.trip?.id ?: return
                when (intent.tabIndex) {
                    1 -> sendEffect(TripOverviewContract.Effect.NavigateToTimeline(tripId))
                    2 -> sendEffect(TripOverviewContract.Effect.NavigateToBookings(tripId))
                    4 -> sendEffect(TripOverviewContract.Effect.NavigateToBudgets(tripId))
                }
            }
            TripOverviewContract.Intent.BackClicked -> {
                sendEffect(TripOverviewContract.Effect.NavigateBack)
            }
            TripOverviewContract.Intent.AddBookingClicked -> {
                uiState.value.trip?.id?.let {
                    sendEffect(TripOverviewContract.Effect.NavigateToAddBooking(it))
                }
            }
            TripOverviewContract.Intent.AddTaskClicked -> {
                uiState.value.trip?.id?.let {
                    sendEffect(TripOverviewContract.Effect.NavigateToAddTask(it))
                }
            }
            TripOverviewContract.Intent.AddExpenseClicked -> {
                // Handle action
            }
            TripOverviewContract.Intent.AddNoteClicked -> {
                uiState.value.trip?.id?.let {
                    sendEffect(TripOverviewContract.Effect.NavigateToAddNote(it))
                }
            }
            is TripOverviewContract.Intent.UpdateStatus -> updateTripStatus(intent.status)
            TripOverviewContract.Intent.ArchiveTrip -> archiveTrip()
            TripOverviewContract.Intent.DeleteTrip -> deleteTrip()
        }
    }

    private fun updateTripStatus(status: com.android.trippoint.core.common.model.TripStatus) {
        viewModelScope.launch {
            val tripId = uiState.value.trip?.id ?: return@launch
            setState { copy(isLoading = true) }
            val result = repository.updateTrip(id = tripId, status = status)
            if (result.isSuccess) {
                loadTripDetails(tripId)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun archiveTrip() {
        viewModelScope.launch {
            val tripId = uiState.value.trip?.id ?: return@launch
            setState { copy(isLoading = true) }
            val result = repository.archiveTrip(tripId)
            if (result.isSuccess) {
                sendEffect(TripOverviewContract.Effect.NavigateBack)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun deleteTrip() {
        viewModelScope.launch {
            val tripId = uiState.value.trip?.id ?: return@launch
            setState { copy(isLoading = true) }
            val result = repository.deleteTrip(tripId)
            if (result.isSuccess) {
                sendEffect(TripOverviewContract.Effect.NavigateBack)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun loadTripDetails(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            val tripResult = repository.getTrip(tripId)
            val membersResult = repository.getTripMembers(tripId)

            if (tripResult.isSuccess) {
                val trip = tripResult.getOrNull()
                val members = membersResult.getOrDefault(emptyList())
                
                // Map members to travelers
                val travelers = members.map { member ->
                    Traveler(
                        id = member.userId,
                        name = "User ${member.userId}", // Need a way to get user names
                        photoUrl = "",
                        role = member.role,
                        status = member.status
                    )
                }

                setState {
                    copy(
                        trip = trip?.copy(travelers = travelers),
                        isLoading = false,
                        error = null
                    )
                }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        error = tripResult.exceptionOrNull()?.message ?: "Failed to load trip"
                    )
                }
            }
        }
    }
}
