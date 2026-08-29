package com.android.trippoint.itinerary.day

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository
import kotlinx.coroutines.launch

class TimelineViewModel(
    private val repository: ItineraryRepository
) : BaseViewModel<TimelineContract.State, TimelineContract.Intent, TimelineContract.Effect>(
    TimelineContract.State()
) {
    override fun onIntent(intent: TimelineContract.Intent) {
        when (intent) {
            is TimelineContract.Intent.LoadTimeline -> {
                setState { copy(tripId = intent.tripId, selectedDate = intent.date) }
                loadTimeline(intent.tripId, intent.date)
            }
            TimelineContract.Intent.AddEventClicked -> {
                sendEffect(TimelineContract.Effect.NavigateToAddEvent(uiState.value.tripId, uiState.value.selectedDate))
            }
            TimelineContract.Intent.AddTaskClicked -> {
                sendEffect(TimelineContract.Effect.NavigateToAddTask(uiState.value.tripId, uiState.value.selectedDate))
            }
            TimelineContract.Intent.AddNoteClicked -> {
                sendEffect(TimelineContract.Effect.NavigateToAddNote(uiState.value.tripId))
            }
            TimelineContract.Intent.FilterClicked -> {
                sendEffect(TimelineContract.Effect.NavigateToFilter(uiState.value.tripId))
            }
            is TimelineContract.Intent.EventClicked -> {
                sendEffect(TimelineContract.Effect.NavigateToEventDetails(
                    uiState.value.tripId, 
                    uiState.value.dayId, 
                    intent.eventId
                ))
            }
            is TimelineContract.Intent.ToggleEventCompletion -> toggleEventCompletion(intent.eventId, intent.completed)
            TimelineContract.Intent.BackClicked -> sendEffect(TimelineContract.Effect.NavigateBack)
        }
    }

    private fun toggleEventCompletion(eventId: String, completed: Boolean) {
        viewModelScope.launch {
            val result = repository.markItineraryActivityCompleted(
                tripId = uiState.value.tripId,
                itineraryDayId = uiState.value.dayId,
                activityId = eventId,
                completed = completed
            )
            if (result.isSuccess) {
                // Refresh timeline to show updated state
                loadTimeline(uiState.value.tripId, uiState.value.selectedDate)
            } else {
                sendEffect(TimelineContract.Effect.ShowError(result.exceptionOrNull()?.message ?: "Update failed"))
            }
        }
    }

    private fun loadTimeline(tripId: String, date: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            // First get the dayId for the given date
            val daysResult = repository.getItineraryDays(tripId)
            val dayId = daysResult.getOrNull()?.find { it.date == date }?.id
            
            if (dayId != null) {
                setState { copy(dayId = dayId) }
                val activitiesResult = repository.getItineraryActivities(tripId, dayId)
                if (activitiesResult.isSuccess) {
                    setState { 
                        copy(
                            isLoading = false, 
                            events = activitiesResult.getOrDefault(emptyList()), 
                            error = null
                        ) 
                    }
                } else {
                    setState { copy(isLoading = false, error = activitiesResult.exceptionOrNull()?.message) }
                }
            } else {
                setState { copy(isLoading = false, error = "Itinerary day not found for date: $date") }
            }
        }
    }
}
