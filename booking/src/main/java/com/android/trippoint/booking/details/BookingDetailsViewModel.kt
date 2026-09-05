package com.android.trippoint.booking.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class BookingDetailsViewModel(
    private val repository: BookingRepository
) : BaseViewModel<BookingDetailsContract.State, BookingDetailsContract.Intent, BookingDetailsContract.Effect>(
    BookingDetailsContract.State()
) {
    override fun onIntent(intent: BookingDetailsContract.Intent) {
        when (intent) {
            is BookingDetailsContract.Intent.LoadBookingDetails -> loadDetails(intent.tripId, intent.bookingId)
            BookingDetailsContract.Intent.BackClicked -> sendEffect(BookingDetailsContract.Effect.NavigateBack)
            BookingDetailsContract.Intent.DeleteClicked -> deleteBooking()
            BookingDetailsContract.Intent.EditClicked -> { /* Handle edit */ }
            BookingDetailsContract.Intent.ShareClicked -> { /* Handle share */ }
        }
    }

    private fun loadDetails(tripId: String, bookingId: String) {
        if (tripId.isBlank()) {
            setState { copy(error = "Trip ID is missing", isLoading = false) }
            return
        }
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId, bookingId = bookingId) }
            val bookingResult = repository.getBooking(tripId, bookingId)
            val travellersResult = repository.getBookingTravellers(tripId, bookingId)
            
            if (bookingResult.isSuccess) {
                setState { 
                    copy(
                        isLoading = false, 
                        booking = bookingResult.getOrNull(),
                        travellers = travellersResult.getOrDefault(emptyList()),
                        error = null
                    ) 
                }
            } else {
                setState { copy(isLoading = false, error = bookingResult.exceptionOrNull()?.message) }
            }
        }
    }

    private fun deleteBooking() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.deleteBooking(uiState.value.tripId, uiState.value.bookingId)
            if (result.isSuccess && result.getOrDefault(false)) {
                sendEffect(BookingDetailsContract.Effect.NavigateBack)
            } else {
                setState { copy(isLoading = false, error = "Failed to delete booking") }
            }
        }
    }
}
