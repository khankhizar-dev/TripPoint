package com.android.trippoint.booking.list

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.trippoint.booking.domain.model.BookingStatus
import com.android.trippoint.core.designsystem.components.BookingCard
import com.android.trippoint.core.designsystem.components.IconButtonVariant
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.StatusVariant
import com.android.trippoint.core.designsystem.components.TripPointBottomNavigation
import com.android.trippoint.core.designsystem.components.TripPointEmptyState
import com.android.trippoint.core.designsystem.components.TripPointIconButton
import com.android.trippoint.core.designsystem.components.TripPointTabs
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.theme.TripPointIcons
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BookingListRoute(
    tripId: String,
    viewModel: BookingListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToDetails: (String, String) -> Unit,
    onNavigateToCreate: (String) -> Unit,
    onNavigateToFilter: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(BookingListContract.Intent.LoadBookings(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                BookingListContract.Effect.NavigateBack -> onNavigateBack()
                is BookingListContract.Effect.NavigateToBookingDetails -> onNavigateToDetails(
                    effect.tripId, 
                    effect.bookingId
                )
                is BookingListContract.Effect.NavigateToCreateBooking -> onNavigateToCreate(effect.tripId)
            }
        }
    }

    BookingListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onFilterClick = { onNavigateToFilter(tripId) },
        onNavigateToHome = onNavigateToHome,
        onNavigateToProfile = onNavigateToProfile
    )
}

@Composable
fun BookingListScreen(
    uiState: BookingListContract.State,
    onIntent: (BookingListContract.Intent) -> Unit,
    onFilterClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            BookingListTopBar(onIntent, onFilterClick)
        },
        bottomBar = {
            BookingListBottomBar(onIntent, onNavigateToHome, onNavigateToProfile)
        }
    ) { innerPadding ->
        BookingListContent(uiState, onIntent, innerPadding)
    }
}

@Composable
private fun BookingListTopBar(
    onIntent: (BookingListContract.Intent) -> Unit,
    onFilterClick: () -> Unit
) {
    TripPointTopAppBar(
        title = stringResource(id = designR.string.bookings_title),
        onNavClick = { onIntent(BookingListContract.Intent.BackClicked) },
        actions = {
            TripPointIconButton(
                icon = TripPointIcons.Filter,
                onClick = onFilterClick,
                variant = IconButtonVariant.Standard
            )
        }
    )
}

@Composable
private fun BookingListBottomBar(
    onIntent: (BookingListContract.Intent) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    TripPointBottomNavigation(
        selectedRoute = "bookings",
        onRouteSelected = { route ->
            when (route) {
                "home" -> onNavigateToHome()
                "workspace" -> onNavigateToHome()
                "profile" -> onNavigateToProfile()
            }
        },
        onAddClick = { onIntent(BookingListContract.Intent.AddBookingClicked) }
    )
}

@Composable
private fun BookingListContent(
    uiState: BookingListContract.State,
    onIntent: (BookingListContract.Intent) -> Unit,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        BookingListSearchAndTabs(uiState, onIntent)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        BookingListItems(uiState, onIntent)
    }
}

@Composable
private fun BookingListSearchAndTabs(
    uiState: BookingListContract.State,
    onIntent: (BookingListContract.Intent) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        TripPointTextField(
            value = uiState.searchQuery,
            onValueChange = { onIntent(BookingListContract.Intent.SearchQueryChanged(it)) },
            label = "",
            placeholder = stringResource(id = designR.string.bookings_search_placeholder),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TripPointTabs(
            tabs = listOf(
                stringResource(id = designR.string.bookings_tab_upcoming),
                stringResource(id = designR.string.bookings_tab_past),
                stringResource(id = designR.string.bookings_tab_cancelled)
            ),
            selectedTabIndex = uiState.selectedTab,
            onTabSelected = { onIntent(BookingListContract.Intent.TabSelected(it)) }
        )
    }
}

@Composable
private fun BookingListItems(
    uiState: BookingListContract.State,
    onIntent: (BookingListContract.Intent) -> Unit
) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
    } else if (uiState.filteredBookings.isEmpty()) {
        TripPointEmptyState(
            title = stringResource(id = designR.string.bookings_empty_title),
            subtitle = stringResource(id = designR.string.bookings_empty_desc),
            imageResId = designR.drawable.illustration_empty_trip,
            actionText = stringResource(id = designR.string.bookings_empty_action),
            onActionClick = { /* Handle action */ }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        ) {
            items(uiState.filteredBookings) { booking ->
                BookingCard(
                    title = booking.title,
                    startAt = booking.startAt,
                    status = booking.status.name,
                    statusVariant = when (booking.status) {
                        BookingStatus.CONFIRMED -> StatusVariant.Confirmed
                        BookingStatus.PENDING -> StatusVariant.Pending
                        BookingStatus.CANCELLED -> StatusVariant.Cancelled
                        BookingStatus.REFUNDED -> StatusVariant.Refunded
                        else -> StatusVariant.Draft
                    },
                    location = booking.location,
                    amount = if (booking.amount != null) {
                        "${booking.currency ?: "$"} ${booking.amount}"
                    } else null,
                    providerLogoUrl = booking.providerLogoUrl,
                    onClick = { 
                        val currentTripId = if (booking.tripId.isNotBlank()) {
                            booking.tripId
                        } else uiState.tripId
                        if (currentTripId.isNotBlank()) {
                            onIntent(
                                BookingListContract.Intent.BookingClicked(
                                    currentTripId, 
                                    booking.id
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}
