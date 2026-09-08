package com.android.trippoint.budget.expense.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.budget.domain.model.Expense
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointEmptyState
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ExpenseListRoute(
    tripId: String,
    budgetId: String,
    viewModel: ExpenseListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: (String, String) -> Unit,
    onNavigateToDetails: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, budgetId) {
        viewModel.onIntent(ExpenseListContract.Intent.LoadExpenses(tripId, budgetId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ExpenseListContract.Effect.NavigateBack -> onNavigateBack()
                is ExpenseListContract.Effect.NavigateToAddExpense -> {
                    onNavigateToAddExpense(tripId, effect.budgetId)
                }
            }
        }
    }

    ExpenseListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onExpenseClick = { eId -> onNavigateToDetails(tripId, eId) }
    )
}

@Composable
fun ExpenseListScreen(
    uiState: ExpenseListContract.State,
    onIntent: (ExpenseListContract.Intent) -> Unit,
    onExpenseClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.budget_expense_list_title),
                onNavClick = { onIntent(ExpenseListContract.Intent.BackClicked) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(ExpenseListContract.Intent.AddExpenseClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        ExpenseListContent(
            uiState = uiState, 
            onIntent = onIntent,
            onExpenseClick = onExpenseClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun ExpenseListContent(
    uiState: ExpenseListContract.State,
    onIntent: (ExpenseListContract.Intent) -> Unit,
    onExpenseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
    } else if (uiState.error != null) {
        Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            TripPointAlert(message = uiState.error!!, variant = AlertVariant.Error)
        }
    } else if (uiState.expenses.isEmpty()) {
        TripPointEmptyState(
            title = "No expenses yet",
            subtitle = "Start tracking your spending for this trip.",
            imageResId = designR.drawable.illustration_empty_trip,
            actionText = "Add Expense",
            onActionClick = { onIntent(ExpenseListContract.Intent.AddExpenseClicked) }
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.expenses) { expense ->
                val paidByName = uiState.members.find { it.userId == expense.paidBy }?.userName 
                    ?: expense.paidBy?.take(8) ?: "Unknown"
                
                ExpenseItem(
                    expense = expense, 
                    paidByName = paidByName,
                    onClick = { onExpenseClick(expense.id) }
                )
            }
        }
    }
}

@Composable
private fun ExpenseItem(expense: Expense, paidByName: String, onClick: () -> Unit) {
    TripPointInfoCard(
        title = expense.category,
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Paid by $paidByName • ${expense.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${expense.currency} ${expense.amount}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
