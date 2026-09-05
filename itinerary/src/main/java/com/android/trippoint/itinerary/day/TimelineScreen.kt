package com.android.trippoint.itinerary.day

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.itinerary.domain.model.TimelineEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TimelineRoute(
    tripId: String,
    date: String,
    viewModel: TimelineViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEventDetails: (String, String, String) -> Unit,
    onNavigateToAddEvent: (String, String) -> Unit,
    onNavigateToAddTask: (String, String) -> Unit,
    onNavigateToAddNote: (String) -> Unit,
    onNavigateToFilter: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, date) {
        viewModel.onIntent(TimelineContract.Intent.LoadTimeline(tripId, date))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                TimelineContract.Effect.NavigateBack -> onNavigateBack()
                is TimelineContract.Effect.NavigateToEventDetails -> 
                    onNavigateToEventDetails(effect.tripId, effect.dayId, effect.eventId)
                is TimelineContract.Effect.NavigateToAddEvent -> onNavigateToAddEvent(effect.tripId, effect.date)
                is TimelineContract.Effect.NavigateToAddTask -> onNavigateToAddTask(effect.tripId, effect.date)
                is TimelineContract.Effect.NavigateToAddNote -> onNavigateToAddNote(effect.tripId)
                is TimelineContract.Effect.NavigateToFilter -> onNavigateToFilter(effect.tripId)
                is TimelineContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }
    
    TimelineScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun TimelineScreen(
    uiState: TimelineContract.State,
    onIntent: (TimelineContract.Intent) -> Unit
) {
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            MultiActionFAB(
                expanded = fabExpanded,
                onExpandChange = { fabExpanded = it },
                onAddEvent = { onIntent(TimelineContract.Intent.AddEventClicked) },
                onAddTask = { onIntent(TimelineContract.Intent.AddTaskClicked) },
                onAddNote = { onIntent(TimelineContract.Intent.AddNoteClicked) }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TimelineHeader(
                date = uiState.selectedDate,
                onFilterClick = { onIntent(TimelineContract.Intent.FilterClicked) }
            )
            
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            } else {
                TimelineList(
                    events = uiState.events, 
                    onEventClick = { onIntent(TimelineContract.Intent.EventClicked(it)) },
                    onToggleCompletion = { id, completed -> 
                        onIntent(TimelineContract.Intent.ToggleEventCompletion(id, completed))
                    }
                )
            }
        }
    }
}

@Composable
private fun MultiActionFAB(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onAddEvent: () -> Unit,
    onAddTask: () -> Unit,
    onAddNote: () -> Unit
) {
    Column(horizontalAlignment = Alignment.End) {
        if (expanded) {
            SmallFloatingActionButton(
                onClick = { onAddNote(); onExpandChange(false) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "Add Note")
            }
            SmallFloatingActionButton(
                onClick = { onAddTask(); onExpandChange(false) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Event, contentDescription = "Add Task") // Using Event as proxy
            }
            SmallFloatingActionButton(
                onClick = { onAddEvent(); onExpandChange(false) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Event, contentDescription = "Add Event")
            }
        }
        FloatingActionButton(
            onClick = { onExpandChange(!expanded) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (expanded) "Close" else "Add"
            )
        }
    }
}

@Composable
private fun TimelineHeader(
    date: String,
    onFilterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(Color(0xFF1B4332)) // Dark green from design
            .padding(24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White
                )
                Text(
                    text = date.ifEmpty { "12 May, Mon" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun TimelineList(
    events: List<TimelineEvent>,
    onEventClick: (String) -> Unit,
    onToggleCompletion: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        itemsIndexed(events) { index, event ->
            TimelineItem(
                event = event,
                isLast = index == events.size - 1,
                onClick = { onEventClick(event.id) },
                onToggleCompletion = { onToggleCompletion(event.id, it) }
            )
        }
    }
}

@Composable
private fun TimelineItem(
    event: TimelineEvent,
    isLast: Boolean,
    onClick: () -> Unit,
    onToggleCompletion: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp)
        ) {
            Text(
                text = event.startTime,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = event.location ?: event.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Checkbox(
                    checked = event.completed,
                    onCheckedChange = { onToggleCompletion(it) }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}
