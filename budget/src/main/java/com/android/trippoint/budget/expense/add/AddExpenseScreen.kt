package com.android.trippoint.budget.expense.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
fun AddExpenseRoute(
    tripId: String,
    budgetId: String,
    viewModel: AddExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, budgetId) {
        viewModel.onIntent(AddExpenseContract.Intent.LoadIds(tripId, budgetId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddExpenseContract.Effect.NavigateBack -> onNavigateBack()
                AddExpenseContract.Effect.ExpenseAdded -> onNavigateBack()
            }
        }
    }

    AddExpenseScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    uiState: AddExpenseContract.State,
    onIntent: (AddExpenseContract.Intent) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        ExpenseDatePickerDialog(
            onDateSelected = { onIntent(AddExpenseContract.Intent.DateChanged(it)) },
            onDismiss = { showDatePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.budget_add_expense_title),
                onNavClick = { onIntent(AddExpenseContract.Intent.BackClicked) }
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
            val errorMessage = uiState.error ?: uiState.errorResId?.let { stringResource(id = it) }
            if (errorMessage != null) {
                TripPointAlert(
                    message = errorMessage,
                    variant = AlertVariant.Error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TripPointTextField(
                value = uiState.amount,
                onValueChange = { onIntent(AddExpenseContract.Intent.AmountChanged(it)) },
                label = stringResource(id = designR.string.budget_amount_label),
                placeholder = "0.00"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripPointDropdown(
                value = uiState.category,
                onValueChange = { onIntent(AddExpenseContract.Intent.CategoryChanged(it)) },
                label = stringResource(id = designR.string.budget_category_label),
                options = listOf(
                    stringResource(id = designR.string.budget_category_food),
                    stringResource(id = designR.string.budget_category_transport),
                    stringResource(id = designR.string.budget_category_activities),
                    stringResource(id = designR.string.budget_category_accommodation),
                    stringResource(id = designR.string.budget_category_other)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripPointTextField(
                value = uiState.date,
                onValueChange = { },
                label = stringResource(id = designR.string.budget_date_label),
                placeholder = "YYYY-MM-DD",
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripPointTextField(
                value = uiState.description,
                onValueChange = { onIntent(AddExpenseContract.Intent.DescriptionChanged(it)) },
                label = stringResource(id = designR.string.budget_description_label),
                placeholder = "e.g. Starbucks Coffee"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.members.isNotEmpty()) {
                TripPointDropdown(
                    value = uiState.paidBy,
                    onValueChange = { selected ->
                        // Find the userId for the selected name/id
                        val member = uiState.members.find { 
                            (it.userName ?: it.userId) == selected 
                        }
                        onIntent(AddExpenseContract.Intent.PaidByChanged(member?.userId ?: selected))
                    },
                    label = stringResource(id = designR.string.budget_paid_by_label),
                    options = uiState.members.map { it.userName ?: it.userId },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                TripPointTextField(
                    value = uiState.paidBy,
                    onValueChange = { onIntent(AddExpenseContract.Intent.PaidByChanged(it)) },
                    label = stringResource(id = designR.string.budget_paid_by_label),
                    placeholder = stringResource(id = designR.string.budget_paid_by_placeholder)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TripPointButton(
                text = stringResource(id = designR.string.budget_save_expense),
                onClick = { onIntent(AddExpenseContract.Intent.SaveClicked) },
                enabled = uiState.amount.isNotBlank() && uiState.date.isNotBlank(),
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseDatePickerDialog(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
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
