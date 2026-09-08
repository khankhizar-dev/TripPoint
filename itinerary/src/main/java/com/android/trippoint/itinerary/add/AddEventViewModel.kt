package com.android.trippoint.itinerary.add

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.itinerary.domain.model.CreateActivityInput
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEventViewModel(
    private val repository: ItineraryRepository
) : BaseViewModel<
    AddEventContract.State,
    AddEventContract.Intent,
    AddEventContract.Effect
>(
    AddEventContract.State()
) {
    override fun onIntent(intent: AddEventContract.Intent) {
        when (intent) {
            is AddEventContract.Intent.LoadTripInfo -> loadTripInfo(intent.tripId, intent.date)
            is AddEventContract.Intent.NameChanged -> setState { copy(name = intent.name) }
            is AddEventContract.Intent.DateChanged -> setState { copy(date = intent.date) }
            is AddEventContract.Intent.TimeChanged -> setState { copy(time = intent.time) }
            is AddEventContract.Intent.LocationChanged -> setState { copy(location = intent.location) }
            is AddEventContract.Intent.CategoryChanged -> setState { copy(category = intent.category) }
            AddEventContract.Intent.SaveClicked -> saveEvent()
            AddEventContract.Intent.BackClicked -> sendEffect(AddEventContract.Effect.NavigateBack)
        }
    }

    private fun loadTripInfo(tripId: String, date: String) {
        val resolvedDate = if (date == "today") {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        } else {
            date
        }
        setState { copy(tripId = tripId, date = resolvedDate) }
    }

    private fun saveEvent() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val state = uiState.value
            
            try {
                val daysResult = repository.getItineraryDays(state.tripId)
                var dayId = daysResult.getOrNull()?.find { it.date == state.date }?.id
                
                if (dayId == null) {
                    dayId = createNewDay(state.tripId, state.date, daysResult.getOrNull()?.size ?: 0)
                    if (dayId == null) return@launch
                }

                val input = CreateActivityInput(
                    title = state.name,
                    description = "",
                    type = state.category.uppercase(),
                    startTime = "${state.date}T${state.time.ifEmpty { "00:00" }}:00",
                    endTime = null,
                    location = state.location,
                    latitude = null,
                    longitude = null,
                    sortOrder = 0
                )
                val result = repository.createItineraryActivity(state.tripId, dayId, input)
                if (result.isSuccess) {
                    setState { copy(isLoading = false) }
                    sendEffect(AddEventContract.Effect.EventAdded)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to create event"
                    setState { copy(isLoading = false, error = error) }
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message ?: "An unexpected error occurred") }
            }
        }
    }

    private suspend fun createNewDay(tripId: String, date: String, currentDaysCount: Int): String? {
        val dayNumber = currentDaysCount + 1
        val newDayResult = repository.createItineraryDay(
            tripId = tripId,
            dayNumber = dayNumber,
            date = date,
            title = "Day $dayNumber",
            notes = ""
        )
        return if (newDayResult.isSuccess) {
            newDayResult.getOrNull()?.id
        } else {
            val errorMsg = newDayResult.exceptionOrNull()?.message ?: "Failed to create day"
            setState { copy(isLoading = false, error = errorMsg) }
            null
        }
    }
}
