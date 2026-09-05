package com.android.trippoint.trip.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddDetailsViewModel : BaseViewModel<
    AddDetailsContract.State,
    AddDetailsContract.Intent,
    AddDetailsContract.Effect
>(
    AddDetailsContract.State()
) {

    override fun onIntent(intent: AddDetailsContract.Intent) {
        when (intent) {
            is AddDetailsContract.Intent.LoadTrip -> loadTrip(intent.tripId)
            AddDetailsContract.Intent.SaveAndContinueClicked -> saveAndContinue()
            AddDetailsContract.Intent.BackClicked -> sendEffect(AddDetailsContract.Effect.NavigateBack)
            is AddDetailsContract.Intent.ToggleSection -> handleSectionClick(intent.sectionId)
        }
    }

    private fun loadTrip(tripId: String) {
        setState { 
            copy(
                tripId = tripId,
                sections = listOf(
                    AddDetailsContract.DetailSection("itinerary", "Itinerary"),
                    AddDetailsContract.DetailSection("bookings", "Bookings"),
                    AddDetailsContract.DetailSection("tasks", "Tasks"),
                    AddDetailsContract.DetailSection("budget", "Budget"),
                    AddDetailsContract.DetailSection("notes", "Notes"),
                    AddDetailsContract.DetailSection("documents", "Documents")
                )
            )
        }
    }

    private fun handleSectionClick(sectionId: String) {
        val tripId = uiState.value.tripId
        when (sectionId) {
            "itinerary" -> sendEffect(AddDetailsContract.Effect.NavigateToItinerary(tripId))
            "tasks" -> sendEffect(AddDetailsContract.Effect.NavigateToTasks(tripId))
            "notes" -> sendEffect(AddDetailsContract.Effect.NavigateToNotes(tripId))
            else -> {
                // For others, we still toggle the "added" state for now
                setState {
                    copy(
                        sections = sections.map {
                            if (it.id == sectionId) it.copy(isAdded = !it.isAdded) else it
                        }
                    )
                }
            }
        }
    }

    private fun saveAndContinue() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(1000)
            setState { copy(isLoading = false) }
            sendEffect(AddDetailsContract.Effect.NavigateToHome)
        }
    }
}
