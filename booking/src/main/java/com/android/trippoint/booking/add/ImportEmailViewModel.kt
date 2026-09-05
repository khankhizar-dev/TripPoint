package com.android.trippoint.booking.add

import androidx.lifecycle.viewModelScope
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImportEmailViewModel(
    private val repository: BookingRepository
) : BaseViewModel<ImportEmailContract.State, ImportEmailContract.Intent, ImportEmailContract.Effect>(
    ImportEmailContract.State()
) {
    override fun onIntent(intent: ImportEmailContract.Intent) {
        when (intent) {
            is ImportEmailContract.Intent.LoadTripId -> setState { copy(tripId = intent.tripId) }
            ImportEmailContract.Intent.BackClicked -> sendEffect(ImportEmailContract.Effect.NavigateBack)
            is ImportEmailContract.Intent.ProviderClicked -> connectProvider(intent.provider)
        }
    }

    private fun connectProvider(provider: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(1500) // Simulate OAuth/Sync
            // logic to trigger backend sync
            setState { copy(isLoading = false) }
            sendEffect(ImportEmailContract.Effect.Success)
        }
    }
}
