package com.android.trippoint.checklist.ai_suggest

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AiSuggestViewModel : BaseViewModel<
    AiSuggestContract.State,
    AiSuggestContract.Intent,
    AiSuggestContract.Effect
>(
    AiSuggestContract.State()
) {
    override fun onIntent(intent: AiSuggestContract.Intent) {
        when (intent) {
            is AiSuggestContract.Intent.LoadSuggestions -> loadSuggestions(intent.tripId)
            is AiSuggestContract.Intent.ItemToggled -> toggleItem(intent.itemId)
            AiSuggestContract.Intent.AddAllClicked -> addItems()
            AiSuggestContract.Intent.RegenerateClicked -> loadSuggestions(uiState.value.tripId)
            AiSuggestContract.Intent.BackClicked -> sendEffect(AiSuggestContract.Effect.NavigateBack)
        }
    }

    private fun loadSuggestions(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId) }
            delay(1500) // Simulate AI processing
            
            val mockSuggestions = listOf(
                AiSuggestContract.SuggestedItem("s1", "Sunscreen"),
                AiSuggestContract.SuggestedItem("s2", "Raincoat"),
                AiSuggestContract.SuggestedItem("s3", "Mosquito Repellent"),
                AiSuggestContract.SuggestedItem("s4", "Universal Adapter")
            )
            
            setState { 
                copy(
                    isLoading = false, 
                    location = "Thailand", 
                    month = "May", 
                    suggestions = mockSuggestions 
                ) 
            }
        }
    }

    private fun toggleItem(itemId: String) {
        val updatedList = uiState.value.suggestions.map {
            if (it.id == itemId) it.copy(isSelected = !it.isSelected) else it
        }
        setState { copy(suggestions = updatedList) }
    }

    private fun addItems() {
        viewModelScope.launch {
            setState { copy(isAdding = true) }
            delay(1000) // Simulation
            setState { copy(isAdding = false) }
            sendEffect(AiSuggestContract.Effect.ItemsAdded)
        }
    }
}
