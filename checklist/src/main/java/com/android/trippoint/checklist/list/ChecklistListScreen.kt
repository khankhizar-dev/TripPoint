package com.android.trippoint.checklist.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointBudgetCard
import com.android.trippoint.core.designsystem.components.TripPointTabs
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChecklistListRoute(
    viewModel: ChecklistListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(ChecklistListContract.Intent.LoadChecklists)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ChecklistListContract.Effect.NavigateBack -> onNavigateBack()
                is ChecklistListContract.Effect.NavigateToDetails -> onNavigateToDetails(effect.id)
                ChecklistListContract.Effect.NavigateToCreate -> onNavigateToCreate()
                is ChecklistListContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    ChecklistListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ChecklistListScreen(
    uiState: ChecklistListContract.State,
    onIntent: (ChecklistListContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.checklist_list_title),
                onNavClick = { onIntent(ChecklistListContract.Intent.BackClicked) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(ChecklistListContract.Intent.CreateChecklistClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = designR.string.checklist_new_button))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ChecklistSearchAndTabs(uiState, onIntent)
            
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            } else {
                ChecklistLazyList(uiState, onIntent)
            }
        }
    }
}

@Composable
private fun ChecklistSearchAndTabs(
    uiState: ChecklistListContract.State,
    onIntent: (ChecklistListContract.Intent) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        TripPointTextField(
            value = uiState.searchQuery,
            onValueChange = { onIntent(ChecklistListContract.Intent.SearchQueryChanged(it)) },
            label = "",
            placeholder = stringResource(id = designR.string.checklist_search_placeholder),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TripPointTabs(
            tabs = listOf(
                stringResource(id = designR.string.checklist_tab_all),
                stringResource(id = designR.string.checklist_tab_active),
                stringResource(id = designR.string.checklist_tab_completed)
            ),
            selectedTabIndex = uiState.selectedTab,
            onTabSelected = { onIntent(ChecklistListContract.Intent.TabSelected(it)) }
        )
    }
}

@Composable
private fun ChecklistLazyList(
    uiState: ChecklistListContract.State,
    onIntent: (ChecklistListContract.Intent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(uiState.filteredChecklists) { checklist ->
            ChecklistCard(checklist) {
                onIntent(ChecklistListContract.Intent.ChecklistClicked(checklist.id))
            }
        }
    }
}

@Composable
private fun ChecklistCard(
    checklist: Checklist,
    onClick: () -> Unit
) {
    TripPointBudgetCard(
        title = checklist.title,
        totalBudget = checklist.dateRange,
        spentSoFar = "${checklist.completedItems}/${checklist.totalItems}",
        progress = checklist.progress,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    )
}
