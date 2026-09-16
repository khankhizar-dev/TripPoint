package com.android.trippoint.trip.collaboration.discussion

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState
import com.android.trippoint.trip.collaboration.domain.model.ChatMessage

class DiscussionContract {
    sealed class Intent : UiIntent {
        data class LoadMessages(val tripId: String) : Intent()
        data class MessageChanged(val content: String) : Intent()
        object SendMessage : Intent()
        object SendImage : Intent()
        data class ReplyToMessage(val message: ChatMessage) : Intent()
        object CancelReply : Intent()
        object BackClicked : Intent()
    }

    data class State(
        val tripId: String = "",
        val messages: List<ChatMessage> = emptyList(),
        val currentMessage: String = "",
        val replyTo: ChatMessage? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
        object ScrollToBottom : Effect()
    }
}
