package com.android.trippoint.notification.detail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.notification.domain.model.NotificationDetail
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationDetailRoute(
    notificationId: String,
    onNavigateBack: () -> Unit,
    onNavigateToTrip: (String) -> Unit,
    viewModel: NotificationDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(notificationId) {
        viewModel.onIntent(NotificationDetailContract.Intent.LoadDetail(notificationId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                NotificationDetailContract.Effect.NavigateBack -> onNavigateBack()
                is NotificationDetailContract.Effect.NavigateToTrip -> onNavigateToTrip(effect.tripId)
                is NotificationDetailContract.Effect.NavigateToRoute -> { /* Handle general route */ }
            }
        }
    }

    NotificationDetailScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun NotificationDetailScreen(
    uiState: NotificationDetailContract.State,
    onIntent: (NotificationDetailContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "Notification Detail",
                onNavClick = { onIntent(NotificationDetailContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else if (uiState.detail != null) {
            NotificationDetailContent(uiState.detail, onIntent, innerPadding)
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.error ?: "Unknown error")
            }
        }
    }
}

@Composable
private fun NotificationDetailContent(
    detail: NotificationDetail,
    onIntent: (NotificationDetailContract.Intent) -> Unit,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ) {
        if (detail.notification.imageUrl != null) {
            AsyncImage(
                model = detail.notification.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = detail.notification.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = detail.notification.createdAt, // Changed from timestamp
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = detail.detailedMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (detail.keyDetails.isNotEmpty()) {
                KeyDetailsCard(detail.keyDetails)
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (detail.actionText != null) {
                TripPointButton(
                    text = detail.actionText,
                    onClick = { onIntent(NotificationDetailContract.Intent.ActionClicked) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { onIntent(NotificationDetailContract.Intent.MarkAsRead) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Mark as Read", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun KeyDetailsCard(details: List<com.android.trippoint.notification.domain.model.NotificationKeyDetail>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            details.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (index < details.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}
