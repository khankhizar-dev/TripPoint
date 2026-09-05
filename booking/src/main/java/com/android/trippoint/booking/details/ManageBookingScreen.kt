package com.android.trippoint.booking.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.TrendingUp
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
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointListItem
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ManageBookingRoute(
    tripId: String,
    bookingId: String,
    viewModel: ManageBookingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, bookingId) {
        viewModel.onIntent(ManageBookingContract.Intent.LoadBooking(tripId, bookingId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ManageBookingContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    ManageBookingScreen(uiState = uiState, onIntent = viewModel::onIntent)
}

@Composable
fun ManageBookingScreen(
    uiState: ManageBookingContract.State,
    onIntent: (ManageBookingContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.bookings_manage_title),
                onNavClick = { onIntent(ManageBookingContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                TripPointAlert(message = uiState.error, variant = AlertVariant.Error)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                TripPointListItem(
                    title = stringResource(id = designR.string.bookings_manage_change),
                    subtitle = stringResource(id = designR.string.bookings_manage_change_desc),
                    leadingIcon = Icons.Default.Edit,
                    onClick = { onIntent(ManageBookingContract.Intent.ChangeFlightClicked) }
                )
                
                TripPointListItem(
                    title = stringResource(id = designR.string.bookings_manage_cancel),
                    subtitle = stringResource(id = designR.string.bookings_manage_cancel_desc),
                    leadingIcon = Icons.Default.Cancel,
                    onClick = { onIntent(ManageBookingContract.Intent.CancelBookingClicked) }
                )
                
                TripPointListItem(
                    title = stringResource(id = designR.string.bookings_manage_upgrade),
                    subtitle = stringResource(id = designR.string.bookings_manage_upgrade_desc),
                    leadingIcon = Icons.Default.TrendingUp,
                    onClick = { onIntent(ManageBookingContract.Intent.UpgradeClicked) }
                )
                
                TripPointListItem(
                    title = stringResource(id = designR.string.bookings_manage_seats),
                    subtitle = stringResource(id = designR.string.bookings_manage_seats_desc),
                    leadingIcon = Icons.Default.EventSeat,
                    onClick = { onIntent(ManageBookingContract.Intent.SelectSeatsClicked) }
                )
                
                TripPointListItem(
                    title = stringResource(id = designR.string.bookings_manage_baggage),
                    subtitle = stringResource(id = designR.string.bookings_manage_baggage_desc),
                    leadingIcon = Icons.Default.Luggage,
                    onClick = { onIntent(ManageBookingContract.Intent.AddBaggageClicked) }
                )
            }
        }
    }
}
