package com.android.trippoint.trip.collaboration.activity

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.trip.collaboration.domain.repository.CollaborationRepository
import kotlinx.coroutines.launch

class ActivityFeedViewModel(
    private val repository: CollaborationRepository
) : BaseViewModel<
    ActivityFeedContract.State,
    ActivityFeedContract.Intent,
    ActivityFeedContract.Effect
>(
    ActivityFeedContract.State()
) {
    override fun onIntent(intent: ActivityFeedContract.Intent) {
        when (intent) {
            is ActivityFeedContract.Intent.LoadFeed -> loadFeed(intent.tripId)
            ActivityFeedContract.Intent.BackClicked -> sendEffect(ActivityFeedContract.Effect.NavigateBack)
        }
    }

    private fun loadFeed(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId, error = null) }
            val result = repository.getActivityLogs(tripId)
            
            if (result.isSuccess) {
                setState { copy(isLoading = false, activities = result.getOrDefault(emptyList())) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
