package com.android.trippoint.notification.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.notification.domain.model.Notification
import com.android.trippoint.notification.domain.model.NotificationType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HistoryRoute(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                HistoryContract.Effect.NavigateBack -> onNavigateBack()
                is HistoryContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    HistoryScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun HistoryScreen(
    uiState: HistoryContract.State,
    onIntent: (HistoryContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "History",
                onNavClick = { onIntent(HistoryContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TripPointTextField(
                value = uiState.searchQuery,
                onValueChange = { onIntent(HistoryContract.Intent.LoadHistory(it)) },
                label = "",
                placeholder = "Search history...",
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.padding(24.dp)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    uiState.logs.forEach { log ->
                        item {
                            Text(
                                text = log.dateLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                        items(log.notifications) { notification ->
                            HistoryItem(notification)
                        }
                    }
                }
                
                TextButton(
                    onClick = { onIntent(HistoryContract.Intent.ClearHistory) },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    Text(text = "Clear All", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(notification: Notification) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HistoryIcon(type = notification.type)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (notification.isRead) "Read" else "Unread",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = " • ${notification.createdAt}", // Fixed reference
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HistoryIcon(type: NotificationType) {
    val (icon, color) = when (type) {
        NotificationType.TRIP_INVITATION, NotificationType.TRIP_UPDATED -> 
            Icons.Default.Notifications to Color(0xFF673AB7)
        NotificationType.BOOKING_CONFIRMED, NotificationType.BOOKING_CREATED -> 
            Icons.Default.Hotel to Color(0xFF4CAF50)
        NotificationType.DOCUMENT_EXPIRING, NotificationType.DOCUMENT_UPLOADED -> 
            Icons.Default.Description to Color(0xFFF44336)
        NotificationType.EXPENSE_ADDED, NotificationType.EXPENSE_SETTLEMENT -> 
            Icons.Default.Payments to Color(0xFFFF9800)
        NotificationType.CHAT_MENTION -> 
            Icons.Default.Person to Color(0xFF2196F3)
        NotificationType.INVITATION_ACCEPTED -> 
            Icons.Default.ThumbUp to Color(0xFFFFC107)
        else -> Icons.Default.Notifications to Color(0xFF9E9E9E)
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
    }
}
