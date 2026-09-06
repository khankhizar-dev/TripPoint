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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.CategoryCard
import com.android.trippoint.core.designsystem.components.DocumentCard
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointTabs
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.theme.TripPointIcons
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DocumentListRoute(
    viewModel: DocumentListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(DocumentListContract.Intent.LoadDocuments)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                DocumentListContract.Effect.NavigateBack -> onNavigateBack()
                is DocumentListContract.Effect.NavigateToDetails -> onNavigateToDetails(effect.id)
                DocumentListContract.Effect.NavigateToCategories -> onNavigateToCategories()
                DocumentListContract.Effect.NavigateToAddDocument -> onNavigateToAdd()
            }
        }
    }

    DocumentListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateToCategories = onNavigateToCategories
    )
}

@Composable
fun DocumentListScreen(
    uiState: DocumentListContract.State,
    onIntent: (DocumentListContract.Intent) -> Unit,
    onNavigateToCategories: () -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.documents_title),
                onNavClick = { onIntent(DocumentListContract.Intent.BackClicked) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(DocumentListContract.Intent.AddDocumentClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DocumentSearchAndTabs(uiState, onIntent)
            
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TripPointAlert(message = uiState.error!!, variant = AlertVariant.Error)
                }
            } else {
                DocumentListContent(uiState, onIntent, onNavigateToCategories)
            }
        }
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
        if (uiState.selectedTab <= 1 && uiState.recentDocuments.isNotEmpty()) {
            item {
                RecentDocumentsHeader(onViewAll = onNavigateToCategories)
            }
            items(uiState.recentDocuments.take(3)) { document ->
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    DocumentCard(
                        title = document.title,
                        date = document.createdAt,
                        size = document.fileSize,
                        isFavorite = document.isFavorite,
                        onClick = { onIntent(DocumentListContract.Intent.DocumentClicked(document.id)) },
                        onFavoriteClick = { onIntent(DocumentListContract.Intent.ToggleFavorite(document.id)) }
                    )
                }
            }
        }
        
        if (uiState.selectedTab >= 2) {
            items(uiState.documents) { document ->
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    DocumentCard(
                        title = document.title,
                        date = document.createdAt,
                        size = document.fileSize,
                        isFavorite = document.isFavorite,
                        onClick = { onIntent(DocumentListContract.Intent.DocumentClicked(document.id)) },
                        onFavoriteClick = { onIntent(DocumentListContract.Intent.ToggleFavorite(document.id)) }
                    )
                }
            }
        }

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
        Triple("Passports", TripPointIcons.Docs, "2"),
        Triple("Tickets", TripPointIcons.Flight, "4"),
        Triple("IDs", TripPointIcons.Profile, "1"),
        Triple("Hotels", TripPointIcons.Hotel, "3")
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
