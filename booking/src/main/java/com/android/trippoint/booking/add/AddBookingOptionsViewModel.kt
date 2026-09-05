package com.android.trippoint.booking.add

import com.android.trippoint.core.common.BaseViewModel

class AddBookingOptionsViewModel : BaseViewModel<
    AddBookingOptionsContract.State,
    AddBookingOptionsContract.Intent,
    AddBookingOptionsContract.Effect
>(
    AddBookingOptionsContract.State()
) {
    override fun onIntent(intent: AddBookingOptionsContract.Intent) {
        when (intent) {
            AddBookingOptionsContract.Intent.BackClicked -> sendEffect(AddBookingOptionsContract.Effect.NavigateBack)
            AddBookingOptionsContract.Intent.ManualEntryClicked -> {
                sendEffect(AddBookingOptionsContract.Effect.NavigateToManualEntry(uiState.value.tripId))
            }
            AddBookingOptionsContract.Intent.PnrReferenceClicked -> {
                sendEffect(AddBookingOptionsContract.Effect.NavigateToPnrEntry(uiState.value.tripId))
            }
            AddBookingOptionsContract.Intent.ScanTicketClicked -> {
                sendEffect(AddBookingOptionsContract.Effect.NavigateToScanTicket(uiState.value.tripId))
            }
            AddBookingOptionsContract.Intent.ImportEmailClicked -> {
                sendEffect(AddBookingOptionsContract.Effect.NavigateToImportEmail(uiState.value.tripId))
            }
            else -> { /* Handle others */ }
        }
    }
    
    fun setTripId(tripId: String) {
        setState { copy(tripId = tripId) }
    }
}
