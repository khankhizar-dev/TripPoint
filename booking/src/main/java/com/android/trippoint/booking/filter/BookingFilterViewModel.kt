package com.android.trippoint.booking.filter

import com.android.trippoint.core.common.BaseViewModel

class BookingFilterViewModel : BaseViewModel<
    BookingFilterContract.State,
    BookingFilterContract.Intent,
    BookingFilterContract.Effect
>(
    BookingFilterContract.State()
) {
    override fun onIntent(intent: BookingFilterContract.Intent) {
        when (intent) {
            BookingFilterContract.Intent.BackClicked -> sendEffect(BookingFilterContract.Effect.NavigateBack)
            BookingFilterContract.Intent.ApplyClicked -> {
                sendEffect(BookingFilterContract.Effect.FiltersApplied(uiState.value))
            }
            BookingFilterContract.Intent.ResetClicked -> setState { BookingFilterContract.State() }
            is BookingFilterContract.Intent.SearchQueryChanged -> setState { copy(searchQuery = intent.query) }
            is BookingFilterContract.Intent.TypeSelected -> setState { copy(selectedType = intent.type) }
            is BookingFilterContract.Intent.StatusSelected -> setState { copy(selectedStatus = intent.status) }
            is BookingFilterContract.Intent.DateSelected -> setState { copy(selectedDate = intent.date) }
            is BookingFilterContract.Intent.ProviderSelected -> setState { copy(selectedProvider = intent.provider) }
        }
    }
}
