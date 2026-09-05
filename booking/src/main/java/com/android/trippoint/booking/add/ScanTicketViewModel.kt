package com.android.trippoint.booking.add

import androidx.lifecycle.viewModelScope
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanTicketViewModel(
    private val repository: BookingRepository
) : BaseViewModel<ScanTicketContract.State, ScanTicketContract.Intent, ScanTicketContract.Effect>(
    ScanTicketContract.State()
) {
    override fun onIntent(intent: ScanTicketContract.Intent) {
        when (intent) {
            is ScanTicketContract.Intent.LoadTripId -> setState { copy(tripId = intent.tripId) }
            ScanTicketContract.Intent.BackClicked -> sendEffect(ScanTicketContract.Effect.NavigateBack)
            ScanTicketContract.Intent.ScanClicked -> simulateScan()
        }
    }

    private fun simulateScan() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(2000) // Simulate OCR processing
            // In a real app, we'd use CameraX + ML Kit to scan and then call repository.importBooking
            setState { copy(isLoading = false) }
            sendEffect(ScanTicketContract.Effect.BookingAdded)
        }
    }
}
