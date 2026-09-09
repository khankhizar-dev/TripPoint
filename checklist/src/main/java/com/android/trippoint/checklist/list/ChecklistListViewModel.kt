package com.android.trippoint.checklist.list

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistStatus
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChecklistListViewModel : BaseViewModel<
    ChecklistListContract.State,
    ChecklistListContract.Intent,
    ChecklistListContract.Effect
>(
    ChecklistListContract.State()
) {
    private var allChecklists = emptyList<Checklist>()

    override fun onIntent(intent: ChecklistListContract.Intent) {
        when (intent) {
            ChecklistListContract.Intent.LoadChecklists -> loadChecklists()
            is ChecklistListContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.index) }
                filterChecklists()
            }
            is ChecklistListContract.Intent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
                filterChecklists()
            }
            is ChecklistListContract.Intent.ChecklistClicked -> {
                sendEffect(ChecklistListContract.Effect.NavigateToDetails(intent.id))
            }
            ChecklistListContract.Intent.CreateChecklistClicked -> {
                sendEffect(ChecklistListContract.Effect.NavigateToCreate)
            }
            ChecklistListContract.Intent.BackClicked -> {
                sendEffect(ChecklistListContract.Effect.NavigateBack)
            }
        }
    }

    private fun loadChecklists() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(1000) // Simulation
            
            allChecklists = listOf(
                Checklist(
                    id = "c1", tripId = "t1", title = "Thailand Trip", 
                    dateRange = "24 May - 2 Jun", totalItems = 55, completedItems = 33,
                    status = ChecklistStatus.ACTIVE, createdAt = "now", updatedAt = "now"
                ),
                Checklist(
                    id = "c2", tripId = "t2", title = "Goa Trip with Friends", 
                    dateRange = "10 May - 14 May", totalItems = 30, completedItems = 18,
                    status = ChecklistStatus.ACTIVE, createdAt = "now", updatedAt = "now"
                ),
                Checklist(
                    id = "c3", tripId = "t3", title = "Business Trip - Dubai", 
                    dateRange = "5 May - 7 May", totalItems = 25, completedItems = 5,
                    status = ChecklistStatus.ACTIVE, createdAt = "now", updatedAt = "now"
                ),
                Checklist(
                    id = "c4", tripId = "t4", title = "Weekend Getaway", 
                    dateRange = "24 Apr - 26 Apr", totalItems = 15, completedItems = 15,
                    status = ChecklistStatus.COMPLETED, createdAt = "now", updatedAt = "now"
                )
            )
            
            setState { copy(isLoading = false, checklists = allChecklists) }
            filterChecklists()
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
