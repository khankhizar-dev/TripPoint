package com.android.trippoint.checklist.items

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class ChecklistItemsViewModel(
    private val repository: ChecklistRepository
) : BaseViewModel<
    ChecklistItemsContract.State,
    ChecklistItemsContract.Intent,
    ChecklistItemsContract.Effect
>(
    ChecklistItemsContract.State()
) {
    private var allItems = emptyList<ChecklistItem>()
    private var tripId: String = ""
    private var checklistId: String = ""
    private var sectionId: String = ""

    override fun onIntent(intent: ChecklistItemsContract.Intent) {
        when (intent) {
            is ChecklistItemsContract.Intent.LoadSection -> {
                loadSection(intent.tripId, intent.checklistId, intent.sectionId)
            }
            is ChecklistItemsContract.Intent.ItemToggled -> toggleItem(intent.itemId)
            is ChecklistItemsContract.Intent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
                filterItems()
            }
            ChecklistItemsContract.Intent.AddItemClicked -> {
                sendEffect(ChecklistItemsContract.Effect.NavigateToAddItem(tripId, checklistId, sectionId))
            }
            ChecklistItemsContract.Intent.BackClicked -> {
                sendEffect(ChecklistItemsContract.Effect.NavigateBack)
            }
        }
    }

    private fun loadSection(tripId: String, checklistId: String, sectionId: String) {
        this.tripId = tripId
        this.checklistId = checklistId
        this.sectionId = sectionId
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = repository.getChecklist(tripId, checklistId)
            
            if (result.isSuccess) {
                val checklist = result.getOrThrow()
                val section = checklist.sections.find { it.id == sectionId }
                if (section != null) {
                    allItems = section.items
                    setState { 
                        copy(
                            isLoading = false, 
                            section = section,
                            items = allItems,
                            filteredItems = allItems
                        ) 
                    }
                } else {
                    setState { copy(isLoading = false, error = "Section not found") }
                }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun toggleItem(itemId: String) {
        val item = allItems.find { it.id == itemId } ?: return
        val newCompleted = !item.isCompleted
        
        viewModelScope.launch {
            val result = repository.updateItem(
                tripId = tripId,
                checklistId = checklistId,
                sectionId = sectionId,
                itemId = itemId,
                isCompleted = newCompleted
            )
            
            if (result.isSuccess) {
                allItems = allItems.map {
                    if (it.id == itemId) it.copy(isCompleted = newCompleted) else it
                }
                val completedCount = allItems.count { it.isCompleted }
                setState { 
                    copy(
                        items = allItems,
                        section = section?.copy(completedItems = completedCount)
                    ) 
                }
                filterItems()
            }
        }
    }

    private fun filterItems() {
        val query = uiState.value.searchQuery
        val filtered = allItems.filter { 
            it.name.contains(query, ignoreCase = true) 
        }
        setState { copy(filteredItems = filtered) }
    }
}
