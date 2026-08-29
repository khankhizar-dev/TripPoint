package com.android.trippoint.itinerary.notes

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.itinerary.domain.model.CreateActivityInput
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNoteViewModel(
    private val repository: ItineraryRepository
) : BaseViewModel<
    AddNoteContract.State,
    AddNoteContract.Intent,
    AddNoteContract.Effect
>(
    AddNoteContract.State()
) {
    override fun onIntent(intent: AddNoteContract.Intent) {
        when (intent) {
            is AddNoteContract.Intent.LoadTripId -> setState { copy(tripId = intent.tripId) }
            is AddNoteContract.Intent.TitleChanged -> setState { copy(title = intent.title) }
            is AddNoteContract.Intent.ContentChanged -> setState { copy(content = intent.content) }
            AddNoteContract.Intent.SaveClicked -> saveNote()
            AddNoteContract.Intent.BackClicked -> sendEffect(AddNoteContract.Effect.NavigateBack)
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val state = uiState.value
            
            try {
                val daysResult = repository.getItineraryDays(state.tripId)
                var dayId = daysResult.getOrNull()?.firstOrNull()?.id
                
                if (dayId == null) {
                    dayId = createDefaultDay(state.tripId)
                    if (dayId == null) return@launch
                }

                val input = CreateActivityInput(
                    title = state.title,
                    description = state.content,
                    type = "ACTIVITY",
                    startTime = "00:00",
                    endTime = null,
                    location = "",
                    latitude = null,
                    longitude = null,
                    sortOrder = 0
                )
                val result = repository.createItineraryActivity(state.tripId, dayId, input)
                if (result.isSuccess) {
                    setState { copy(isLoading = false) }
                    sendEffect(AddNoteContract.Effect.NoteAdded)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to create note"
                    setState { copy(isLoading = false, error = error) }
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message ?: "An unexpected error occurred") }
            }
        }
    }

    private suspend fun createDefaultDay(tripId: String): String? {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val newDayResult = repository.createItineraryDay(
            tripId = tripId,
            dayNumber = 1,
            date = today,
            title = "Day 1",
            notes = ""
        )
        return if (newDayResult.isSuccess) {
            newDayResult.getOrNull()?.id
        } else {
            val errorMsg = newDayResult.exceptionOrNull()?.message ?: "Failed to create Day 1"
            setState { copy(isLoading = false, error = errorMsg) }
            null
        }
    }
}
