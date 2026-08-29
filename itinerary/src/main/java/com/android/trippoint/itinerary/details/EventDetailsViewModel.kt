package com.android.trippoint.itinerary.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository
import kotlinx.coroutines.launch

class EventDetailsViewModel(
    private val repository: ItineraryRepository
) : BaseViewModel<
    EventDetailsContract.State,
    EventDetailsContract.Intent,
    EventDetailsContract.Effect
>(
    EventDetailsContract.State()
) {
    override fun onIntent(intent: EventDetailsContract.Intent) {
        when (intent) {
            is EventDetailsContract.Intent.LoadEventDetails -> {
                setState { copy(tripId = intent.tripId, dayId = intent.dayId, activityId = intent.activityId) }
                loadEventDetails(intent.tripId, intent.dayId, intent.activityId)
            }
            EventDetailsContract.Intent.BackClicked -> sendEffect(EventDetailsContract.Effect.NavigateBack)
            EventDetailsContract.Intent.EditClicked -> { /* Handle edit */ }
            EventDetailsContract.Intent.ShareClicked -> { /* Handle share */ }
            EventDetailsContract.Intent.DeleteClicked -> deleteEvent()
        }
    }

    private fun loadEventDetails(tripId: String, dayId: String, activityId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getItineraryActivity(tripId, dayId, activityId)
            if (result.isSuccess) {
                setState { copy(isLoading = false, event = result.getOrNull(), error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun deleteEvent() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val state = uiState.value
            val result = repository.deleteItineraryActivity(state.tripId, state.dayId, state.activityId)
            if (result.isSuccess) {
                sendEffect(EventDetailsContract.Effect.NavigateBack)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
