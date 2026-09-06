package com.android.trippoint.documents.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointChip
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import com.android.trippoint.documents.domain.model.DocumentType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchFilterRoute(
    viewModel: SearchFilterViewModel,
    onNavigateBack: () -> Unit,
    onFiltersApplied: (SearchFilterContract.State) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SearchFilterContract.Effect.NavigateBack -> onNavigateBack()
                is SearchFilterContract.Effect.FiltersApplied -> onFiltersApplied(effect.state)
            }
        }
    }

    SearchFilterScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun SearchFilterScreen(
    uiState: SearchFilterContract.State,
    onIntent: (SearchFilterContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.documents_search_filter_title),
                onNavClick = { onIntent(SearchFilterContract.Intent.BackClicked) },
                actions = {
                    TextButton(onClick = { onIntent(SearchFilterContract.Intent.ResetFilters) }) {
                        Text(
                            text = stringResource(id = designR.string.documents_reset_filters),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            TripPointTextField(
                value = uiState.searchQuery,
                onValueChange = { onIntent(SearchFilterContract.Intent.SearchQueryChanged(it)) },
                label = "",
                placeholder = stringResource(id = designR.string.documents_search_placeholder),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            FilterSection(
                title = stringResource(id = designR.string.documents_filter_category),
                items = DocumentType.values().map { it.name.replace("_", " & ") },
                selectedIndex = uiState.selectedCategory?.ordinal ?: -1,
                onItemSelected = { 
                    onIntent(SearchFilterContract.Intent.CategorySelected(DocumentType.values()[it])) 
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            FilterSection(
                title = stringResource(id = designR.string.documents_filter_expiry),
                items = listOf(
                    stringResource(id = designR.string.documents_filter_all),
                    stringResource(id = designR.string.documents_filter_this_month),
                    stringResource(id = designR.string.documents_filter_this_year)
                ),
                selectedIndex = when(uiState.selectedExpiryPeriod) {
                    "All" -> 0
                    "This Month" -> 1
                    "This Year" -> 2
                    else -> 0
                },
                onItemSelected = { index ->
                    val period = when(index) {
                        0 -> "All"
                        1 -> "This Month"
                        2 -> "This Year"
                        else -> "All"
                    }
                    onIntent(SearchFilterContract.Intent.ExpirySelected(period))
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            TripPointTextField(
                value = uiState.issuedBy,
                onValueChange = { onIntent(SearchFilterContract.Intent.IssuerChanged(it)) },
                label = stringResource(id = designR.string.documents_filter_issued_by),
                placeholder = "e.g. Govt. of India"
            )

            Spacer(modifier = Modifier.height(48.dp))

            TripPointButton(
                text = stringResource(id = designR.string.documents_apply_filters),
                onClick = { onIntent(SearchFilterContract.Intent.ApplyFilters) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    title: String,
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEachIndexed { index, item ->
                TripPointChip(
                    text = item,
                    isSelected = index == selectedIndex,
                    onClick = { onItemSelected(index) }
                )
            }
        }
    }
}
