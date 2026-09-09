package com.android.trippoint.checklist.list

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistStatus
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class ChecklistListViewModel(
    private val repository: ChecklistRepository
) : BaseViewModel<
    ChecklistListContract.State,
    ChecklistListContract.Intent,
    ChecklistListContract.Effect
>(
    ChecklistListContract.State()
) {
    private var allChecklists = emptyList<Checklist>()

    override fun onIntent(intent: ChecklistListContract.Intent) {
        when (intent) {
            is ChecklistListContract.Intent.LoadChecklists -> loadChecklists(intent.tripId)
            is ChecklistListContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.index) }
                filterChecklists()
            }
            is ChecklistListContract.Intent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
                filterChecklists()
            }
            is ChecklistListContract.Intent.ChecklistClicked -> {
                sendEffect(ChecklistListContract.Effect.NavigateToDetails(uiState.value.tripId, intent.id))
            }
            ChecklistListContract.Intent.CreateChecklistClicked -> {
                sendEffect(ChecklistListContract.Effect.NavigateToCreate(uiState.value.tripId))
            }
            ChecklistListContract.Intent.BackClicked -> {
                sendEffect(ChecklistListContract.Effect.NavigateBack)
            }
        }
    }

    private fun loadChecklists(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId) }
            val result = repository.getChecklists(tripId)
            
            if (result.isSuccess) {
                allChecklists = result.getOrDefault(emptyList())
                setState { copy(isLoading = false, checklists = allChecklists) }
                filterChecklists()
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun filterChecklists() {
        val query = uiState.value.searchQuery
        val tab = uiState.value.selectedTab
        
        var filtered = allChecklists.filter { 
            it.title.contains(query, ignoreCase = true) 
        }
        
        filtered = when (tab) {
            0 -> filtered // All
            1 -> filtered.filter { it.status == ChecklistStatus.ACTIVE }
            2 -> filtered.filter { it.status == ChecklistStatus.COMPLETED }
            else -> filtered
        }
        
        setState { copy(filteredChecklists = filtered) }
    }
}
