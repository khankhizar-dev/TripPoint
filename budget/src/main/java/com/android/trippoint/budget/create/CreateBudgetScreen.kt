package com.android.trippoint.budget.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun CreateBudgetRoute(
    tripId: String,
    viewModel: CreateBudgetViewModel,
    onNavigateBack: () -> Unit,
    onBudgetCreated: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(CreateBudgetContract.Intent.LoadTripId(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                CreateBudgetContract.Effect.NavigateBack -> onNavigateBack()
                is CreateBudgetContract.Effect.BudgetCreated -> onBudgetCreated(effect.budget.id)
            }
        }
    }

    CreateBudgetScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun CreateBudgetScreen(
    uiState: CreateBudgetContract.State,
    onIntent: (CreateBudgetContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.budget_create_title),
                onNavClick = { onIntent(CreateBudgetContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.error != null) {
                TripPointAlert(message = uiState.error, variant = AlertVariant.Error)
                Spacer(modifier = Modifier.height(16.dp))
            }

            TripPointTextField(
                value = uiState.amount,
                onValueChange = { onIntent(CreateBudgetContract.Intent.AmountChanged(it)) },
                label = stringResource(id = designR.string.budget_limit_label),
                placeholder = "0.00",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripPointDropdown(
                value = uiState.currency,
                onValueChange = { onIntent(CreateBudgetContract.Intent.CurrencyChanged(it)) },
                label = "Currency",
                options = listOf("INR", "USD", "EUR", "GBP", "AED"),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            TripPointButton(
                text = "Save Budget",
                onClick = { onIntent(CreateBudgetContract.Intent.SaveClicked) },
                isLoading = uiState.isLoading,
                enabled = uiState.amount.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
