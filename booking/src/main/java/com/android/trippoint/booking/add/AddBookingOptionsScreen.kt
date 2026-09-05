package com.android.trippoint.booking.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.TripPointFileUpload
import com.android.trippoint.core.designsystem.components.TripPointListItem
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddBookingOptionsRoute(
    tripId: String,
    viewModel: AddBookingOptionsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToManualEntry: (String) -> Unit,
    onNavigateToPnrEntry: (String) -> Unit,
    onNavigateToScanTicket: (String) -> Unit,
    onNavigateToImportEmail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.setTripId(tripId)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddBookingOptionsContract.Effect.NavigateBack -> onNavigateBack()
                is AddBookingOptionsContract.Effect.NavigateToManualEntry -> onNavigateToManualEntry(effect.tripId)
                is AddBookingOptionsContract.Effect.NavigateToPnrEntry -> onNavigateToPnrEntry(effect.tripId)
                is AddBookingOptionsContract.Effect.NavigateToScanTicket -> onNavigateToScanTicket(effect.tripId)
                is AddBookingOptionsContract.Effect.NavigateToImportEmail -> onNavigateToImportEmail(effect.tripId)
            }
        }
    }

    AddBookingOptionsScreen(
        onIntent = viewModel::onIntent
    )
}

@Composable
fun AddBookingOptionsScreen(
    onIntent: (AddBookingOptionsContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.bookings_add_title),
                onNavClick = { onIntent(AddBookingOptionsContract.Intent.BackClicked) }
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
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointListItem(
                title = stringResource(id = designR.string.bookings_add_import_email),
                subtitle = stringResource(id = designR.string.bookings_add_import_email_desc),
                leadingIcon = Icons.Outlined.Email,
                onClick = { onIntent(AddBookingOptionsContract.Intent.ImportEmailClicked) }
            )
            
            TripPointListItem(
                title = stringResource(id = designR.string.bookings_add_pnr),
                subtitle = stringResource(id = designR.string.bookings_add_pnr_desc),
                leadingIcon = Icons.Outlined.StickyNote2,
                onClick = { onIntent(AddBookingOptionsContract.Intent.PnrReferenceClicked) }
            )
            
            TripPointListItem(
                title = stringResource(id = designR.string.bookings_add_scan),
                subtitle = stringResource(id = designR.string.bookings_add_scan_desc),
                leadingIcon = Icons.Outlined.QrCodeScanner,
                onClick = { onIntent(AddBookingOptionsContract.Intent.ScanTicketClicked) }
            )
            
            TripPointListItem(
                title = stringResource(id = designR.string.bookings_add_manual),
                subtitle = stringResource(id = designR.string.bookings_add_manual_desc),
                leadingIcon = com.android.trippoint.core.designsystem.theme.TripPointIcons.Edit,
                onClick = { onIntent(AddBookingOptionsContract.Intent.ManualEntryClicked) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TripPointFileUpload(
                onUploadClick = { onIntent(AddBookingOptionsContract.Intent.FileUploadClicked) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
