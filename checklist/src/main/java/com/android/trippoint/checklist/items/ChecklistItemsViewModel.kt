package com.android.trippoint.checklist.items

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.checklist.domain.repository.ChecklistRepository
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.launch

class ChecklistItemsViewModel(
    private val repository: ChecklistRepository,
    private val authRepository: AuthRepository
) : BaseViewModel<
    ChecklistItemsContract.State,
    ChecklistItemsContract.Intent,
    ChecklistItemsContract.Effect
>(
    ChecklistItemsContract.State()
) {
    private var originalItems = emptyList<ChecklistItem>()
    private val modifiedItemIds = mutableSetOf<String>()

    init {
        setState { copy(currentUserId = authRepository.getUserId()) }
    }

    override fun onIntent(intent: ChecklistItemsContract.Intent) {
        when (intent) {
            is ChecklistItemsContract.Intent.LoadSection -> {
                loadSection(intent.tripId, intent.checklistId, intent.sectionId)
            }
            is ChecklistItemsContract.Intent.ItemToggled -> toggleItemLocally(intent.itemId)
            is ChecklistItemsContract.Intent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
                filterItems(uiState.value.items, intent.query, uiState.value.selectedTab)
            }
            is ChecklistItemsContract.Intent.TabSelected -> {
                setState { copy(selectedTab = intent.index) }
                filterItems(uiState.value.items, uiState.value.searchQuery, intent.index)
            }
            is ChecklistItemsContract.Intent.DeleteItem -> deleteItem(intent.itemId)
            ChecklistItemsContract.Intent.SaveClicked -> saveChanges()
            ChecklistItemsContract.Intent.AddItemClicked -> {
                val state = uiState.value
                val effect = ChecklistItemsContract.Effect.NavigateToAddItem(
                    state.tripId, state.checklistId, state.sectionId
                )
                sendEffect(effect)
            }
            ChecklistItemsContract.Intent.BackClicked -> {
                sendEffect(ChecklistItemsContract.Effect.NavigateBack)
            }
            ChecklistItemsContract.Intent.RetryClicked -> {
                val state = uiState.value
                loadSection(state.tripId, state.checklistId, state.sectionId)
            }
        }
    }

    private fun loadSection(tripId: String, checklistId: String, sectionId: String) {
        viewModelScope.launch {
            setState { 
                copy(
                    isLoading = true, 
                    tripId = tripId, 
                    checklistId = checklistId, 
                    sectionId = sectionId,
                    error = null,
                    hasChanges = false
                ) 
            }
            modifiedItemIds.clear()
            val result = repository.getChecklist(tripId, checklistId)
            
            if (result.isSuccess) {
                val checklist = result.getOrThrow()
                val section = checklist.sections.find { it.id == sectionId }
                if (section != null) {
                    originalItems = section.items
                    setState { 
                        copy(
                            isLoading = false, 
                            section = section,
                            items = originalItems
                        ) 
                    }
                    filterItems(originalItems, uiState.value.searchQuery, uiState.value.selectedTab)
                } else {
                    setState { copy(isLoading = false, error = "Section not found") }
                }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun toggleItemLocally(itemId: String) {
        val currentItems = uiState.value.items
        val updatedItems = currentItems.map {
            if (it.id == itemId) it.copy(isCompleted = !it.isCompleted) else it
        }
        
        val item = updatedItems.find { it.id == itemId } ?: return
        val originalItem = originalItems.find { it.id == itemId }
        
        if (item.isCompleted != originalItem?.isCompleted) {
            modifiedItemIds.add(itemId)
        } else {
            modifiedItemIds.remove(itemId)
        }
        
        val completedCount = updatedItems.count { it.isCompleted }
        setState { 
            copy(
                items = updatedItems,
                hasChanges = modifiedItemIds.isNotEmpty(),
                section = section?.copy(completedItems = completedCount)
            ) 
        }
        filterItems(updatedItems, uiState.value.searchQuery, uiState.value.selectedTab)
    }

    private fun saveChanges() {
        val state = uiState.value
        val itemsToSync = state.items.filter { modifiedItemIds.contains(it.id) }
        
        if (itemsToSync.isEmpty()) return

        viewModelScope.launch {
            setState { copy(isSaving = true) }
            
            var allSuccess = true
            itemsToSync.forEach { item ->
                val result = repository.updateItem(
                    tripId = state.tripId,
                    checklistId = state.checklistId,
                    sectionId = state.sectionId,
                    itemId = item.id,
                    isCompleted = item.isCompleted
                )
                if (result.isFailure) allSuccess = false
            }
            
            if (allSuccess) {
                originalItems = state.items
                modifiedItemIds.clear()
                setState { copy(isSaving = false, hasChanges = false) }
                sendEffect(ChecklistItemsContract.Effect.SaveSuccess)
            } else {
                setState { copy(isSaving = false, error = "Failed to sync some items") }
            }
        }
    }

    private fun deleteItem(itemId: String) {
        viewModelScope.launch {
            val state = uiState.value
            setState { copy(isLoading = true) }
            val result = repository.deleteItem(state.tripId, state.checklistId, state.sectionId, itemId)
            
            if (result.isSuccess) {
                loadSection(state.tripId, state.checklistId, state.sectionId)
                sendEffect(ChecklistItemsContract.Effect.ItemDeleted)
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun filterItems(items: List<ChecklistItem>, query: String, tabIndex: Int) {
        var filtered = items.filter { 
            it.name.contains(query, ignoreCase = true) 
        }
        
        if (tabIndex == 1) { // My Tasks
            val currentUserId = uiState.value.currentUserId
            filtered = filtered.filter { it.assigneeId == currentUserId }
        }
        
        setState { copy(filteredItems = filtered) }
    }
}
