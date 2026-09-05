package com.android.trippoint.booking.add

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class AddBookingOptionsContract {
    sealed class Intent : UiIntent {
        object BackClicked : Intent()
        object ImportEmailClicked : Intent()
        object PnrReferenceClicked : Intent()
        object ScanTicketClicked : Intent()
        object ManualEntryClicked : Intent()
        object FileUploadClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val isLoading: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class NavigateToManualEntry(val tripId: String) : Effect()
        data class NavigateToPnrEntry(val tripId: String) : Effect()
        data class NavigateToScanTicket(val tripId: String) : Effect()
        data class NavigateToImportEmail(val tripId: String) : Effect()
    }
}
