package com.android.trippoint.trip.create

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.trip.domain.repository.TripRepository
import kotlinx.coroutines.launch

class CreateTripViewModel(
    private val repository: TripRepository
) : BaseViewModel<CreateTripContract.State, CreateTripContract.Intent, CreateTripContract.Effect>(
    CreateTripContract.State()
) {

    override fun onIntent(intent: CreateTripContract.Intent) {
        when (intent) {
            is CreateTripContract.Intent.NameChanged -> setState { copy(name = intent.name) }
            is CreateTripContract.Intent.DestinationChanged -> setState { copy(destination = intent.destination) }
            is CreateTripContract.Intent.StartDateChanged -> setState { copy(startDate = intent.date) }
            is CreateTripContract.Intent.EndDateChanged -> setState { copy(endDate = intent.date) }
            CreateTripContract.Intent.NextClicked -> createTrip()
            CreateTripContract.Intent.BackClicked -> sendEffect(CreateTripContract.Effect.NavigateBack)
        }
    }

    private fun createTrip() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            val result = repository.createTrip(
                name = uiState.value.name,
                destination = uiState.value.destination,
                startDate = "${uiState.value.startDate}T00:00:00",
                endDate = "${uiState.value.endDate}T23:59:59"
            )
            
            if (result.isSuccess) {
                val trip = result.getOrNull()
                setState { copy(isLoading = false) }
                if (trip != null) {
                    sendEffect(CreateTripContract.Effect.NavigateToAddDetails(trip.id))
                }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to create trip"
                    )
                }
            }
        }
    }
}
