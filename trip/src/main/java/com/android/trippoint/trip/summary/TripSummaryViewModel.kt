package com.android.trippoint.trip.summary

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.trip.domain.repository.TripRepository
import kotlinx.coroutines.launch

class TripSummaryViewModel(
    private val repository: TripRepository
) : BaseViewModel<TripSummaryContract.State, TripSummaryContract.Intent, TripSummaryContract.Effect>(
    TripSummaryContract.State()
) {

    override fun onIntent(intent: TripSummaryContract.Intent) {
        when (intent) {
            is TripSummaryContract.Intent.LoadTrip -> loadTrip(intent.tripId)
            TripSummaryContract.Intent.CreateTripClicked -> finalizeTrip()
            TripSummaryContract.Intent.BackClicked -> sendEffect(TripSummaryContract.Effect.NavigateBack)
        }
    }

    private fun loadTrip(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getTrip(tripId)
            if (result.isSuccess) {
                setState { copy(trip = result.getOrNull(), isLoading = false) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun finalizeTrip() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val tripId = uiState.value.trip?.id ?: return@launch
            val result = repository.updateTrip(id = tripId, status = TripStatus.UPCOMING)
            setState { copy(isLoading = false) }
            if (result.isSuccess) {
                sendEffect(TripSummaryContract.Effect.NavigateToHome)
            } else {
                setState { copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
