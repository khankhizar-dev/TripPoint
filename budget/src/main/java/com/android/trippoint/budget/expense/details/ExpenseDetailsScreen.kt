package com.android.trippoint.budget.expense.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ExpenseDetailsRoute(
    tripId: String,
    expenseId: String,
    viewModel: ExpenseDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, expenseId) {
        viewModel.onIntent(ExpenseDetailsContract.Intent.LoadExpense(tripId, expenseId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ExpenseDetailsContract.Effect.NavigateBack -> onNavigateBack()
                is ExpenseDetailsContract.Effect.NavigateToEdit -> onNavigateToEdit(effect.tripId, effect.expenseId)
            }
        }
    }

    ExpenseDetailsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ExpenseDetailsScreen(
    uiState: ExpenseDetailsContract.State,
    onIntent: (ExpenseDetailsContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "Expense Details",
                onNavClick = { onIntent(ExpenseDetailsContract.Intent.BackClicked) },
                actions = {
                    IconButton(onClick = { onIntent(ExpenseDetailsContract.Intent.EditClicked) }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onIntent(ExpenseDetailsContract.Intent.ArchiveClicked) }) {
                        Icon(imageVector = Icons.Default.Archive, contentDescription = "Archive")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp), 
                contentAlignment = Alignment.Center
            ) {
                TripPointAlert(message = uiState.error, variant = AlertVariant.Error)
            }
        } else {
            uiState.expense?.let { expense ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp)
                ) {
                    Text(
                        text = expense.category,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "${expense.currency} ${expense.amount}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TripPointInfoCard(title = "Description") {
                        Text(text = expense.description, style = MaterialTheme.typography.bodyLarge)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TripPointInfoCard(title = "Date") {
                        Text(text = expense.date, style = MaterialTheme.typography.bodyLarge)
                    }
                    
                    if (expense.location != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TripPointInfoCard(title = "Location") {
                            Text(text = expense.location, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
