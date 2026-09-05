package com.android.trippoint.booking.details

import androidx.lifecycle.viewModelScope
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.network.CreateBookingTravellerInput
import kotlinx.coroutines.launch

class AddTravellerViewModel(
    private val repository: BookingRepository
) : BaseViewModel<AddTravellerContract.State, AddTravellerContract.Intent, AddTravellerContract.Effect>(
    AddTravellerContract.State()
) {
    override fun onIntent(intent: AddTravellerContract.Intent) {
        when (intent) {
            is AddTravellerContract.Intent.LoadIds -> setState {
                copy(tripId = intent.tripId, bookingId = intent.bookingId)
            }
            is AddTravellerContract.Intent.FirstNameChanged -> setState { copy(firstName = intent.value) }
            is AddTravellerContract.Intent.LastNameChanged -> setState { copy(lastName = intent.value) }
            is AddTravellerContract.Intent.EmailChanged -> setState { copy(email = intent.value) }
            is AddTravellerContract.Intent.PhoneChanged -> setState { copy(phone = intent.value) }
            is AddTravellerContract.Intent.DobChanged -> setState { copy(dob = intent.value) }
            is AddTravellerContract.Intent.TicketChanged -> setState { copy(ticket = intent.value) }
            is AddTravellerContract.Intent.SeatChanged -> setState { copy(seat = intent.value) }
            AddTravellerContract.Intent.SaveClicked -> saveTraveller()
            AddTravellerContract.Intent.BackClicked -> sendEffect(AddTravellerContract.Effect.NavigateBack)
        }
    }

    private fun saveTraveller() {
        val state = uiState.value
        if (state.firstName.isBlank() || state.lastName.isBlank()) {
            setState { copy(error = "Name is required") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val input = CreateBookingTravellerInput(
                firstName = state.firstName,
                lastName = state.lastName,
                email = state.email.takeIf { it.isNotBlank() },
                phoneNumber = state.phone.takeIf { it.isNotBlank() },
                dateOfBirth = state.dob.takeIf { it.isNotBlank() },
                ticketNumber = state.ticket.takeIf { it.isNotBlank() },
                seatNumber = state.seat.takeIf { it.isNotBlank() }
            )
            
            val result = repository.addBookingTraveller(state.tripId, state.bookingId, input)
            if (result.isSuccess) {
                setState { copy(isLoading = false) }
                sendEffect(AddTravellerContract.Effect.TravellerAdded)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
