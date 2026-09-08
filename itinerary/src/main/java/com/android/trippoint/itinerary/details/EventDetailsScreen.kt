package com.android.trippoint.itinerary.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.itinerary.domain.model.EventType
import com.android.trippoint.itinerary.domain.model.TimelineEvent
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EventDetailsRoute(
    tripId: String,
    dayId: String,
    activityId: String,
    viewModel: EventDetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, dayId, activityId) {
        viewModel.onIntent(EventDetailsContract.Intent.LoadEventDetails(tripId, dayId, activityId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                EventDetailsContract.Effect.NavigateBack -> onNavigateBack()
                is EventDetailsContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    EventDetailsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    uiState: EventDetailsContract.State,
    onIntent: (EventDetailsContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = designR.string.event_details_title)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(EventDetailsContract.Intent.BackClicked) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            EventDetailsBottomBar(onIntent)
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else {
            uiState.event?.let { event ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    if (event.type == EventType.FLIGHT) {
                        FlightBanner(event)
                    } else {
                        GenericBanner(event)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    DescriptionSection(event)
                    
                    if (event.location?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(24.dp))
                        LocationSection(event)
                    }

                    if (event.type == EventType.FLIGHT) {
                        Spacer(modifier = Modifier.height(24.dp))
                        PassengerSection()
                        Spacer(modifier = Modifier.height(24.dp))
                        PnrSection()
                    }
                }
            }
        }
    }
}

@Composable
private fun GenericBanner(event: TimelineEvent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = event.type.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                imageVector = when(event.type) {
                    EventType.ACTIVITY -> Icons.Default.Assignment
                    EventType.CHECK_IN -> Icons.Default.Info
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(id = designR.string.event_details_starts_at, event.startTime),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (event.endTime != null) {
            Text(
                text = stringResource(id = designR.string.event_details_ends_at, event.endTime),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun FlightBanner(event: TimelineEvent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE0F2F1)) // Light teal banner
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = designR.string.event_details_flight_activity),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.FlightTakeoff,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF00695C)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = event.startTime, 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = event.location ?: stringResource(id = designR.string.event_details_origin), 
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Color.Gray.copy(alpha = 0.3f))
                    .align(Alignment.CenterVertically)
            )
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = event.endTime ?: "", 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = designR.string.event_details_destination), 
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun DescriptionSection(event: TimelineEvent) {
    Column {
        Text(
            text = stringResource(id = designR.string.event_details_description),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = event.description ?: stringResource(id = designR.string.event_details_no_description),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun LocationSection(event: TimelineEvent) {
    Column {
        Text(
            text = stringResource(id = designR.string.event_details_location),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = event.location ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun PassengerSection() {
    Column {
        Text(
            text = stringResource(id = designR.string.event_details_passengers),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = designR.string.event_details_primary_traveler), 
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun PnrSection() {
    Column {
        Text(
            text = stringResource(id = designR.string.event_details_pnr),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = designR.string.event_details_fetching), 
            style = MaterialTheme.typography.headlineMedium, 
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            InfoItem(
                label = stringResource(id = designR.string.event_details_terminal), 
                value = "-", 
                modifier = Modifier.weight(1f)
            )
            InfoItem(
                label = stringResource(id = designR.string.event_details_gate), 
                value = "-", 
                modifier = Modifier.weight(1f)
            )
            InfoItem(
                label = stringResource(id = designR.string.event_details_seat), 
                value = "-", 
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelSmall, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EventDetailsBottomBar(onIntent: (EventDetailsContract.Intent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = Icons.Default.Edit, 
            label = stringResource(id = designR.string.event_details_edit), 
            onClick = { onIntent(EventDetailsContract.Intent.EditClicked) }
        )
        ActionButton(
            icon = Icons.Default.Share, 
            label = stringResource(id = designR.string.event_details_share), 
            onClick = { onIntent(EventDetailsContract.Intent.ShareClicked) }
        )
        ActionButton(
            icon = Icons.Default.Delete, 
            label = stringResource(id = designR.string.event_details_delete), 
            onClick = { onIntent(EventDetailsContract.Intent.DeleteClicked) }
        )
    }
}

@Composable
private fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}
