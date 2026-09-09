package com.android.trippoint.checklist.items

import androidx.lifecycle.viewModelScope
import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChecklistItemsViewModel : BaseViewModel<
    ChecklistItemsContract.State,
    ChecklistItemsContract.Intent,
    ChecklistItemsContract.Effect
>(
    ChecklistItemsContract.State()
) {
    private var allItems = emptyList<ChecklistItem>()
    private var checklistId: String = ""
    private var sectionId: String = ""

    override fun onIntent(intent: ChecklistItemsContract.Intent) {
        when (intent) {
            is ChecklistItemsContract.Intent.LoadSection -> loadSection(intent.checklistId, intent.sectionId)
            is ChecklistItemsContract.Intent.ItemToggled -> toggleItem(intent.itemId)
            is ChecklistItemsContract.Intent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
                filterItems()
            }
            ChecklistItemsContract.Intent.AddItemClicked -> {
                sendEffect(ChecklistItemsContract.Effect.NavigateToAddItem(checklistId, sectionId))
            }
            ChecklistItemsContract.Intent.BackClicked -> {
                sendEffect(ChecklistItemsContract.Effect.NavigateBack)
            }
        }
    }

    private fun loadSection(checklistId: String, sectionId: String) {
        this.checklistId = checklistId
        this.sectionId = sectionId
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(500) // Simulation
            
            val mockSection = ChecklistSection(sectionId, checklistId, "Packing List", 35, 18)
            
            allItems = listOf(
                ChecklistItem("i1", sectionId, "T-Shirts", true, "Clothing"),
                ChecklistItem("i2", sectionId, "Jeans", true, "Clothing"),
                ChecklistItem("i3", sectionId, "Jacket", false, "Clothing"),
                ChecklistItem("i4", sectionId, "Swimwear", false, "Clothing"),
                ChecklistItem("i5", sectionId, "Phone Charger", true, "Electronics", isEssential = true),
                ChecklistItem("i6", sectionId, "Power Bank", false, "Electronics", isEssential = true),
                ChecklistItem("i7", sectionId, "Camera", false, "Electronics"),
                ChecklistItem("i8", sectionId, "Travel Adapter", false, "Electronics", isEssential = true)
            )
            
            setState { 
                copy(
                    isLoading = false, 
                    section = mockSection,
                    items = allItems,
                    filteredItems = allItems
                ) 
            }
        }
    }

    private fun toggleItem(itemId: String) {
        allItems = allItems.map {
            if (it.id == itemId) it.copy(isCompleted = !it.isCompleted) else it
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

    private fun filterItems() {
        val query = uiState.value.searchQuery
        val filtered = allItems.filter { 
            it.name.contains(query, ignoreCase = true) 
        }
        setState { copy(filteredItems = filtered) }
    }
}
