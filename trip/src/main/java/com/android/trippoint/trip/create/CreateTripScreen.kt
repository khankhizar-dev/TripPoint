package com.android.trippoint.trip.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateTripRoute(
    viewModel: CreateTripViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddDetails: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is CreateTripContract.Effect.NavigateToAddDetails -> onNavigateToAddDetails(effect.tripId)
                CreateTripContract.Effect.NavigateBack -> onNavigateBack()
                is CreateTripContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    CreateTripScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    uiState: CreateTripContract.State,
    onIntent: (CreateTripContract.Intent) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()

    if (showStartDatePicker) {
        TripDatePicker(
            state = startDatePickerState,
            onDismiss = { showStartDatePicker = false },
            onConfirm = { onIntent(CreateTripContract.Intent.StartDateChanged(it)); showStartDatePicker = false }
        )
    }
    if (showEndDatePicker) {
        TripDatePicker(
            state = endDatePickerState,
            onDismiss = { showEndDatePicker = false },
            onConfirm = { onIntent(CreateTripContract.Intent.EndDateChanged(it)); showEndDatePicker = false }
        )
    }

    Scaffold(
        topBar = {
            CreateTripTopBar(onIntent = onIntent)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CreateTripPhotoSection()
            CreateTripForm(
                uiState = uiState,
                onIntent = onIntent,
                onShowStartDate = { showStartDatePicker = true },
                onShowEndDate = { showEndDatePicker = true }
            )
            CreateTripTravelersSection()
            Spacer(modifier = Modifier.weight(1f))
            CreateTripActions(uiState = uiState, onIntent = onIntent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTripTopBar(onIntent: (CreateTripContract.Intent) -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = designR.string.create_trip_title)) },
        navigationIcon = {
            IconButton(onClick = { onIntent(CreateTripContract.Intent.BackClicked) }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        }
    )
}

@Composable
private fun CreateTripPhotoSection() {
    Spacer(modifier = Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
            .clickable { /* Photo Picker */ },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = designR.string.create_trip_add_photo),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun CreateTripForm(
    uiState: CreateTripContract.State,
    onIntent: (CreateTripContract.Intent) -> Unit,
    onShowStartDate: () -> Unit,
    onShowEndDate: () -> Unit
) {
    TripPointTextField(
        value = uiState.name,
        onValueChange = { onIntent(CreateTripContract.Intent.NameChanged(it)) },
        label = stringResource(id = designR.string.create_trip_name_label),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    TripPointTextField(
        value = uiState.destination,
        onValueChange = { onIntent(CreateTripContract.Intent.DestinationChanged(it)) },
        label = stringResource(id = designR.string.create_trip_destination_label),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        TripPointTextField(
            value = uiState.startDate,
            onValueChange = { },
            label = stringResource(id = designR.string.create_trip_start_date_label),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onShowStartDate) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                }
            },
            modifier = Modifier.weight(1f).clickable { onShowStartDate() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        TripPointTextField(
            value = uiState.endDate,
            onValueChange = { },
            label = stringResource(id = designR.string.create_trip_end_date_label),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onShowEndDate) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                }
            },
            modifier = Modifier.weight(1f).clickable { onShowEndDate() }
        )
    }
}

@Composable
private fun CreateTripTravelersSection() {
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = stringResource(id = designR.string.create_trip_travelers_label),
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(4) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width((-12).dp))
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun CreateTripActions(
    uiState: CreateTripContract.State,
    onIntent: (CreateTripContract.Intent) -> Unit
) {
    Spacer(modifier = Modifier.height(40.dp))
    TripPointButton(
        text = stringResource(id = designR.string.create_trip_next_button),
        onClick = { onIntent(CreateTripContract.Intent.NextClicked) },
        enabled = uiState.name.isNotBlank() && uiState.destination.isNotBlank(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(24.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripDatePicker(
    state: DatePickerState,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let {
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(it))
                    onConfirm(formattedDate)
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = state)
    }
}
