package com.android.trippoint.budget.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.ButtonVariant
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointBudgetCard
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointInteractiveCard
import com.android.trippoint.core.designsystem.components.TripPointLinearProgress
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import kotlinx.coroutines.flow.collectLatest

@Suppress("LongParameterList")
@Composable
fun BudgetOverviewRoute(
    tripId: String,
    budgetId: String,
    viewModel: BudgetOverviewViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: (String, String) -> Unit,
    onNavigateToExpenses: (String, String) -> Unit,
    onNavigateToTrends: (String, String) -> Unit,
    onNavigateToReports: (String, String) -> Unit,
    onNavigateToSettlements: (String) -> Unit,
    onNavigateToScanner: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, budgetId) {
        viewModel.onIntent(BudgetOverviewContract.Intent.LoadBudget(tripId, budgetId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                BudgetOverviewContract.Effect.NavigateBack -> onNavigateBack()
                is BudgetOverviewContract.Effect.NavigateToAddExpense -> {
                    onNavigateToAddExpense(tripId, effect.budgetId)
                }
            }
        }
    }

    BudgetOverviewScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onViewAllExpenses = { onNavigateToExpenses(tripId, budgetId) },
        onTrendsClick = { onNavigateToTrends(tripId, budgetId) },
        onReportsClick = { onNavigateToReports(tripId, budgetId) },
        onSettlementsClick = { onNavigateToSettlements(tripId) },
        onScannerClick = { onNavigateToScanner(budgetId) }
    )
}

@Composable
fun BudgetOverviewScreen(
    uiState: BudgetOverviewContract.State,
    onIntent: (BudgetOverviewContract.Intent) -> Unit,
    onViewAllExpenses: () -> Unit,
    onTrendsClick: () -> Unit,
    onReportsClick: () -> Unit,
    onSettlementsClick: () -> Unit,
    onScannerClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "Budget Overview",
                onNavClick = { onIntent(BudgetOverviewContract.Intent.BackClicked) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(BudgetOverviewContract.Intent.AddExpenseClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        BudgetOverviewContent(
            uiState = uiState,
            onViewAllExpenses = onViewAllExpenses,
            onTrendsClick = onTrendsClick,
            onReportsClick = onReportsClick,
            onSettlementsClick = onSettlementsClick,
            onScannerClick = onScannerClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun BudgetOverviewContent(
    uiState: BudgetOverviewContract.State,
    onViewAllExpenses: () -> Unit,
    onTrendsClick: () -> Unit,
    onReportsClick: () -> Unit,
    onSettlementsClick: () -> Unit,
    onScannerClick: () -> Unit,
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
    } else {
        uiState.budget?.let { budget ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                TripPointBudgetCard(
                    title = budget.title,
                    totalBudget = "${budget.currency} ${budget.totalAmount}",
                    spentSoFar = "${budget.currency} ${budget.spentAmount}",
                    progress = budget.progress,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TripPointInteractiveCard(
                        title = "Spending Trends",
                        subtitle = "View analytics",
                        onClick = onTrendsClick,
                        modifier = Modifier.weight(1f)
                    )
                    TripPointInteractiveCard(
                        title = "Reports",
                        subtitle = "Export data",
                        onClick = onReportsClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TripPointInteractiveCard(
                    title = "Settlements",
                    subtitle = "Who owes whom?",
                    onClick = onSettlementsClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TripPointButton(
                    text = "AI Receipt Scan",
                    onClick = onScannerClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Secondary,
                    leadingIcon = Icons.Default.Camera
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onViewAllExpenses) {
                        Text(text = "View All", style = MaterialTheme.typography.labelLarge)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                budget.categories.forEach { category ->
                    CategoryItem(category)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun CategoryItem(category: com.android.trippoint.budget.domain.model.BudgetCategory) {
    TripPointInfoCard(title = category.name) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent: ${category.spentAmount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Limit: ${category.allocatedAmount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TripPointLinearProgress(progress = category.progress)
        }
    }
}
