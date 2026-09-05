package com.android.trippoint.booking.list

import androidx.lifecycle.viewModelScope
import com.android.trippoint.booking.domain.model.BookingStatus
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class BookingListViewModel(
    private val repository: BookingRepository
) : BaseViewModel<BookingListContract.State, BookingListContract.Intent, BookingListContract.Effect>(
    BookingListContract.State()
) {
    override fun onIntent(intent: BookingListContract.Intent) {
        when (intent) {
            is BookingListContract.Intent.LoadBookings -> loadBookings(intent.tripId)
            is BookingListContract.Intent.BookingClicked -> {
                sendEffect(BookingListContract.Effect.NavigateToBookingDetails(intent.tripId, intent.bookingId))
            }
            BookingListContract.Intent.AddBookingClicked -> {
                sendEffect(BookingListContract.Effect.NavigateToCreateBooking(uiState.value.tripId))
            }
            BookingListContract.Intent.BackClicked -> sendEffect(BookingListContract.Effect.NavigateBack)
            is BookingListContract.Intent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
                applyFilters()
            }
            is BookingListContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.index) }
                applyFilters()
            }
        }
    }

    private fun loadBookings(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId) }
            val result = repository.getBookings(if (tripId.isBlank()) null else tripId)
            if (result.isSuccess) {
                val bookings = result.getOrDefault(emptyList())
                setState { 
                    copy(
                        isLoading = false, 
                        bookings = bookings,
                        error = null
                    ) 
                }
                applyFilters()
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun applyFilters() {
        val currentState = uiState.value
        val filtered = currentState.bookings.filter { booking ->
            val matchesQuery = if (currentState.searchQuery.isBlank()) true 
                                else booking.title.contains(currentState.searchQuery, ignoreCase = true) ||
                                     booking.location?.contains(currentState.searchQuery, ignoreCase = true) == true
            
            val statusTab = when (currentState.selectedTab) {
                0 -> booking.status != BookingStatus.CANCELLED &&
                     booking.status != BookingStatus.DRAFT
                1 -> booking.status != BookingStatus.CANCELLED
                2 -> booking.status == BookingStatus.CANCELLED
                else -> true
            }
            
            matchesQuery && statusTab
        }
        setState { copy(filteredBookings = filtered) }
    }
}
