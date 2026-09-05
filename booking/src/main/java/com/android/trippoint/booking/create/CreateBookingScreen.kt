package com.android.trippoint.booking.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointDropdown
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateBookingRoute(
    tripId: String,
    viewModel: CreateBookingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(CreateBookingContract.Intent.LoadTripId(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                CreateBookingContract.Effect.NavigateBack -> onNavigateBack()
                CreateBookingContract.Effect.BookingCreated -> onNavigateBack()
            }
        }
    }

    CreateBookingScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingScreen(
    uiState: CreateBookingContract.State,
    onIntent: (CreateBookingContract.Intent) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        BookingDatePickerDialog(
            onDateSelected = { onIntent(CreateBookingContract.Intent.DateChanged(it)) },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        BookingTimePickerDialog(
            onTimeSelected = { onIntent(CreateBookingContract.Intent.TimeChanged(it)) },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showEndDatePicker) {
        BookingDatePickerDialog(
            onDateSelected = { onIntent(CreateBookingContract.Intent.EndDateChanged(it)) },
            onDismiss = { showEndDatePicker = false }
        )
    }

    if (showEndTimePicker) {
        BookingTimePickerDialog(
            onTimeSelected = { onIntent(CreateBookingContract.Intent.EndTimeChanged(it)) },
            onDismiss = { showEndTimePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.bookings_add_manual),
                onNavClick = { onIntent(CreateBookingContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.error != null) {
                TripPointAlert(
                    message = uiState.error,
                    variant = AlertVariant.Error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            BookingBasicInfo(uiState, onIntent)

            BookingDateTimeSection(
                date = uiState.date,
                time = uiState.time,
                endDate = uiState.endDate,
                endTime = uiState.endTime,
                onShowDatePicker = { showDatePicker = true },
                onShowTimePicker = { showTimePicker = true },
                onShowEndDatePicker = { showEndDatePicker = true },
                onShowEndTimePicker = { showEndTimePicker = true }
            )
            
            BookingAdditionalDetails(uiState, onIntent)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TripPointButton(
                text = stringResource(id = designR.string.add_task_save_button),
                onClick = { onIntent(CreateBookingContract.Intent.SaveClicked) },
                enabled = uiState.title.isNotBlank() && uiState.date.isNotBlank(),
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BookingBasicInfo(
    uiState: CreateBookingContract.State,
    onIntent: (CreateBookingContract.Intent) -> Unit
) {
    if (uiState.tripId.isBlank()) {
        TripPointDropdown(
            value = uiState.tripId,
            onValueChange = { onIntent(CreateBookingContract.Intent.LoadTripId(it)) },
            label = stringResource(id = designR.string.bookings_add_select_trip),
            options = uiState.availableTrips.map { it.id },
            optionLabels = uiState.availableTrips.map { it.title },
            placeholder = stringResource(id = designR.string.bookings_add_placeholder_trip)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    TripPointTextField(
        value = uiState.title,
        onValueChange = { onIntent(CreateBookingContract.Intent.TitleChanged(it)) },
        label = stringResource(id = designR.string.bookings_add_title_label),
        placeholder = stringResource(id = designR.string.bookings_add_placeholder_title)
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    TripPointDropdown(
        value = uiState.type,
        onValueChange = { onIntent(CreateBookingContract.Intent.TypeChanged(it)) },
        label = stringResource(id = designR.string.bookings_filter_type_label),
        options = listOf("FLIGHT", "HOTEL", "TRANSPORTATION", "ACTIVITY", "OTHER")
    )
    
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun BookingDateTimeSection(
    date: String,
    time: String,
    endDate: String,
    endTime: String,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit,
    onShowEndDatePicker: () -> Unit,
    onShowEndTimePicker: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        TripPointTextField(
            value = date,
            onValueChange = { },
            label = stringResource(id = designR.string.bookings_filter_date_label),
            placeholder = "YYYY-MM-DD",
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onShowDatePicker) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                }
            },
            modifier = Modifier.weight(1f).clickable { onShowDatePicker() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        TripPointTextField(
            value = time,
            onValueChange = { },
            label = stringResource(id = designR.string.add_event_time_label),
            placeholder = "HH:mm",
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onShowTimePicker) {
                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
                }
            },
            modifier = Modifier.weight(1f).clickable { onShowTimePicker() }
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        TripPointTextField(
            value = endDate,
            onValueChange = { },
            label = stringResource(id = designR.string.bookings_add_end_date_label),
            placeholder = "YYYY-MM-DD",
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onShowEndDatePicker) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                }
            },
            modifier = Modifier.weight(1f).clickable { onShowEndDatePicker() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        TripPointTextField(
            value = endTime,
            onValueChange = { },
            label = stringResource(id = designR.string.bookings_add_end_time_label),
            placeholder = "HH:mm",
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onShowEndTimePicker) {
                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
                }
            },
            modifier = Modifier.weight(1f).clickable { onShowEndTimePicker() }
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun BookingAdditionalDetails(
    uiState: CreateBookingContract.State,
    onIntent: (CreateBookingContract.Intent) -> Unit
) {
    TripPointTextField(
        value = uiState.location,
        onValueChange = { onIntent(CreateBookingContract.Intent.LocationChanged(it)) },
        label = stringResource(id = designR.string.bookings_details_location),
        placeholder = stringResource(id = designR.string.bookings_add_placeholder_location)
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    TripPointTextField(
        value = uiState.provider,
        onValueChange = { onIntent(CreateBookingContract.Intent.ProviderChanged(it)) },
        label = stringResource(id = designR.string.bookings_details_provider),
        placeholder = stringResource(id = designR.string.bookings_add_placeholder_provider)
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    TripPointTextField(
        value = uiState.reference,
        onValueChange = { onIntent(CreateBookingContract.Intent.ReferenceChanged(it)) },
        label = stringResource(id = designR.string.bookings_details_reference),
        placeholder = stringResource(id = designR.string.bookings_add_placeholder_reference)
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    TripPointTextField(
        value = uiState.amount,
        onValueChange = { onIntent(CreateBookingContract.Intent.AmountChanged(it)) },
        label = stringResource(id = designR.string.bookings_details_total_amount),
        placeholder = stringResource(id = designR.string.bookings_add_placeholder_amount)
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    TripPointTextField(
        value = uiState.notes,
        onValueChange = { onIntent(CreateBookingContract.Intent.NotesChanged(it)) },
        label = stringResource(id = designR.string.bookings_details_notes),
        placeholder = stringResource(id = designR.string.bookings_add_notes_placeholder),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingDatePickerDialog(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val state = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let {
                    val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    onDateSelected(formatted)
                }
                onDismiss()
            }) { Text(stringResource(id = designR.string.bookings_add_ok)) }
        }
    ) { DatePicker(state = state) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingTimePickerDialog(onTimeSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val state = rememberTimePickerState()
    TimePickerDialog(
        onDismiss = onDismiss,
        onConfirm = {
            val formatted = String.format(Locale.getDefault(), "%02d:%02d", state.hour, state.minute)
            onTimeSelected(formatted)
            onDismiss()
        }
    ) { TimePicker(state = state) }
}

@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    text = stringResource(id = designR.string.bookings_add_select_time),
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(stringResource(id = designR.string.bookings_add_cancel)) }
                    TextButton(onClick = onConfirm) { Text(stringResource(id = designR.string.bookings_add_ok)) }
                }
            }
        }
    }
}
