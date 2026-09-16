package com.android.trippoint.trip.collaboration.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.trip.collaboration.domain.model.ActivityLog
import com.android.trippoint.trip.collaboration.domain.model.ActivityTarget

@Composable
fun ActivityFeedRoute(
    tripId: String,
    viewModel: ActivityFeedViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(ActivityFeedContract.Intent.LoadFeed(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ActivityFeedContract.Effect.NavigateBack -> onNavigateBack()
                is ActivityFeedContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    ActivityFeedScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ActivityFeedScreen(
    uiState: ActivityFeedContract.State,
    onIntent: (ActivityFeedContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "Activity Feed",
                onNavClick = { onIntent(ActivityFeedContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(uiState.activities) { activity ->
                    ActivityItem(activity)
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(activity: ActivityLog) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val icon = when (activity.targetType) {
                ActivityTarget.TASK -> Icons.AutoMirrored.Filled.Assignment
                ActivityTarget.DOCUMENT -> Icons.Default.Description
                ActivityTarget.BOOKING -> Icons.Default.Event
                ActivityTarget.EXPENSE -> Icons.Default.Payments
                ActivityTarget.MEMBER -> Icons.Default.PersonAdd
                ActivityTarget.CHAT -> Icons.AutoMirrored.Filled.Chat
                else -> Icons.Default.Event
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(activity.userName)
                }
                append(" ${activity.action} ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                    append(activity.targetName)
                }
            }
            
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = activity.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
