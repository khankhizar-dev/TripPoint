package com.android.trippoint.booking.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PnrIntakeRoute(
    tripId: String,
    viewModel: PnrIntakeViewModel,
    onNavigateBack: () -> Unit,
    onBookingAdded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(PnrIntakeContract.Intent.LoadTripId(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                PnrIntakeContract.Effect.NavigateBack -> onNavigateBack()
                PnrIntakeContract.Effect.BookingAdded -> onBookingAdded()
            }
        }
    }

    PnrIntakeScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun PnrIntakeScreen(
    uiState: PnrIntakeContract.State,
    onIntent: (PnrIntakeContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.bookings_add_pnr_title),
                onNavClick = { onIntent(PnrIntakeContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            if (uiState.error != null) {
                TripPointAlert(
                    message = uiState.error,
                    variant = AlertVariant.Error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            TripPointTextField(
                value = uiState.pnr,
                onValueChange = { onIntent(PnrIntakeContract.Intent.PnrChanged(it)) },
                label = stringResource(id = designR.string.bookings_add_pnr_label),
                placeholder = stringResource(id = designR.string.bookings_add_pnr_placeholder),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TripPointButton(
                text = stringResource(id = designR.string.bookings_add_pnr_fetch_button),
                onClick = { onIntent(PnrIntakeContract.Intent.FetchClicked) },
                enabled = uiState.pnr.isNotBlank(),
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
