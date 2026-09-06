package com.android.trippoint.budget.reports

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BudgetReportsRoute(
    tripId: String,
    budgetId: String,
    viewModel: BudgetReportsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(tripId, budgetId) {
        viewModel.onIntent(BudgetReportsContract.Intent.LoadReports(tripId, budgetId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                BudgetReportsContract.Effect.NavigateBack -> onNavigateBack()
                is BudgetReportsContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    BudgetReportsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun BudgetReportsScreen(
    uiState: BudgetReportsContract.State,
    onIntent: (BudgetReportsContract.Intent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = "Budget Reports",
                onNavClick = { onIntent(BudgetReportsContract.Intent.BackClicked) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Export and share your trip financial data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            uiState.availableReports.forEach { reportTitle ->
                ReportOptionItem(
                    title = reportTitle,
                    onDownload = { onIntent(BudgetReportsContract.Intent.DownloadReport("PDF")) },
                    onShare = { onIntent(BudgetReportsContract.Intent.ShareReport("CSV")) },
                    isLoading = uiState.isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReportOptionItem(
    title: String,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    isLoading: Boolean
) {
    TripPointInfoCard(title = title) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Available in PDF and CSV",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            
            Row {
                IconButton(onClick = onDownload, enabled = !isLoading) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                }
                IconButton(onClick = onShare, enabled = !isLoading) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        }
    }
}
