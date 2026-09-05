package com.android.trippoint.booking.details

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
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddTravellerRoute(
    tripId: String,
    bookingId: String,
    viewModel: AddTravellerViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, bookingId) {
        viewModel.onIntent(AddTravellerContract.Intent.LoadIds(tripId, bookingId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddTravellerContract.Effect.NavigateBack -> onNavigateBack()
                AddTravellerContract.Effect.TravellerAdded -> onNavigateBack()
            }
        }
    }

    AddTravellerScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun AddTravellerScreen(
    uiState: AddTravellerContract.State,
    onIntent: (AddTravellerContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.bookings_traveller_add_title),
                onNavClick = { onIntent(AddTravellerContract.Intent.BackClicked) }
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
            if (uiState.error != null) {
                TripPointAlert(
                    message = uiState.error,
                    variant = AlertVariant.Error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TravellerNameSection(uiState, onIntent)

            TravellerContactSection(uiState, onIntent)

            TravellerDocumentSection(uiState, onIntent)

            Spacer(modifier = Modifier.height(32.dp))

            TripPointButton(
                text = stringResource(id = designR.string.bookings_traveller_save),
                onClick = { onIntent(AddTravellerContract.Intent.SaveClicked) },
                isLoading = uiState.isLoading,
                enabled = uiState.firstName.isNotBlank() && uiState.lastName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TravellerNameSection(
    uiState: AddTravellerContract.State,
    onIntent: (AddTravellerContract.Intent) -> Unit
) {
    TripPointTextField(
        value = uiState.firstName,
        onValueChange = { onIntent(AddTravellerContract.Intent.FirstNameChanged(it)) },
        label = stringResource(id = designR.string.bookings_traveller_first_name),
        placeholder = stringResource(id = designR.string.bookings_traveller_placeholder_first)
    )

    Spacer(modifier = Modifier.height(16.dp))

    TripPointTextField(
        value = uiState.lastName,
        onValueChange = { onIntent(AddTravellerContract.Intent.LastNameChanged(it)) },
        label = stringResource(id = designR.string.bookings_traveller_last_name),
        placeholder = stringResource(id = designR.string.bookings_traveller_placeholder_last)
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun TravellerContactSection(
    uiState: AddTravellerContract.State,
    onIntent: (AddTravellerContract.Intent) -> Unit
) {
    TripPointTextField(
        value = uiState.email,
        onValueChange = { onIntent(AddTravellerContract.Intent.EmailChanged(it)) },
        label = stringResource(id = designR.string.bookings_traveller_email),
        placeholder = "john.doe@example.com"
    )

    Spacer(modifier = Modifier.height(16.dp))

    TripPointTextField(
        value = uiState.phone,
        onValueChange = { onIntent(AddTravellerContract.Intent.PhoneChanged(it)) },
        label = stringResource(id = designR.string.bookings_traveller_phone),
        placeholder = "+1 234 567 890"
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun TravellerDocumentSection(
    uiState: AddTravellerContract.State,
    onIntent: (AddTravellerContract.Intent) -> Unit
) {
    TripPointTextField(
        value = uiState.dob,
        onValueChange = { onIntent(AddTravellerContract.Intent.DobChanged(it)) },
        label = stringResource(id = designR.string.bookings_traveller_dob),
        placeholder = "YYYY-MM-DD"
    )

    Spacer(modifier = Modifier.height(16.dp))

    TripPointTextField(
        value = uiState.ticket,
        onValueChange = { onIntent(AddTravellerContract.Intent.TicketChanged(it)) },
        label = stringResource(id = designR.string.bookings_traveller_ticket),
        placeholder = "Ticket or Confirmation No."
    )

    Spacer(modifier = Modifier.height(16.dp))

    TripPointTextField(
        value = uiState.seat,
        onValueChange = { onIntent(AddTravellerContract.Intent.SeatChanged(it)) },
        label = stringResource(id = designR.string.bookings_traveller_seat),
        placeholder = "e.g. 12A"
    )
}
