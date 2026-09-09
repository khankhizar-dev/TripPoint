package com.android.trippoint.checklist.add_item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.checklist.domain.model.ChecklistItemCategory
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointDropdown
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddChecklistItemRoute(
    tripId: String,
    checklistId: String,
    sectionId: String,
    viewModel: AddChecklistItemViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, checklistId, sectionId) {
        viewModel.onIntent(AddChecklistItemContract.Intent.LoadIds(tripId, checklistId, sectionId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddChecklistItemContract.Effect.NavigateBack -> onNavigateBack()
                AddChecklistItemContract.Effect.ItemAdded -> onNavigateBack()
                is AddChecklistItemContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    AddChecklistItemScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun AddChecklistItemScreen(
    uiState: AddChecklistItemContract.State,
    onIntent: (AddChecklistItemContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.checklist_add_item_title),
                onNavClick = { onIntent(AddChecklistItemContract.Intent.BackClicked) }
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
            val error = uiState.error
            if (error != null) {
                TripPointAlert(message = error, variant = AlertVariant.Error)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TripPointTextField(
                value = uiState.name,
                onValueChange = { onIntent(AddChecklistItemContract.Intent.NameChanged(it)) },
                label = stringResource(id = designR.string.checklist_item_name_label),
                placeholder = stringResource(id = designR.string.checklist_item_name_placeholder)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TripPointDropdown(
                value = uiState.category.name,
                onValueChange = { onIntent(AddChecklistItemContract.Intent.CategoryChanged(it)) },
                label = stringResource(id = designR.string.checklist_item_category_label),
                options = ChecklistItemCategory.values().map { it.name }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TripPointTextField(
                value = uiState.notes,
                onValueChange = { onIntent(AddChecklistItemContract.Intent.NotesChanged(it)) },
                label = stringResource(id = designR.string.checklist_item_notes_label),
                placeholder = stringResource(id = designR.string.checklist_item_notes_placeholder),
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SwitchRow(
                label = stringResource(id = designR.string.checklist_item_essential_label),
                checked = uiState.isEssential,
                onCheckedChange = { onIntent(AddChecklistItemContract.Intent.EssentialToggled(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SwitchRow(
                label = stringResource(id = designR.string.checklist_item_remind_me_label),
                checked = uiState.remindMe,
                onCheckedChange = { onIntent(AddChecklistItemContract.Intent.RemindMeToggled(it)) }
            )

            if (uiState.remindMe) {
                Spacer(modifier = Modifier.height(16.dp))
                // Mock for date/time picker
                TripPointTextField(
                    value = uiState.reminderTime,
                    onValueChange = { onIntent(AddChecklistItemContract.Intent.ReminderTimeChanged(it)) },
                    label = "Reminder Date & Time",
                    placeholder = "24 May, 9:30 AM"
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            TripPointButton(
                text = stringResource(id = designR.string.checklist_item_save_button),
                onClick = { onIntent(AddChecklistItemContract.Intent.SaveClicked) },
                enabled = uiState.name.isNotBlank(),
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
