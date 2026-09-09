package com.android.trippoint.checklist.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.checklist.domain.model.ChecklistItem
import com.android.trippoint.core.designsystem.components.ErrorView
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointLinearProgress
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChecklistItemsRoute(
    tripId: String,
    checklistId: String,
    sectionId: String,
    viewModel: ChecklistItemsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddItem: (String, String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, checklistId, sectionId) {
        viewModel.onIntent(ChecklistItemsContract.Intent.LoadSection(tripId, checklistId, sectionId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ChecklistItemsContract.Effect.NavigateBack -> onNavigateBack()
                is ChecklistItemsContract.Effect.NavigateToAddItem -> {
                    onNavigateToAddItem(effect.tripId, effect.checklistId, effect.sectionId)
                }
                is ChecklistItemsContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    ChecklistItemsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ChecklistItemsScreen(
    uiState: ChecklistItemsContract.State,
    onIntent: (ChecklistItemsContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = uiState.section?.title ?: "Items",
                onNavClick = { onIntent(ChecklistItemsContract.Intent.BackClicked) }
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading && uiState.error == null) {
                FloatingActionButton(
                    onClick = { onIntent(ChecklistItemsContract.Intent.AddItemClicked) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            uiState.error != null -> {
                ErrorView(
                    title = stringResource(id = designR.string.checklist_error_title),
                    description = uiState.error,
                    icon = Icons.Default.Error,
                    actionText = stringResource(id = designR.string.core_designsystem_retry),
                    onActionClick = { onIntent(ChecklistItemsContract.Intent.RetryClicked) }
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    SectionProgressHeader(uiState)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TripPointTextField(
                        value = uiState.searchQuery,
                        onValueChange = { onIntent(ChecklistItemsContract.Intent.SearchQueryChanged(it)) },
                        label = "",
                        placeholder = "Search items",
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    ChecklistItemsList(uiState, onIntent)
                }
            }
        }
    }
}

@Composable
private fun SectionProgressHeader(uiState: ChecklistItemsContract.State) {
    val section = uiState.section ?: return
    val progress = if (section.totalItems > 0) section.completedItems.toFloat() / section.totalItems else 0f
    
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val countText = stringResource(
                id = designR.string.checklist_items_checked_count, 
                section.completedItems, 
                section.totalItems
            )
            Text(
                text = countText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TripPointLinearProgress(progress = progress)
    }
}

@Composable
private fun ChecklistItemsList(
    uiState: ChecklistItemsContract.State,
    onIntent: (ChecklistItemsContract.Intent) -> Unit
) {
    val groupedItems = uiState.filteredItems.groupBy { it.category }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        groupedItems.forEach { (category, items) ->
            item {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
            
            items(items) { item ->
                ChecklistItemRow(item) {
                    onIntent(ChecklistItemsContract.Intent.ItemToggled(item.id))
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ChecklistItemRow(
    item: ChecklistItem,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        
        Spacer(modifier = Modifier.size(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            val textColor = if (item.isCompleted) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontWeight = if (item.isEssential) FontWeight.Bold else FontWeight.Normal
            )
            if (item.notes != null) {
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
