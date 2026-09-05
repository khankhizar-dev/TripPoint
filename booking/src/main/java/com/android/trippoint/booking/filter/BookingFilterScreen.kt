package com.android.trippoint.booking.filter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.android.trippoint.core.designsystem.components.TripPointDropdown
import com.android.trippoint.core.designsystem.components.TripPointFilterChipGroup
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BookingFilterRoute(
    viewModel: BookingFilterViewModel,
    onNavigateBack: () -> Unit,
    onFiltersApplied: (BookingFilterContract.State) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                BookingFilterContract.Effect.NavigateBack -> onNavigateBack()
                is BookingFilterContract.Effect.FiltersApplied -> onFiltersApplied(effect.state)
            }
        }
    }

    BookingFilterScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun BookingFilterScreen(
    uiState: BookingFilterContract.State,
    onIntent: (BookingFilterContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.bookings_filter_title),
                onNavClick = { onIntent(BookingFilterContract.Intent.BackClicked) },
                actions = {
                    TextButton(onClick = { onIntent(BookingFilterContract.Intent.ResetClicked) }) {
                        Text(
                            text = stringResource(id = designR.string.bookings_filter_reset_button), 
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp)) {
                TripPointButton(
                    text = stringResource(id = designR.string.bookings_filter_apply_button),
                    onClick = { onIntent(BookingFilterContract.Intent.ApplyClicked) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TripPointTextField(
                value = uiState.searchQuery,
                onValueChange = { onIntent(BookingFilterContract.Intent.SearchQueryChanged(it)) },
                label = "",
                placeholder = stringResource(id = designR.string.bookings_search_placeholder),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            FilterSection(title = stringResource(id = designR.string.bookings_filter_type_label)) {
                TripPointFilterChipGroup(
                    filters = uiState.types,
                    selectedFilter = uiState.selectedType,
                    onFilterSelected = { onIntent(BookingFilterContract.Intent.TypeSelected(it)) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            FilterSection(title = stringResource(id = designR.string.bookings_filter_status_label)) {
                TripPointFilterChipGroup(
                    filters = uiState.statuses,
                    selectedFilter = uiState.selectedStatus,
                    onFilterSelected = { onIntent(BookingFilterContract.Intent.StatusSelected(it)) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TripPointTextField(
                value = uiState.selectedDate,
                onValueChange = { onIntent(BookingFilterContract.Intent.DateSelected(it)) },
                label = stringResource(id = designR.string.bookings_filter_date_label),
                placeholder = stringResource(id = designR.string.bookings_filter_all_dates),
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TripPointDropdown(
                value = uiState.selectedProvider,
                onValueChange = { onIntent(BookingFilterContract.Intent.ProviderSelected(it)) },
                label = stringResource(id = designR.string.bookings_filter_provider_label),
                options = listOf("All", "Air India", "Hilton", "Uber") // These should also come from API eventually
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}
