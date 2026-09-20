package com.android.trippoint.notification.reminders

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointTabs
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.notification.domain.model.Reminder
import com.android.trippoint.notification.domain.model.ReminderStatus
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RemindersRoute(
    viewModel: RemindersViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                RemindersContract.Effect.NavigateBack -> onNavigateBack()
                RemindersContract.Effect.NavigateToAddReminder -> { /* Handle */ }
                is RemindersContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    RemindersScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun RemindersScreen(
    uiState: RemindersContract.State,
    onIntent: (RemindersContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "Reminders",
                onNavClick = { onIntent(RemindersContract.Intent.BackClicked) },
                actions = {
                    IconButton(onClick = { /* History */ }) {
                        Icon(imageVector = Icons.Default.History, contentDescription = "History")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(RemindersContract.Intent.AddReminderClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TripPointTabs(
                tabs = listOf(
                    "Upcoming (${uiState.upcomingCount})",
                    "Overdue (${uiState.overdueCount})",
                    "Done"
                ),
                selectedTabIndex = uiState.selectedTab,
                onTabSelected = { onIntent(RemindersContract.Intent.TabSelected(it)) },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.filteredReminders) { reminder ->
                        ReminderCard(reminder, onIntent)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    onIntent: (RemindersContract.Intent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reminder.tripName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = reminder.dueDateTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (reminder.status == ReminderStatus.OVERDUE) MaterialTheme.colorScheme.error 
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (reminder.status != ReminderStatus.DONE) {
                    TextButton(onClick = { onIntent(RemindersContract.Intent.SnoozeReminder(reminder.id)) }) {
                        Text("Snooze")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onIntent(RemindersContract.Intent.MarkAsDone(reminder.id)) }) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark as Done")
                    }
                }
            }
        }
    }
}
