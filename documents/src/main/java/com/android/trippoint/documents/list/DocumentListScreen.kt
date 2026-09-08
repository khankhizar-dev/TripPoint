package com.android.trippoint.documents.list

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.CategoryCard
import com.android.trippoint.core.designsystem.components.DocumentCard
import com.android.trippoint.core.designsystem.components.ErrorView
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointEmptyState
import com.android.trippoint.core.designsystem.components.TripPointTabs
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.theme.TripPointIcons
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DocumentListRoute(
    tripId: String,
    viewModel: DocumentListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(tripId) {
        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                DocumentListContract.Effect.NavigateBack -> onNavigateBack()
                is DocumentListContract.Effect.NavigateToDetails -> onNavigateToDetails(effect.id)
                DocumentListContract.Effect.NavigateToCategories -> onNavigateToCategories()
                DocumentListContract.Effect.NavigateToAddDocument -> onNavigateToAdd()
                is DocumentListContract.Effect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    DocumentListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateToCategories = onNavigateToCategories,
        onSearchClick = onNavigateToSearch,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun DocumentListScreen(
    uiState: DocumentListContract.State,
    onIntent: (DocumentListContract.Intent) -> Unit,
    onNavigateToCategories: () -> Unit,
    onSearchClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            DocumentListTopBar(uiState, onIntent, onSearchClick)
        },
        floatingActionButton = {
            DocumentListFab(uiState, onIntent)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!uiState.isSelectionMode) {
                DocumentSearchAndTabs(uiState, onIntent)
            }
            
            DocumentListStates(uiState, onIntent, onNavigateToCategories)
        }
    }
}

@Composable
private fun DocumentListTopBar(
    uiState: DocumentListContract.State,
    onIntent: (DocumentListContract.Intent) -> Unit,
    onSearchClick: () -> Unit
) {
    if (uiState.isSelectionMode) {
        SelectionTopBar(
            selectedCount = uiState.selectedDocumentIds.size,
            onClearSelection = { 
                onIntent(DocumentListContract.Intent.ClearSelection) 
            }
        )
    } else {
        TripPointTopAppBar(
            title = stringResource(id = designR.string.documents_title),
            onNavClick = { onIntent(DocumentListContract.Intent.BackClicked) },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                }
            }
        )
    }
}

@Composable
private fun DocumentListFab(
    uiState: DocumentListContract.State,
    onIntent: (DocumentListContract.Intent) -> Unit
) {
    if (!uiState.isSelectionMode) {
        FloatingActionButton(
            onClick = { onIntent(DocumentListContract.Intent.AddDocumentClicked) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}

@Composable
private fun DocumentListStates(
    uiState: DocumentListContract.State,
    onIntent: (DocumentListContract.Intent) -> Unit,
    onNavigateToCategories: () -> Unit
) {
    when {
        uiState.isLoading -> LoadingStateView()
        uiState.isOffline -> OfflineStateView(onRetry = { 
            onIntent(DocumentListContract.Intent.LoadDocuments(uiState.tripId)) 
        })
        uiState.error != null -> ErrorStateView(
            message = uiState.error, 
            onRetry = { onIntent(DocumentListContract.Intent.LoadDocuments(uiState.tripId)) }
        )
        uiState.documents.isEmpty() && uiState.recentDocuments.isEmpty() -> {
            EmptyStateView(onAddClick = { onIntent(DocumentListContract.Intent.AddDocumentClicked) })
        }
        else -> DocumentListContent(uiState, onIntent, onNavigateToCategories)
    }
}

@Composable
private fun DocumentSearchAndTabs(
    uiState: DocumentListContract.State,
    onIntent: (DocumentListContract.Intent) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        TripPointTextField(
            value = uiState.searchQuery,
            onValueChange = { onIntent(DocumentListContract.Intent.SearchQueryChanged(it)) },
            label = "",
            placeholder = stringResource(id = designR.string.documents_search_placeholder),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TripPointTabs(
            tabs = listOf(
                stringResource(id = designR.string.documents_tab_all),
                stringResource(id = designR.string.documents_tab_recent),
                stringResource(id = designR.string.documents_tab_favorites),
                stringResource(id = designR.string.documents_tab_shared)
            ),
            selectedTabIndex = uiState.selectedTab,
            onTabSelected = { onIntent(DocumentListContract.Intent.TabSelected(it)) }
        )
    }
}

@Composable
private fun DocumentListContent(
    uiState: DocumentListContract.State,
    onIntent: (DocumentListContract.Intent) -> Unit,
    onNavigateToCategories: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if ((uiState.selectedTab <= 1 && uiState.recentDocuments.isNotEmpty())) {
            item {
                RecentDocumentsHeader(onViewAll = onNavigateToCategories)
            }
            items(uiState.recentDocuments.take(3)) { document ->
                val isSelected = uiState.selectedDocumentIds.contains(document.id)
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    DocumentCard(
                        title = document.title,
                        date = document.createdAt,
                        size = document.fileSize ?: "",
                        isFavorite = document.isFavorite,
                        isSelected = isSelected,
                        isSelectionMode = uiState.isSelectionMode,
                        onClick = { 
                            if (uiState.isSelectionMode) {
                                onIntent(DocumentListContract.Intent.DocumentLongClicked(document.id))
                            } else {
                                onIntent(DocumentListContract.Intent.DocumentClicked(document.id))
                            }
                        },
                        onLongClick = { 
                            onIntent(DocumentListContract.Intent.DocumentLongClicked(document.id)) 
                        },
                        onFavoriteClick = { 
                            onIntent(DocumentListContract.Intent.ToggleFavorite(document.id)) 
                        }
                    )
                }
            }
        }
        
        if (uiState.selectedTab >= 2 || uiState.recentDocuments.isEmpty()) {
            items(uiState.documents) { document ->
                val isSelected = uiState.selectedDocumentIds.contains(document.id)
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    DocumentCard(
                        title = document.title,
                        date = document.createdAt,
                        size = document.fileSize ?: "",
                        isFavorite = document.isFavorite,
                        isSelected = isSelected,
                        isSelectionMode = uiState.isSelectionMode,
                        onClick = { 
                            if (uiState.isSelectionMode) {
                                onIntent(DocumentListContract.Intent.DocumentLongClicked(document.id))
                            } else {
                                onIntent(DocumentListContract.Intent.DocumentClicked(document.id))
                            }
                        },
                        onLongClick = { 
                            onIntent(DocumentListContract.Intent.DocumentLongClicked(document.id)) 
                        },
                        onFavoriteClick = { 
                            onIntent(DocumentListContract.Intent.ToggleFavorite(document.id)) 
                        }
                    )
                }
            }
        }

        if (!uiState.isSelectionMode) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = designR.string.documents_categories_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                CategoriesGridPreview(onNavigateToCategories)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SelectionTopBar(selectedCount: Int, onClearSelection: () -> Unit) {
    TripPointTopAppBar(
        title = stringResource(id = designR.string.documents_count_suffix, selectedCount),
        onNavClick = onClearSelection,
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Share, contentDescription = null)
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    )
}

@Composable
private fun LoadingStateView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadingIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = designR.string.documents_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyStateView(onAddClick: () -> Unit) {
    TripPointEmptyState(
        title = stringResource(id = designR.string.documents_empty_title),
        subtitle = stringResource(id = designR.string.documents_empty_desc),
        imageResId = designR.drawable.illustration_empty_trip,
        actionText = stringResource(id = designR.string.documents_add_title),
        onActionClick = onAddClick
    )
}

@Composable
private fun OfflineStateView(onRetry: () -> Unit) {
    ErrorView(
        title = stringResource(id = designR.string.documents_offline_title),
        description = stringResource(id = designR.string.documents_offline_desc),
        icon = Icons.Default.CloudOff,
        actionText = stringResource(id = designR.string.core_designsystem_retry),
        onActionClick = onRetry
    )
}

@Composable
private fun ErrorStateView(message: String?, onRetry: () -> Unit) {
    ErrorView(
        title = stringResource(id = designR.string.state_error_title),
        description = message ?: stringResource(id = designR.string.state_error_desc),
        icon = Icons.Default.Error,
        actionText = stringResource(id = designR.string.core_designsystem_retry),
        onActionClick = onRetry
    )
}

@Composable
private fun RecentDocumentsHeader(onViewAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = designR.string.documents_section_recent),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onViewAll) {
            Text(
                text = stringResource(id = designR.string.documents_section_view_all),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CategoriesGridPreview(onNavigateToCategories: () -> Unit) {
    val categories = listOf(
        Triple(stringResource(id = designR.string.documents_type_passport_visa), TripPointIcons.Docs, "2"),
        Triple(stringResource(id = designR.string.documents_type_tickets_boarding), TripPointIcons.Flight, "4"),
        Triple(stringResource(id = designR.string.documents_type_id_proofs), TripPointIcons.Profile, "1"),
        Triple(stringResource(id = designR.string.documents_type_hotel_vouchers), TripPointIcons.Hotel, "3")
    )
    
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CategoryCard(
                title = categories[0].first,
                count = categories[0].third,
                icon = categories[0].second,
                onClick = onNavigateToCategories,
                modifier = Modifier.weight(1f)
            )
            CategoryCard(
                title = categories[1].first,
                count = categories[1].third,
                icon = categories[1].second,
                onClick = onNavigateToCategories,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CategoryCard(
                title = categories[2].first,
                count = categories[2].third,
                icon = categories[2].second,
                onClick = onNavigateToCategories,
                modifier = Modifier.weight(1f)
            )
            CategoryCard(
                title = categories[3].first,
                count = categories[3].third,
                icon = categories[3].second,
                onClick = onNavigateToCategories,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
