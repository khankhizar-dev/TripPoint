package com.android.trippoint.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.core.designsystem.components.SettingsListItem

@Composable
fun NotificationsRoute(
    onNavigateBack: () -> Unit
) {
    val viewModel: NotificationsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    NotificationsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun NotificationsScreen(
    uiState: NotificationsContract.State,
    onIntent: (NotificationsContract.Intent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SettingsListItem(
                    title = "Push Notifications",
                    icon = Icons.Outlined.NotificationsActive,
                    trailingContent = {
                        Switch(
                            checked = uiState.isPushEnabled,
                            onCheckedChange = { onIntent(NotificationsContract.Intent.PushToggled(it)) }
                        )
                    },
                    onClick = { onIntent(NotificationsContract.Intent.PushToggled(!uiState.isPushEnabled)) }
                )
                SettingsListItem(
                    title = "Email Notifications",
                    icon = Icons.Outlined.Email,
                    trailingContent = {
                        Switch(
                            checked = uiState.isEmailEnabled,
                            onCheckedChange = { onIntent(NotificationsContract.Intent.EmailToggled(it)) }
                        )
                    },
                    onClick = { onIntent(NotificationsContract.Intent.EmailToggled(!uiState.isEmailEnabled)) }
                )
                SettingsListItem(
                    title = "Trip Alerts",
                    icon = Icons.Outlined.Timeline,
                    trailingContent = {
                        Switch(
                            checked = uiState.isAlertsEnabled,
                            onCheckedChange = { onIntent(NotificationsContract.Intent.AlertsToggled(it)) }
                        )
                    },
                    onClick = { onIntent(NotificationsContract.Intent.AlertsToggled(!uiState.isAlertsEnabled)) }
                )
                SettingsListItem(
                    title = "Reminders",
                    icon = Icons.Outlined.Schedule,
                    trailingContent = {
                        Switch(
                            checked = uiState.isRemindersEnabled,
                            onCheckedChange = { onIntent(NotificationsContract.Intent.RemindersToggled(it)) }
                        )
                    },
                    onClick = { onIntent(NotificationsContract.Intent.RemindersToggled(!uiState.isRemindersEnabled)) }
                )
                SettingsListItem(
                    title = "Marketing Emails",
                    icon = Icons.Outlined.Notifications,
                    trailingContent = {
                        Switch(
                            checked = uiState.isMarketingEnabled,
                            onCheckedChange = { onIntent(NotificationsContract.Intent.MarketingToggled(it)) }
                        )
                    },
                    onClick = { onIntent(NotificationsContract.Intent.MarketingToggled(!uiState.isMarketingEnabled)) }
                )
            }
        }
    }
}
