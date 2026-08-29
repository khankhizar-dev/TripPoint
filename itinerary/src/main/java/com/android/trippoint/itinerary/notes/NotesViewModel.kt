package com.android.trippoint.itinerary.notes

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository
import kotlinx.coroutines.launch

class NotesViewModel(
    private val repository: ItineraryRepository
) : BaseViewModel<
    NotesContract.State,
    NotesContract.Intent,
    NotesContract.Effect
>(
    NotesContract.State()
) {
    override fun onIntent(intent: NotesContract.Intent) {
        when (intent) {
            is NotesContract.Intent.LoadNotes -> {
                setState { copy(tripId = intent.tripId) }
                loadNotes(intent.tripId)
            }
            is NotesContract.Intent.SearchQueryChanged -> setState { copy(searchQuery = intent.query) }
            NotesContract.Intent.AddNoteClicked -> {
                sendEffect(NotesContract.Effect.NavigateToAddNote(uiState.value.tripId))
            }
            NotesContract.Intent.BackClicked -> sendEffect(NotesContract.Effect.NavigateBack)
        }
    }

    private fun loadNotes(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            // We need to fetch days then activities and filter for notes or just use day 1 as placeholder
            val daysResult = repository.getItineraryDays(tripId)
            val dayId = daysResult.getOrNull()?.firstOrNull()?.id
            
            if (dayId != null) {
                val activitiesResult = repository.getItineraryActivities(tripId, dayId)
                if (activitiesResult.isSuccess) {
                    val activities = activitiesResult.getOrDefault(emptyList())
                    // Map to notes if description exists or type matches
                    val notes = activities.map { 
                        com.android.trippoint.itinerary.domain.model.TripNote(
                            id = it.id,
                            title = it.title,
                            content = it.description ?: "",
                            date = it.createdAt,
                            priority = com.android.trippoint.itinerary.domain.model.Priority.MEDIUM
                        )
                    }
                    setState { copy(isLoading = false, notes = notes, error = null) }
                } else {
                    setState { copy(isLoading = false, error = activitiesResult.exceptionOrNull()?.message) }
                }
            } else {
                setState { copy(isLoading = false, notes = emptyList()) }
            }
        }
    }
}
