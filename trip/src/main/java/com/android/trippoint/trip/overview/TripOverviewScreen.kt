package com.android.trippoint.trip.overview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.android.trippoint.core.common.model.Traveler
import com.android.trippoint.core.common.model.Trip
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.theme.TripPointTheme
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Suppress("LongParameterList")
@Composable
fun TripOverviewRoute(
    tripId: String,
    viewModel: TripOverviewViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTimeline: (String) -> Unit,
    onNavigateToBookings: (String) -> Unit,
    onNavigateToAddTask: (String) -> Unit,
    onNavigateToAddNote: (String) -> Unit,
    onNavigateToAddBooking: (String) -> Unit,
    onNavigateToAddExpense: (String) -> Unit,
    onNavigateToBudgets: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onIntent(TripOverviewContract.Intent.LoadTripDetails(tripId))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                TripOverviewContract.Effect.NavigateBack -> onNavigateBack()
                is TripOverviewContract.Effect.NavigateToTimeline -> onNavigateToTimeline(effect.tripId)
                is TripOverviewContract.Effect.NavigateToBookings -> onNavigateToBookings(effect.tripId)
                is TripOverviewContract.Effect.NavigateToAddTask -> onNavigateToAddTask(effect.tripId)
                is TripOverviewContract.Effect.NavigateToAddNote -> onNavigateToAddNote(effect.tripId)
                is TripOverviewContract.Effect.NavigateToAddBooking -> onNavigateToAddBooking(effect.tripId)
                is TripOverviewContract.Effect.NavigateToAddExpense -> onNavigateToAddExpense(effect.tripId)
                is TripOverviewContract.Effect.NavigateToBudgets -> onNavigateToBudgets(effect.tripId)
                is TripOverviewContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    TripOverviewScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripOverviewScreen(
    uiState: TripOverviewContract.State,
    onIntent: (TripOverviewContract.Intent) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = designR.string.trip_overview_title)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(TripOverviewContract.Intent.BackClicked) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                        TripOverviewMenu(
                            expanded = showMenu,
                            onDismiss = { showMenu = false },
                            onIntent = onIntent
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else if (uiState.trip != null) {
            TripOverviewContent(
                trip = uiState.trip,
                selectedTab = uiState.selectedTab,
                onIntent = onIntent,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun TripOverviewMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onIntent: (TripOverviewContract.Intent) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(
            text = { Text(stringResource(id = designR.string.trip_overview_menu_upcoming)) },
            onClick = {
                onIntent(TripOverviewContract.Intent.UpdateStatus(TripStatus.UPCOMING))
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = designR.string.trip_overview_menu_in_progress)) },
            onClick = {
                onIntent(TripOverviewContract.Intent.UpdateStatus(TripStatus.IN_PROGRESS))
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = designR.string.trip_overview_menu_completed)) },
            onClick = {
                onIntent(TripOverviewContract.Intent.UpdateStatus(TripStatus.COMPLETED))
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = designR.string.trip_overview_menu_archive)) },
            onClick = {
                onIntent(TripOverviewContract.Intent.ArchiveTrip)
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = designR.string.trip_overview_menu_delete)) },
            onClick = {
                onIntent(TripOverviewContract.Intent.DeleteTrip)
                onDismiss()
            }
        )
    }
}

@Composable
private fun TripOverviewContent(
    trip: Trip,
    selectedTab: Int,
    onIntent: (TripOverviewContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = trip.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = trip.title,
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "${trip.startDate} - ${trip.endDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                trip.travelers.take(3).forEachIndexed { index, traveler ->
                    AsyncImage(
                        model = traveler.photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                    if (index < 2) Spacer(modifier = Modifier.width((-8).dp)) // Overlap effect
                }
                if (trip.travelers.size > 3) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${trip.travelers.size - 3}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            val tabs = listOf(
                stringResource(id = designR.string.trip_overview_tab_overview),
                stringResource(id = designR.string.trip_overview_tab_timeline),
                stringResource(id = designR.string.trip_overview_tab_bookings),
                stringResource(id = designR.string.trip_overview_tab_tasks),
                stringResource(id = designR.string.trip_overview_tab_budget)
            )
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onIntent(TripOverviewContract.Intent.TabSelected(index)) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selectedTab == index) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            ProgressSection(trip.calculatedProgress)
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsSection(onIntent)
            Spacer(modifier = Modifier.height(24.dp))
            StatsSection(trip)
        }
    }
}

@Composable
private fun ProgressSection(progress: Float) {
    Column {
        Text(
            text = stringResource(id = designR.string.trip_overview_progress),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun QuickActionsSection(onIntent: (TripOverviewContract.Intent) -> Unit) {
    Text(
        text = stringResource(id = designR.string.trip_overview_quick_actions),
        style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                text = stringResource(id = designR.string.trip_overview_action_add_booking),
                onClick = { onIntent(TripOverviewContract.Intent.AddBookingClicked) },
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                text = stringResource(id = designR.string.trip_overview_action_add_task),
                onClick = { onIntent(TripOverviewContract.Intent.AddTaskClicked) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                text = stringResource(id = designR.string.trip_overview_action_add_expense),
                onClick = { onIntent(TripOverviewContract.Intent.AddExpenseClicked) },
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                text = stringResource(id = designR.string.trip_overview_action_add_note),
                onClick = { onIntent(TripOverviewContract.Intent.AddNoteClicked) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
            Text(text = text, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun StatsSection(trip: Trip) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatItem(
            label = stringResource(id = designR.string.trip_overview_budget_label),
            value = trip.budget,
            modifier = Modifier.weight(1f)
        )
        StatItem(
            label = stringResource(id = designR.string.trip_overview_tasks_label),
            value = "${trip.completedTasksCount}/${trip.tasksCount}",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripOverviewScreenPreview() {
    TripPointTheme {
        TripOverviewScreen(
            uiState = TripOverviewContract.State(
                trip = Trip(
                    id = "1",
                    ownerId = "owner1",
                    title = "Bali, Indonesia",
                    location = "Denpasar, Bali",
                    startDate = "12 May 2025",
                    endDate = "18 May 2025",
                    status = TripStatus.IN_PROGRESS,
                    imageUrl = "",
                    progress = 0.6f,
                    budget = "$1,240",
                    tasksCount = 20,
                    completedTasksCount = 12,
                    travelers = listOf(
                        Traveler("1", "John", ""),
                        Traveler("2", "Jane", "")
                    )
                )
            ),
            onIntent = {}
        )
    }
}
