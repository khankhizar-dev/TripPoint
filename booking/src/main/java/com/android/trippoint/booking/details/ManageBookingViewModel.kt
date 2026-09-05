package com.android.trippoint.booking.details

import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.core.common.BaseViewModel

class ManageBookingViewModel(
    private val repository: BookingRepository
) : BaseViewModel<ManageBookingContract.State, ManageBookingContract.Intent, ManageBookingContract.Effect>(
    ManageBookingContract.State()
) {
    override fun onIntent(intent: ManageBookingContract.Intent) {
        when (intent) {
            is ManageBookingContract.Intent.LoadBooking -> {
                if (intent.tripId.isBlank()) {
                    setState { copy(error = "Trip ID is missing") }
                } else {
                    setState { copy(tripId = intent.tripId, bookingId = intent.bookingId, error = null) }
                }
            }
            ManageBookingContract.Intent.BackClicked -> sendEffect(ManageBookingContract.Effect.NavigateBack)
            ManageBookingContract.Intent.ChangeFlightClicked -> { /* logic */ }
            ManageBookingContract.Intent.CancelBookingClicked -> { /* logic */ }
            ManageBookingContract.Intent.UpgradeClicked -> { /* logic */ }
            ManageBookingContract.Intent.SelectSeatsClicked -> { /* logic */ }
            ManageBookingContract.Intent.AddBaggageClicked -> { /* logic */ }
        }
    }
}
