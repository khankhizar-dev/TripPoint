package com.android.trippoint.budget.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointBudgetCard
import com.android.trippoint.core.designsystem.components.TripPointEmptyState
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BudgetListRoute(
    tripId: String?,
    viewModel: BudgetListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String, String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(BudgetListContract.Intent.LoadBudgets(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                BudgetListContract.Effect.NavigateBack -> onNavigateBack()
                is BudgetListContract.Effect.NavigateToBudgetDetails -> {
                    onNavigateToDetails(effect.tripId, effect.budgetId)
                }
                BudgetListContract.Effect.NavigateToCreateBudget -> onNavigateToCreate()
            }
        }
    }

    BudgetListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun BudgetListScreen(
    uiState: BudgetListContract.State,
    onIntent: (BudgetListContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "My Budgets",
                onNavClick = { onIntent(BudgetListContract.Intent.BackClicked) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(BudgetListContract.Intent.CreateBudgetClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Budget")
            }
        }
    ) { innerPadding ->
        BudgetListContent(
            uiState = uiState,
            onIntent = onIntent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun BudgetListContent(
    uiState: BudgetListContract.State,
    onIntent: (BudgetListContract.Intent) -> Unit,
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
    } else if (uiState.budgets.isEmpty()) {
        TripPointEmptyState(
            title = "No budgets yet",
            subtitle = "Start planning your trip expenses by creating a budget.",
            imageResId = designR.drawable.illustration_empty_trip,
            actionText = "Create Budget",
            onActionClick = { onIntent(BudgetListContract.Intent.CreateBudgetClicked) }
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.budgets) { budget ->
                TripPointBudgetCard(
                    title = budget.title,
                    totalBudget = "${budget.currency} ${budget.totalAmount}",
                    spentSoFar = "${budget.currency} ${budget.spentAmount}",
                    progress = budget.progress,
                    onClick = { 
                        onIntent(BudgetListContract.Intent.BudgetClicked(budget.tripId, budget.id)) 
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
