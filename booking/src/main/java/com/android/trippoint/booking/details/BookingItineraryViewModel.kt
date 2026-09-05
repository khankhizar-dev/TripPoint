package com.android.trippoint.booking.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class BookingItineraryViewModel(
    private val repository: BookingRepository
) : BaseViewModel<BookingItineraryContract.State, BookingItineraryContract.Intent, BookingItineraryContract.Effect>(
    BookingItineraryContract.State()
) {
    override fun onIntent(intent: BookingItineraryContract.Intent) {
        when (intent) {
            is BookingItineraryContract.Intent.LoadItinerary -> loadItinerary(intent.tripId, intent.bookingId)
            BookingItineraryContract.Intent.BackClicked -> sendEffect(BookingItineraryContract.Effect.NavigateBack)
        }
    }

    private fun loadItinerary(tripId: String, bookingId: String) {
        if (tripId.isBlank()) {
            setState { copy(error = "Trip ID is missing", isLoading = false) }
            return
        }
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId, bookingId = bookingId) }
            val result = repository.getBooking(tripId, bookingId)
            if (result.isSuccess) {
                setState { copy(isLoading = false, booking = result.getOrNull(), error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
