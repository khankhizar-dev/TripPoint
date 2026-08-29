package com.android.trippoint.itinerary.list

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository
import kotlinx.coroutines.launch

class TripDaysViewModel(
    private val repository: ItineraryRepository
) : BaseViewModel<TripDaysContract.State, TripDaysContract.Intent, TripDaysContract.Effect>(
    TripDaysContract.State()
) {
    override fun onIntent(intent: TripDaysContract.Intent) {
        when (intent) {
            is TripDaysContract.Intent.LoadTripDays -> loadTripDays(intent.tripId)
            is TripDaysContract.Intent.DayClicked -> {
                val day = uiState.value.days.find { it.id == intent.dayId }
                day?.let {
                    sendEffect(TripDaysContract.Effect.NavigateToDayTimeline(it.tripId, it.date))
                }
            }
            TripDaysContract.Intent.BackClicked -> sendEffect(TripDaysContract.Effect.NavigateBack)
        }
    }

    private fun loadTripDays(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getItineraryDays(tripId)
            if (result.isSuccess) {
                setState { copy(isLoading = false, days = result.getOrDefault(emptyList())) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
