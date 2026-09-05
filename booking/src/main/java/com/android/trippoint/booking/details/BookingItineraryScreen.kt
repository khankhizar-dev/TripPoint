package com.android.trippoint.booking.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.booking.domain.model.Booking
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BookingItineraryRoute(
    tripId: String,
    bookingId: String,
    viewModel: BookingItineraryViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, bookingId) {
        viewModel.onIntent(BookingItineraryContract.Intent.LoadItinerary(tripId, bookingId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                BookingItineraryContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    BookingItineraryScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun BookingItineraryScreen(
    uiState: BookingItineraryContract.State,
    onIntent: (BookingItineraryContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.trip_overview_tab_timeline),
                onNavClick = { onIntent(BookingItineraryContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else if (uiState.error != null) {
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
            uiState.booking?.let { booking ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ItineraryTimeline(booking)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    ItineraryDetails(booking)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ItineraryTimeline(booking: Booking) {
    Column {
        // Origin
        TimelineNode(
            title = booking.startLocation ?: "",
            subtitle = "${booking.startLocationCode} • ${booking.location}",
            time = booking.startAt,
            isStart = true
        )
        
        // Duration Line
        Box(
            modifier = Modifier
                .padding(start = 11.dp)
                .height(80.dp)
                .width(2.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(start = 24.dp).align(Alignment.CenterStart)) {
                Text(
                    text = booking.duration ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (booking.isNonStop) {
                    Text(
                        text = stringResource(id = designR.string.bookings_itinerary_non_stop),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Destination
        TimelineNode(
            title = booking.endLocation ?: "",
            subtitle = "${booking.endLocationCode} • ${booking.location}",
            time = booking.endAt ?: "",
            isEnd = true
        )
    }
}

@Composable
private fun TimelineNode(
    title: String,
    subtitle: String,
    time: String,
    isStart: Boolean = false,
    isEnd: Boolean = false
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .padding(4.dp)
        ) {
            if (isStart || isEnd) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = time,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ItineraryDetails(booking: Booking) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        TripPointInfoCard(title = stringResource(id = designR.string.bookings_itinerary_baggage)) {
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                BaggageItem(
                    icon = Icons.Default.Work, 
                    label = "${booking.baggageAllowance ?: "0kg"} (${
                        stringResource(id = designR.string.bookings_itinerary_baggage_checkin)
                    })"
                )
                BaggageItem(
                    icon = Icons.Default.ShoppingBag, 
                    label = "${booking.cabinBaggage ?: "0kg"} (${
                        stringResource(id = designR.string.bookings_itinerary_baggage_cabin)
                    })"
                )
            }
        }
        
        TripPointInfoCard(title = stringResource(id = designR.string.bookings_itinerary_aircraft)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AirplanemodeActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = booking.aircraft ?: "N/A",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BaggageItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}
