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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.ButtonVariant
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointBudgetCard
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointEmptyState
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointInteractiveCard
import com.android.trippoint.core.designsystem.components.TripPointLinearProgress
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
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
    onNavigateToScanner: (String) -> Unit,
    onNavigateToSetupBudget: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onIntent(BudgetOverviewContract.Intent.LoadBudget(tripId, budgetId))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
        onScannerClick = { onNavigateToScanner(budgetId) },
        onSetupBudgetClick = { onNavigateToSetupBudget(tripId) }
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
    onScannerClick: () -> Unit,
    onSetupBudgetClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.budget_overview_title),
                onNavClick = { onIntent(BudgetOverviewContract.Intent.BackClicked) }
            )
        },
        floatingActionButton = {
            if (uiState.budget != null) {
                FloatingActionButton(
                    onClick = { onIntent(BudgetOverviewContract.Intent.AddExpenseClicked) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
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
            onSetupBudgetClick = onSetupBudgetClick,
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
    onSetupBudgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
    } else {
        val errorMessage = uiState.error ?: uiState.errorResId?.let { stringResource(id = it) }
        if (errorMessage != null) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp), 
                contentAlignment = Alignment.Center
            ) {
                TripPointAlert(message = errorMessage, variant = AlertVariant.Error)
            }
        } else if (uiState.budget == null) {
            BudgetEmptyState(onSetupBudgetClick, modifier)
        } else {
            BudgetOverviewDataContent(
                uiState = uiState,
                onViewAllExpenses = onViewAllExpenses,
                onTrendsClick = onTrendsClick,
                onReportsClick = onReportsClick,
                onSettlementsClick = onSettlementsClick,
                onScannerClick = onScannerClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun BudgetEmptyState(onSetupBudgetClick: () -> Unit, modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TripPointEmptyState(
            title = stringResource(id = designR.string.budget_no_setup_title),
            subtitle = stringResource(id = designR.string.budget_no_setup_desc),
            imageResId = designR.drawable.illustration_empty_trip,
            actionText = stringResource(id = designR.string.budget_create_title),
            onActionClick = onSetupBudgetClick
        )
    }
}

@Composable
private fun BudgetOverviewDataContent(
    uiState: BudgetOverviewContract.State,
    onViewAllExpenses: () -> Unit,
    onTrendsClick: () -> Unit,
    onReportsClick: () -> Unit,
    onSettlementsClick: () -> Unit,
    onScannerClick: () -> Unit,
    modifier: Modifier
) {
    val budget = uiState.budget!!
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
            onClick = { /* Already on overview */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        QuickInsightCards(onTrendsClick, onReportsClick)

        Spacer(modifier = Modifier.height(16.dp))

        TripPointInteractiveCard(
            title = stringResource(id = designR.string.budget_settlements_title),
            subtitle = stringResource(id = designR.string.budget_who_owes_whom),
            onClick = onSettlementsClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TripPointButton(
            text = stringResource(id = designR.string.budget_ai_receipt_scan),
            onClick = onScannerClick,
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.Secondary,
            leadingIcon = Icons.Default.Camera
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        CategoryBreakdownHeader(onViewAllExpenses)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        budget.categories.forEach { category ->
            CategoryItem(category)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun QuickInsightCards(onTrendsClick: () -> Unit, onReportsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TripPointInteractiveCard(
            title = stringResource(id = designR.string.budget_spending_trends),
            subtitle = stringResource(id = designR.string.budget_view_analytics),
            onClick = onTrendsClick,
            modifier = Modifier.weight(1f)
        )
        TripPointInteractiveCard(
            title = stringResource(id = designR.string.budget_reports),
            subtitle = stringResource(id = designR.string.budget_export_data),
            onClick = onReportsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryBreakdownHeader(onViewAllExpenses: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = designR.string.budget_category_breakdown),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onViewAllExpenses) {
            Text(
                text = stringResource(id = designR.string.budget_view_all), 
                style = MaterialTheme.typography.labelLarge
            )
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
                    text = stringResource(id = designR.string.budget_spent_prefix, category.spentAmount.toString()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(id = designR.string.budget_limit_prefix, category.allocatedAmount.toString()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TripPointLinearProgress(progress = category.progress)
        }
    }
}
