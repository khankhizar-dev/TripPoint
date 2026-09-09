package com.android.trippoint.trip.overview

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.designsystem.R as designR
import com.android.trippoint.trip.domain.repository.TripRepository
import com.android.trippoint.trip.domain.usecase.GetTripOverviewUseCase
import kotlinx.coroutines.launch

class TripOverviewViewModel(
    private val repository: TripRepository,
    private val getTripOverviewUseCase: GetTripOverviewUseCase
) : BaseViewModel<TripOverviewContract.State, TripOverviewContract.Intent, TripOverviewContract.Effect>(
    TripOverviewContract.State()
) {

    override fun onIntent(intent: TripOverviewContract.Intent) {
        when (intent) {
            is TripOverviewContract.Intent.LoadTripDetails -> loadTripDetails(intent.tripId)
            is TripOverviewContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.tabIndex) }
                val tripId = uiState.value.trip?.id ?: return
                when (intent.tabIndex) {
                    1 -> sendEffect(TripOverviewContract.Effect.NavigateToTimeline(tripId))
                    2 -> sendEffect(TripOverviewContract.Effect.NavigateToBookings(tripId))
                    3 -> sendEffect(TripOverviewContract.Effect.NavigateToTimeline(tripId))
                    4 -> sendEffect(TripOverviewContract.Effect.NavigateToBudgets(tripId))
                }
            }
            TripOverviewContract.Intent.BackClicked -> {
                sendEffect(TripOverviewContract.Effect.NavigateBack)
            }
            TripOverviewContract.Intent.AddBookingClicked -> {
                uiState.value.trip?.id?.let {
                    sendEffect(TripOverviewContract.Effect.NavigateToAddBooking(it))
                }
            }
            TripOverviewContract.Intent.AddTaskClicked -> {
                uiState.value.trip?.id?.let {
                    sendEffect(TripOverviewContract.Effect.NavigateToAddTask(it))
                }
            }
            TripOverviewContract.Intent.AddExpenseClicked -> {
                uiState.value.trip?.id?.let {
                    sendEffect(TripOverviewContract.Effect.NavigateToAddExpense(it))
                }
            }
            TripOverviewContract.Intent.AddNoteClicked -> {
                uiState.value.trip?.id?.let {
                    sendEffect(TripOverviewContract.Effect.NavigateToAddNote(it))
                }
            }
            is TripOverviewContract.Intent.UpdateStatus -> updateTripStatus(intent.status)
            TripOverviewContract.Intent.ArchiveTrip -> archiveTrip()
            TripOverviewContract.Intent.DeleteTrip -> deleteTrip()
        }
    }

    private fun updateTripStatus(status: com.android.trippoint.core.common.model.TripStatus) {
        viewModelScope.launch {
            val tripId = uiState.value.trip?.id ?: return@launch
            setState { copy(isLoading = true) }
            val result = repository.updateTrip(id = tripId, status = status)
            if (result.isSuccess) {
                loadTripDetails(tripId)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun archiveTrip() {
        viewModelScope.launch {
            val tripId = uiState.value.trip?.id ?: return@launch
            setState { copy(isLoading = true) }
            val result = repository.archiveTrip(tripId)
            if (result.isSuccess) {
                sendEffect(TripOverviewContract.Effect.NavigateBack)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun deleteTrip() {
        viewModelScope.launch {
            val tripId = uiState.value.trip?.id ?: return@launch
            setState { copy(isLoading = true) }
            val result = repository.deleteTrip(tripId)
            if (result.isSuccess) {
                sendEffect(TripOverviewContract.Effect.NavigateBack)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun loadTripDetails(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            val result = getTripOverviewUseCase(tripId)

            if (result.isSuccess) {
                val data = result.getOrThrow()
                setState {
                    copy(
                        trip = data.trip.copy(
                            budget = data.budgetSummary ?: "",
                            tasksCount = data.totalTasks,
                            completedTasksCount = data.completedTasks
                        ),
                        isLoading = false,
                        error = null,
                        errorResId = null
                    )
                }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        errorResId = designR.string.error_failed_load_trip
                    )
                }
            }
        }
    }
}
