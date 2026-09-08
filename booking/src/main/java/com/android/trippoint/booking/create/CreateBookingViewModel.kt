package com.android.trippoint.booking.create

import androidx.lifecycle.viewModelScope
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.trip.domain.repository.TripRepository
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.network.CreateBookingInput
import kotlinx.coroutines.launch

class CreateBookingViewModel(
    private val repository: BookingRepository,
    private val tripRepository: TripRepository
) : BaseViewModel<CreateBookingContract.State, CreateBookingContract.Intent, CreateBookingContract.Effect>(
    CreateBookingContract.State()
) {
    override fun onIntent(intent: CreateBookingContract.Intent) {
        when (intent) {
            is CreateBookingContract.Intent.LoadTripId -> {
                setState { copy(tripId = intent.tripId) }
                if (intent.tripId.isBlank()) {
                    loadAvailableTrips()
                }
            }
            is CreateBookingContract.Intent.TitleChanged -> setState { copy(title = intent.title) }
            is CreateBookingContract.Intent.TypeChanged -> setState { copy(type = intent.type) }
            is CreateBookingContract.Intent.DateChanged -> setState { copy(date = intent.date) }
            is CreateBookingContract.Intent.TimeChanged -> setState { copy(time = intent.time) }
            is CreateBookingContract.Intent.EndDateChanged -> setState { copy(endDate = intent.date) }
            is CreateBookingContract.Intent.EndTimeChanged -> setState { copy(endTime = intent.time) }
            is CreateBookingContract.Intent.ProviderChanged -> setState { copy(provider = intent.provider) }
            is CreateBookingContract.Intent.ReferenceChanged -> setState { copy(reference = intent.reference) }
            is CreateBookingContract.Intent.LocationChanged -> setState { copy(location = intent.location) }
            is CreateBookingContract.Intent.AmountChanged -> setState { copy(amount = intent.amount) }
            is CreateBookingContract.Intent.CurrencyChanged -> setState { copy(currency = intent.currency) }
            is CreateBookingContract.Intent.NotesChanged -> setState { copy(notes = intent.notes) }
            CreateBookingContract.Intent.SaveClicked -> saveBooking()
            CreateBookingContract.Intent.BackClicked -> sendEffect(CreateBookingContract.Effect.NavigateBack)
        }
    }

    private fun loadAvailableTrips() {
        viewModelScope.launch {
            val result = tripRepository.getTrips()
            if (result.isSuccess) {
                setState { copy(availableTrips = result.getOrDefault(emptyList())) }
            }
        }
    }

    private fun saveBooking() {
        val tripId = uiState.value.tripId
        if (tripId.isBlank()) {
            setState { copy(error = "Trip ID is missing") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val startAt = if (uiState.value.time.isNotBlank()) {
                "${uiState.value.date}T${uiState.value.time}:00"
            } else {
                "${uiState.value.date}T00:00:00"
            }
            
            val endAt = if (uiState.value.endDate.isNotBlank()) {
                if (uiState.value.endTime.isNotBlank()) {
                    "${uiState.value.endDate}T${uiState.value.endTime}:00"
                } else {
                    "${uiState.value.endDate}T23:59:59"
                }
            } else {
                null
            }
            
            val input = CreateBookingInput(
                title = uiState.value.title,
                type = uiState.value.type,
                startAt = startAt,
                endAt = endAt,
                provider = uiState.value.provider,
                bookingReference = uiState.value.reference,
                location = uiState.value.location,
                amount = uiState.value.amount.toDoubleOrNull(),
                currency = uiState.value.currency,
                notes = uiState.value.notes,
                details = emptyMap<String, Any?>()
            )
            val result = repository.createBooking(tripId, input)
            if (result.isSuccess) {
                setState { copy(isLoading = false) }
                sendEffect(CreateBookingContract.Effect.BookingCreated)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
