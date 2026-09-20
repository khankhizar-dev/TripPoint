package com.android.trippoint.notification.history

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: NotificationRepository
) : BaseViewModel<
    HistoryContract.State,
    HistoryContract.Intent,
    HistoryContract.Effect
>(
    HistoryContract.State()
) {

    init {
        onIntent(HistoryContract.Intent.LoadHistory())
    }

    override fun onIntent(intent: HistoryContract.Intent) {
        when (intent) {
            is HistoryContract.Intent.LoadHistory -> loadHistory(intent.query)
            HistoryContract.Intent.ClearHistory -> clearHistory()
            HistoryContract.Intent.BackClicked -> sendEffect(HistoryContract.Effect.NavigateBack)
        }
    }

    private fun loadHistory(query: String?) {
        viewModelScope.launch {
            setState { copy(isLoading = true, searchQuery = query ?: "") }
            val result = repository.getHistoryLogs(query)
            if (result.isSuccess) {
                setState { copy(isLoading = false, logs = result.getOrThrow()) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            loadHistory(null)
        }
    }
}
