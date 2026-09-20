package com.android.trippoint.notification.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.SettingsListItem
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar

@Composable
fun NotificationPreferencesRoute(
    viewModel: NotificationPreferencesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChannels: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    NotificationPreferencesScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
        onNavigateToChannels = onNavigateToChannels
    )
}

@Composable
fun NotificationPreferencesScreen(
    uiState: NotificationPreferencesContract.State,
    onIntent: (NotificationPreferencesContract.Intent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToChannels: () -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "Preferences",
                onNavClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader(title = "Channels")
            
            SettingsListItem(
                title = "Push Notifications",
                subtitle = "Receive notifications on this device",
                icon = Icons.Outlined.NotificationsActive,
                trailingContent = {
                    Switch(
                        checked = uiState.preferences.pushEnabled,
                        onCheckedChange = { onIntent(NotificationPreferencesContract.Intent.PushToggled(it)) }
                    )
                }
            )
            
            SettingsListItem(
                title = "Email Notifications",
                subtitle = "Receive email alerts",
                icon = Icons.Outlined.Email,
                trailingContent = {
                    Switch(
                        checked = uiState.preferences.emailEnabled,
                        onCheckedChange = { onIntent(NotificationPreferencesContract.Intent.EmailToggled(it)) }
                    )
                }
            )
            
            SettingsListItem(
                title = "In-app Notifications",
                subtitle = "Receive in-app messages",
                icon = Icons.Outlined.PhoneIphone,
                trailingContent = {
                    Switch(
                        checked = uiState.preferences.inAppEnabled,
                        onCheckedChange = { onIntent(NotificationPreferencesContract.Intent.InAppToggled(it)) }
                    )
                }
            )
            
            SettingsListItem(
                title = "More Channel Settings",
                icon = Icons.Outlined.NotificationsActive,
                onClick = onNavigateToChannels
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Categories")
            
            SettingsListItem(
                title = "Manage notification categories",
                icon = Icons.Outlined.Schedule,
                onClick = { /* Navigate to categories */ }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Quiet Hours")
            
            SettingsListItem(
                title = "Quiet Hours",
                subtitle = "${uiState.preferences.quietHoursStart} - ${uiState.preferences.quietHoursEnd}",
                icon = Icons.Outlined.AccessTime,
                onClick = { /* Open Time Picker */ }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Digest")
            
            SettingsListItem(
                title = "Daily Digest",
                subtitle = "Daily at 8:00 AM", // Simplified or derived from models
                icon = Icons.Outlined.Schedule,
                trailingContent = {
                    Switch(
                        checked = uiState.preferences.digestEnabled,
                        onCheckedChange = { onIntent(NotificationPreferencesContract.Intent.DigestToggled(it)) }
                    )
                }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}
