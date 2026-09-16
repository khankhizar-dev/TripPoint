package com.android.trippoint.trip.collaboration.discussion

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.trip.collaboration.domain.repository.CollaborationRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DiscussionViewModel(
    private val repository: CollaborationRepository
) : BaseViewModel<
    DiscussionContract.State,
    DiscussionContract.Intent,
    DiscussionContract.Effect
>(
    DiscussionContract.State()
) {
    override fun onIntent(intent: DiscussionContract.Intent) {
        when (intent) {
            is DiscussionContract.Intent.LoadMessages -> loadMessages(intent.tripId)
            is DiscussionContract.Intent.MessageChanged -> setState { copy(currentMessage = intent.content) }
            DiscussionContract.Intent.SendMessage -> sendMessage()
            DiscussionContract.Intent.SendImage -> { /* TODO: Open picker and upload */ }
            is DiscussionContract.Intent.ReplyToMessage -> setState { copy(replyTo = intent.message) }
            DiscussionContract.Intent.CancelReply -> setState { copy(replyTo = null) }
            DiscussionContract.Intent.BackClicked -> sendEffect(DiscussionContract.Effect.NavigateBack)
        }
    }

    private fun loadMessages(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId, error = null) }
            val result = repository.getMessages(tripId)
            
            if (result.isSuccess) {
                setState { copy(isLoading = false, messages = result.getOrDefault(emptyList())) }
                sendEffect(DiscussionContract.Effect.ScrollToBottom)
                observeLiveMessages(tripId)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun observeLiveMessages(tripId: String) {
        viewModelScope.launch {
            repository.observeMessages(tripId).collectLatest { message ->
                setState { copy(messages = messages + message) }
                sendEffect(DiscussionContract.Effect.ScrollToBottom)
            }
        }
    }

    private fun sendMessage() {
        val state = uiState.value
        if (state.currentMessage.isBlank()) return

        viewModelScope.launch {
            val result = repository.sendMessage(
                tripId = state.tripId,
                content = state.currentMessage,
                replyToId = state.replyTo?.id
            )
            
            if (result.isSuccess) {
                setState { copy(currentMessage = "", replyTo = null) }
                // Live message will be received via observer if real, 
                // but for mock we might need to add it manually if observer isn't triggered.
            } else {
                sendEffect(DiscussionContract.Effect.ShowError(result.exceptionOrNull()?.message ?: "Failed"))
            }
        }
    }
}
