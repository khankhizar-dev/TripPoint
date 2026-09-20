package com.android.trippoint.notification.channels

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.SettingsListItem
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.notification.domain.model.NotificationChannel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChannelsRoute(
    viewModel: ChannelsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ChannelsContract.Effect.NavigateBack -> onNavigateBack()
                is ChannelsContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    ChannelsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ChannelsScreen(
    uiState: ChannelsContract.State,
    onIntent: (ChannelsContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "Channels",
                onNavClick = { onIntent(ChannelsContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                uiState.channels.forEach { channel ->
                    ChannelItem(channel, onIntent)
                }
            }
        }
    }
}

@Composable
private fun ChannelItem(
    channel: NotificationChannel,
    onIntent: (ChannelsContract.Intent) -> Unit
) {
    val icon = when (channel.name) {
        "Push Notifications" -> Icons.Outlined.NotificationsActive
        "Email" -> Icons.Outlined.Email
        "In-app Messages" -> Icons.AutoMirrored.Outlined.Message
        "SMS (Optional)" -> Icons.Outlined.Sms
        "Digest Settings" -> Icons.Outlined.Schedule
        else -> Icons.Outlined.NotificationsActive
    }

    SettingsListItem(
        title = channel.name,
        subtitle = channel.description,
        icon = icon,
        trailingContent = {
            Switch(
                checked = channel.isEnabled,
                onCheckedChange = { onIntent(ChannelsContract.Intent.ToggleChannel(channel.id, it)) }
            )
        }
    )
}
