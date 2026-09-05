package com.android.trippoint.trip.list

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.trip.domain.repository.TripRepository
import kotlinx.coroutines.launch

class TripListViewModel(
    private val repository: TripRepository
) : BaseViewModel<TripListContract.State, TripListContract.Intent, TripListContract.Effect>(
    TripListContract.State()
) {

    init {
        onIntent(TripListContract.Intent.LoadTrips)
    }

    override fun onIntent(intent: TripListContract.Intent) {
        when (intent) {
            TripListContract.Intent.LoadTrips -> loadTrips()
            is TripListContract.Intent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
                loadTrips() // API supports search
            }
            is TripListContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.tabIndex) }
                loadTrips() // API supports status filter
            }
            is TripListContract.Intent.TripClicked -> {
                sendEffect(TripListContract.Effect.NavigateToTripDetails(intent.tripId))
            }
            TripListContract.Intent.CreateTripClicked -> {
                sendEffect(TripListContract.Effect.NavigateToCreateTrip)
            }
        }
    }

    private fun loadTrips() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            val status = when (uiState.value.selectedTab) {
                0 -> TripStatus.UPCOMING
                1 -> TripStatus.IN_PROGRESS
                2 -> TripStatus.COMPLETED
                3 -> TripStatus.DRAFT
                4 -> TripStatus.ARCHIVED
                else -> null
            }
            
            val result = repository.getTrips(
                status = status,
                search = uiState.value.searchQuery.ifBlank { null }
            )
            
            if (result.isSuccess) {
                val trips = result.getOrDefault(emptyList())
                setState {
                    copy(
                        trips = trips,
                        filteredTrips = trips,
                        isLoading = false,
                        error = null
                    )
                }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load trips"
                    )
                }
            }
        }
    }
}
