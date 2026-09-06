package com.android.trippoint.budget.trends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointBarChart
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointSegmentedControl
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SpendingTrendsRoute(
    tripId: String,
    budgetId: String,
    viewModel: SpendingTrendsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, budgetId) {
        viewModel.onIntent(SpendingTrendsContract.Intent.LoadTrends(tripId, budgetId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SpendingTrendsContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    SpendingTrendsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun SpendingTrendsScreen(
    uiState: SpendingTrendsContract.State,
    onIntent: (SpendingTrendsContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "Spending Trends",
                onNavClick = { onIntent(SpendingTrendsContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text(
                text = "Overview of your spending habits",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TripPointSegmentedControl(
                options = uiState.periods,
                selectedIndex = uiState.selectedPeriodIndex,
                onOptionSelected = { onIntent(SpendingTrendsContract.Intent.PeriodSelected(uiState.periods[it])) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TripPointInfoCard(title = "Spending Graph") {
                TripPointBarChart(data = uiState.trendData)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointAlert(
                message = "Your spending peaked on Saturday due to activities.",
                variant = AlertVariant.Info
            )
        }
    }
}
