package com.android.trippoint.booking.add

import androidx.lifecycle.viewModelScope
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class PnrIntakeViewModel(
    private val repository: BookingRepository
) : BaseViewModel<PnrIntakeContract.State, PnrIntakeContract.Intent, PnrIntakeContract.Effect>(
    PnrIntakeContract.State()
) {
    override fun onIntent(intent: PnrIntakeContract.Intent) {
        when (intent) {
            is PnrIntakeContract.Intent.LoadTripId -> setState { copy(tripId = intent.tripId) }
            is PnrIntakeContract.Intent.PnrChanged -> setState { copy(pnr = intent.pnr, error = null) }
            PnrIntakeContract.Intent.FetchClicked -> fetchBooking()
            PnrIntakeContract.Intent.BackClicked -> sendEffect(PnrIntakeContract.Effect.NavigateBack)
        }
    }

    private fun fetchBooking() {
        val tripId = uiState.value.tripId
        val pnr = uiState.value.pnr
        
        if (pnr.isBlank()) return
        
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.importBooking(tripId, pnr)
            if (result.isSuccess) {
                setState { copy(isLoading = false) }
                sendEffect(PnrIntakeContract.Effect.BookingAdded)
            } else {
                setState { 
                    copy(
                        isLoading = false, 
                        error = result.exceptionOrNull()?.message ?: "Unknown error"
                    ) 
                }
            }
        }
    }
}
