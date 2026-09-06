package com.android.trippoint.documents.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.CategoryCard
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import com.android.trippoint.documents.domain.model.DocumentType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DocumentCategoriesRoute(
    viewModel: DocumentCategoriesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDocumentsByType: (DocumentType) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(DocumentCategoriesContract.Intent.LoadCategories)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                DocumentCategoriesContract.Effect.NavigateBack -> onNavigateBack()
                is DocumentCategoriesContract.Effect.NavigateToDocumentsByType -> {
                    onNavigateToDocumentsByType(effect.type)
                }
            }
        }
    }

    DocumentCategoriesScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun DocumentCategoriesScreen(
    uiState: DocumentCategoriesContract.State,
    onIntent: (DocumentCategoriesContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.documents_categories_title),
                onNavClick = { onIntent(DocumentCategoriesContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                TripPointAlert(message = uiState.error!!, variant = AlertVariant.Error)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.categories) { category ->
                    CategoryCard(
                        title = category.name,
                        count = stringResource(id = designR.string.documents_count_suffix, category.count),
                        icon = category.icon,
                        onClick = { onIntent(DocumentCategoriesContract.Intent.CategoryClicked(category.type)) }
                    )
                }
            }
        }
    }
}
