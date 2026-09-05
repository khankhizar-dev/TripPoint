package com.android.trippoint.trip.list

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.core.designsystem.components.ErrorView
import com.android.trippoint.core.designsystem.components.TripCard
import com.android.trippoint.core.designsystem.components.TripCardSkeleton
import com.android.trippoint.core.designsystem.components.TripPointBottomNavigation
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.theme.TripPointTheme
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TripListRoute(
    viewModel: TripListViewModel,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToCreate: (String) -> Unit,
    onNavigateToBookings: (String?) -> Unit,
    onNavigateToProfile: () -> Unit,
    userName: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is TripListContract.Effect.NavigateToTripDetails -> onNavigateToDetails(effect.tripId)
                TripListContract.Effect.NavigateToCreateTrip -> onNavigateToCreate("") // Placeholder tripId
                is TripListContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    TripListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateToBookings = { onNavigateToBookings(null) },
        onNavigateToProfile = onNavigateToProfile,
        userName = userName
    )
}

@Composable
fun TripListScreen(
    uiState: TripListContract.State,
    onIntent: (TripListContract.Intent) -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    userName: String? = null
) {
    Scaffold(
        topBar = {
            TripListTopBar(
                userName = userName,
                searchQuery = uiState.searchQuery,
                onIntent = onIntent,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        bottomBar = {
            TripPointBottomNavigation(
                selectedRoute = "workspace",
                onRouteSelected = { route ->
                    when (route) {
                        "bookings" -> onNavigateToBookings()
                        "profile" -> onNavigateToProfile()
                    }
                },
                onAddClick = { onIntent(TripListContract.Intent.CreateTripClicked) }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TripListTabs(selectedTab = uiState.selectedTab, onIntent = onIntent)
            TripListContent(uiState = uiState, onIntent = onIntent)
        }
    }
}

@Composable
private fun TripListTopBar(
    userName: String?,
    searchQuery: String,
    onIntent: (TripListContract.Intent) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        if (userName != null) {
            Text(
                text = "Hello, $userName!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = designR.string.trip_list_title),
                style = MaterialTheme.typography.displayLarge
            )
            IconButton(onClick = onNavigateToProfile) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TripPointTextField(
            value = searchQuery,
            onValueChange = { onIntent(TripListContract.Intent.SearchQueryChanged(it)) },
            label = "",
            placeholder = stringResource(id = designR.string.trip_list_search_placeholder),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TripListFab(
    uiState: TripListContract.State,
    onIntent: (TripListContract.Intent) -> Unit
) {
    if (!uiState.isLoading && !uiState.isOffline && uiState.error == null) {
        FloatingActionButton(
            onClick = { onIntent(TripListContract.Intent.CreateTripClicked) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripListTabs(
    selectedTab: Int,
    onIntent: (TripListContract.Intent) -> Unit
) {
    val tabs = listOf(
        stringResource(id = designR.string.trip_list_tab_upcoming),
        stringResource(id = designR.string.trip_list_tab_in_progress),
        stringResource(id = designR.string.trip_list_tab_completed),
        stringResource(id = designR.string.trip_list_tab_drafts),
        stringResource(id = designR.string.trip_list_tab_archived)
    )

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.background,
        edgePadding = 24.dp,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onIntent(TripListContract.Intent.TabSelected(index)) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selectedTab == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

@Composable
private fun TripListContent(
    uiState: TripListContract.State,
    onIntent: (TripListContract.Intent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(3) { TripCardSkeleton() }
                }
            }
            uiState.isOffline -> {
                ErrorView(
                    title = stringResource(id = designR.string.state_offline_title),
                    description = stringResource(id = designR.string.state_offline_desc),
                    icon = Icons.Default.CloudOff,
                    actionText = stringResource(id = designR.string.state_offline_button),
                    onActionClick = { /* Handle offline mode */ },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.error != null -> {
                ErrorView(
                    title = stringResource(id = designR.string.state_error_title),
                    description = stringResource(id = designR.string.state_error_desc),
                    icon = Icons.Default.Error,
                    actionText = stringResource(id = designR.string.state_error_button),
                    onActionClick = { onIntent(TripListContract.Intent.LoadTrips) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.filteredTrips.isEmpty() -> {
                ErrorView(
                    title = stringResource(id = designR.string.trip_list_empty_title),
                    description = stringResource(id = designR.string.trip_list_empty_desc),
                    icon = Icons.Default.TravelExplore,
                    actionText = stringResource(id = designR.string.trip_list_create_trip),
                    onActionClick = { onIntent(TripListContract.Intent.CreateTripClicked) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.filteredTrips) { trip ->
                        TripCard(
                            trip = trip,
                            onClick = { onIntent(TripListContract.Intent.TripClicked(trip.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripListScreenPreview() {
    TripPointTheme {
        TripListScreen(
            uiState = TripListContract.State(
                filteredTrips = listOf(
                    Trip(
                        id = "1",
                        ownerId = "owner1",
                        title = "Bali, Indonesia",
                        location = "Denpasar, Bali",
                        startDate = "12 May 2025",
                        endDate = "18 May 2025",
                        status = TripStatus.IN_PROGRESS,
                        imageUrl = "",
                        progress = 0.6f
                    )
                )
            ),
            onIntent = {},
            onNavigateToBookings = {},
            onNavigateToProfile = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TripListLoadingPreview() {
    TripPointTheme {
        TripListScreen(
            uiState = TripListContract.State(isLoading = true),
            onIntent = {},
            onNavigateToBookings = {},
            onNavigateToProfile = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TripListOfflinePreview() {
    TripPointTheme {
        TripListScreen(
            uiState = TripListContract.State(isOffline = true),
            onIntent = {},
            onNavigateToBookings = {},
            onNavigateToProfile = {}
        )
    }
}
