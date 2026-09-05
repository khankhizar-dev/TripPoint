package com.android.trippoint.booking.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.booking.domain.model.BookingStatus
import com.android.trippoint.booking.domain.model.BookingType
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.AvatarSize
import com.android.trippoint.core.designsystem.components.ButtonSize
import com.android.trippoint.core.designsystem.components.ButtonVariant
import com.android.trippoint.core.designsystem.components.IconButtonVariant
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.StatusVariant
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointAvatar
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointIconButton
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointStatRow
import com.android.trippoint.core.designsystem.components.TripPointStatusBadge
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.theme.TripPointIcons
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BookingDetailsRoute(
    tripId: String,
    bookingId: String,
    viewModel: BookingDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToItinerary: (String, String) -> Unit,
    onNavigateToManage: (String, String) -> Unit,
    onNavigateToAddTraveller: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, bookingId) {
        viewModel.onIntent(BookingDetailsContract.Intent.LoadBookingDetails(tripId, bookingId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                BookingDetailsContract.Effect.NavigateBack -> onNavigateBack()
                is BookingDetailsContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    BookingDetailsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateToItinerary = { onNavigateToItinerary(tripId, bookingId) },
        onNavigateToManage = { onNavigateToManage(tripId, bookingId) },
        onNavigateToAddTraveller = { onNavigateToAddTraveller(tripId, bookingId) }
    )
}

@Composable
fun BookingDetailsScreen(
    uiState: BookingDetailsContract.State,
    onIntent: (BookingDetailsContract.Intent) -> Unit,
    onNavigateToItinerary: () -> Unit,
    onNavigateToManage: () -> Unit,
    onNavigateToAddTraveller: () -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.bookings_details_title),
                onNavClick = { onIntent(BookingDetailsContract.Intent.BackClicked) }
            )
        },
        bottomBar = {
            BookingManagementBar(onIntent)
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
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
                    BookingHeader(booking)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    BookingActionButtons(
                        onItineraryClick = onNavigateToItinerary,
                        onManageClick = onNavigateToManage
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    TravellersSection(uiState, onNavigateToAddTraveller)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TripPointInfoCard(title = stringResource(id = designR.string.bookings_details_booking_status)) {
                        TripPointStatRow(
                            label = stringResource(id = designR.string.bookings_details_reference), 
                            value = booking.bookingReference ?: "N/A"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TripPointStatRow(
                            label = stringResource(id = designR.string.bookings_details_date_time), 
                            value = booking.startAt
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TripPointInfoCard(
                        title = stringResource(id = designR.string.bookings_details_provider)
                    ) {
                        Text(
                            text = booking.provider ?: stringResource(
                                id = designR.string.bookings_details_unknown_provider
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (booking.notes != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = booking.notes, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TripPointInfoCard(title = stringResource(id = designR.string.bookings_details_total_amount)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${booking.currency ?: "$"} ${booking.amount ?: "0.00"}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = designR.string.bookings_details_view_price_details),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun BookingHeader(booking: com.android.trippoint.booking.domain.model.Booking) {
    Column {
        BookingHeaderTopRow(booking)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        BookingDetailsCard(booking)
    }
}

@Composable
private fun BookingHeaderTopRow(booking: com.android.trippoint.booking.domain.model.Booking) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = booking.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        TripPointStatusBadge(
            text = booking.status.name,
            variant = when (booking.status) {
                BookingStatus.CONFIRMED -> StatusVariant.Confirmed
                BookingStatus.PENDING -> StatusVariant.Pending
                BookingStatus.CANCELLED -> StatusVariant.Cancelled
                else -> StatusVariant.Draft
            }
        )
    }
}

@Composable
private fun BookingDetailsCard(booking: com.android.trippoint.booking.domain.model.Booking) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .padding(24.dp)
    ) {
        Column {
            BookingDetailsCardHeader(booking)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            BookingDetailsCardLocationRow(booking)
        }
    }
}

@Composable
private fun BookingDetailsCardHeader(booking: com.android.trippoint.booking.domain.model.Booking) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = booking.provider ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            val referenceText = if (booking.type == BookingType.FLIGHT) {
                "PNR ${booking.bookingReference} • " +
                    stringResource(id = designR.string.bookings_details_view_eticket)
            } else {
                "Ref: ${booking.bookingReference}"
            }
            Text(
                text = referenceText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = when (booking.type) {
                BookingType.FLIGHT -> TripPointIcons.Flight
                BookingType.HOTEL -> TripPointIcons.Hotel
                else -> TripPointIcons.Bookings
            },
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun BookingDetailsCardLocationRow(booking: com.android.trippoint.booking.domain.model.Booking) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = booking.startLocationCode ?: "DEL",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = booking.startAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = TripPointIcons.Flight,
            contentDescription = null,
            modifier = Modifier.size(24.dp).rotate(90f),
            tint = MaterialTheme.colorScheme.outline
        )
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = booking.endLocationCode ?: "DPS",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = booking.endAt ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BookingActionButtons(
    onItineraryClick: () -> Unit,
    onManageClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TripPointButton(
            text = stringResource(id = designR.string.trip_overview_tab_timeline),
            onClick = onItineraryClick,
            variant = ButtonVariant.Primary,
            size = ButtonSize.Medium,
            modifier = Modifier.weight(1f)
        )
        TripPointButton(
            text = stringResource(id = designR.string.bookings_manage_title),
            onClick = onManageClick,
            variant = ButtonVariant.Secondary,
            size = ButtonSize.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TravellersSection(uiState: BookingDetailsContract.State, onAddClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = designR.string.bookings_details_travellers),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add", style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((-12).dp)
        ) {
            if (uiState.travellers.isEmpty()) {
                repeat(3) { index ->
                    TripPointAvatar(
                        imageUrl = null,
                        initials = if (index == 0) "AK" else if (index == 1) "JD" else "+2",
                        size = AvatarSize.M
                    )
                }
            } else {
                uiState.travellers.take(3).forEach { traveller ->
                    TripPointAvatar(
                        imageUrl = null,
                        initials = "${traveller.firstName.take(1)}${traveller.lastName.take(1)}",
                        size = AvatarSize.M
                    )
                }
                if (uiState.travellers.size > 3) {
                    TripPointAvatar(
                        imageUrl = null,
                        initials = "+${uiState.travellers.size - 3}",
                        size = AvatarSize.M
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingManagementBar(onIntent: (BookingDetailsContract.Intent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TripPointIconButton(
            icon = Icons.Default.Edit,
            onClick = { onIntent(BookingDetailsContract.Intent.EditClicked) }
        )
        TripPointIconButton(
            icon = Icons.Default.Share,
            onClick = { onIntent(BookingDetailsContract.Intent.ShareClicked) }
        )
        TripPointIconButton(
            icon = Icons.Default.Delete,
            onClick = { onIntent(BookingDetailsContract.Intent.DeleteClicked) },
            variant = IconButtonVariant.Destructive
        )
    }
}
