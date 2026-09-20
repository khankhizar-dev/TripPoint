package com.android.trippoint.notification.center

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointTabs
import com.android.trippoint.notification.domain.model.Notification
import com.android.trippoint.notification.domain.model.NotificationType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationCenterRoute(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: NotificationCenterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                NotificationCenterContract.Effect.NavigateBack -> onNavigateBack()
                is NotificationCenterContract.Effect.NavigateToDetail -> onNavigateToDetail(effect.notificationId)
            }
        }
    }

    NotificationCenterScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateToHistory = onNavigateToHistory
    )
}

@Composable
fun NotificationCenterScreen(
    uiState: NotificationCenterContract.State,
    onIntent: (NotificationCenterContract.Intent) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onIntent(NotificationCenterContract.Intent.BackClicked) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onNavigateToHistory) {
                    Icon(imageVector = Icons.Default.History, contentDescription = "History")
                }
                IconButton(onClick = { /* Search */ }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TripPointTabs(
                tabs = listOf("All", "Unread (${uiState.unreadCount})", "Mentions"),
                selectedTabIndex = uiState.selectedTab,
                onTabSelected = { onIntent(NotificationCenterContract.Intent.TabSelected(it)) },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.filteredNotifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = {
                                onIntent(NotificationCenterContract.Intent.NotificationClicked(notification.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        NotificationIcon(type = notification.type)
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium,
                    color = if (!notification.isRead) MaterialTheme.colorScheme.onBackground 
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = notification.createdAt, // Changed from timestamp
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
        
        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(top = 6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun NotificationIcon(type: NotificationType) {
    val (icon, color) = when (type) {
        NotificationType.TRIP_INVITATION, NotificationType.TRIP_UPDATED -> 
            Icons.Default.Notifications to Color(0xFF673AB7)
        NotificationType.BOOKING_CONFIRMED, NotificationType.BOOKING_CREATED -> 
            Icons.Default.Hotel to Color(0xFF4CAF50)
        NotificationType.DOCUMENT_EXPIRING -> 
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
            .size(48.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}
