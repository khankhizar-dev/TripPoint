package com.android.trippoint.itinerary.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.common.model.Priority
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddTaskRoute(
    tripId: String,
    date: String,
    viewModel: AddTaskViewModel,
    onNavigateBack: () -> Unit,
    onTaskAdded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, date) {
        viewModel.onIntent(AddTaskContract.Intent.LoadTripInfo(tripId, date))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddTaskContract.Effect.NavigateBack -> onNavigateBack()
                AddTaskContract.Effect.TaskAdded -> onTaskAdded()
                is AddTaskContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    AddTaskScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    uiState: AddTaskContract.State,
    onIntent: (AddTaskContract.Intent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    if (showDatePicker) {
        AddTaskDatePickerDialog(
            onDateSelected = { onIntent(AddTaskContract.Intent.DateChanged(it)) },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        AddTaskTimePickerDialog(
            onTimeSelected = { onIntent(AddTaskContract.Intent.TimeChanged(it)) },
            onDismiss = { showTimePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = designR.string.add_task_title)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddTaskContract.Intent.BackClicked) }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AddTaskForm(uiState, onIntent, { showDatePicker = true }, { showTimePicker = true })
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TripPointButton(
                text = stringResource(id = designR.string.add_task_save_button),
                onClick = { onIntent(AddTaskContract.Intent.SaveClicked) },
                enabled = uiState.name.isNotBlank() && uiState.date.isNotBlank(),
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AddTaskForm(
    uiState: AddTaskContract.State,
    onIntent: (AddTaskContract.Intent) -> Unit,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit
) {
    TripPointTextField(
        value = uiState.name,
        onValueChange = { onIntent(AddTaskContract.Intent.NameChanged(it)) },
        label = stringResource(id = designR.string.add_task_name_label),
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    AddTaskDateTimeRow(uiState.date, uiState.time, onShowDatePicker, onShowTimePicker)
    
    Spacer(modifier = Modifier.height(24.dp))
    
    Text(
        text = stringResource(id = designR.string.add_task_priority_label),
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(12.dp))
    PrioritySelector(
        selectedPriority = uiState.priority,
        onPrioritySelected = { onIntent(AddTaskContract.Intent.PriorityChanged(it)) }
    )
}

@Composable
private fun AddTaskDateTimeRow(
    date: String,
    time: String,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        TripPointTextField(
            value = date,
            onValueChange = { },
            label = stringResource(id = designR.string.add_event_date_label),
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
            label = stringResource(id = designR.string.add_task_priority_label),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onShowTimePicker) {
                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
                }
            },
            modifier = Modifier.weight(1f).clickable { onShowTimePicker() }
        )
    }
}

@Composable
private fun PrioritySelector(
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Priority.entries.forEach { priority ->
            PriorityButton(
                priority = priority,
                isSelected = selectedPriority == priority,
                onClick = { onPrioritySelected(priority) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PriorityButton(
    priority: Priority,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = priority.name, style = MaterialTheme.typography.bodyMedium, color = contentColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDatePickerDialog(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
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
            }) { Text("OK") }
        }
    ) { DatePicker(state = state) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskTimePickerDialog(onTimeSelected: (String) -> Unit, onDismiss: () -> Unit) {
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

@OptIn(ExperimentalMaterial3Api::class)
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
                    text = "Select Time",
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}
