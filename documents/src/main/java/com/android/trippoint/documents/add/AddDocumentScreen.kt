package com.android.trippoint.documents.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.android.trippoint.core.designsystem.components.TripPointFileUpload
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.documents.domain.model.DocumentType
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddDocumentRoute(
    tripId: String,
    viewModel: AddDocumentViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(AddDocumentContract.Intent.LoadTripId(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddDocumentContract.Effect.NavigateBack -> onNavigateBack()
                AddDocumentContract.Effect.DocumentAdded -> onNavigateBack()
            }
        }
    }

    AddDocumentScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun AddDocumentScreen(
    uiState: AddDocumentContract.State,
    onIntent: (AddDocumentContract.Intent) -> Unit
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            onIntent(AddDocumentContract.Intent.FileSelected(it.toString()))
        }
    }

    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.documents_add_title),
                onNavClick = { onIntent(AddDocumentContract.Intent.BackClicked) }
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
            val errorMessage = uiState.error ?: uiState.errorResId?.let { stringResource(id = it) }
            if (errorMessage != null) {
                TripPointAlert(message = errorMessage, variant = AlertVariant.Error)
                Spacer(modifier = Modifier.height(16.dp))
            }

            TripPointFileUpload(
                onUploadClick = { filePickerLauncher.launch("*/*") },
                label = uiState.fileUrl ?: stringResource(id = designR.string.documents_upload_label)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TripPointTextField(
                value = uiState.title,
                onValueChange = { onIntent(AddDocumentContract.Intent.TitleChanged(it)) },
                label = stringResource(id = designR.string.documents_title_label),
                placeholder = stringResource(id = designR.string.documents_title_placeholder)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripPointDropdown(
                value = uiState.type.name,
                onValueChange = { onIntent(AddDocumentContract.Intent.TypeChanged(DocumentType.valueOf(it))) },
                label = stringResource(id = designR.string.documents_type_label),
                options = DocumentType.values().map { it.name }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripPointTextField(
                value = uiState.expiryDate,
                onValueChange = { onIntent(AddDocumentContract.Intent.ExpiryChanged(it)) },
                label = stringResource(id = designR.string.documents_expiry_label),
                placeholder = stringResource(id = designR.string.documents_expiry_placeholder)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripPointTextField(
                value = uiState.referenceNumber,
                onValueChange = { onIntent(AddDocumentContract.Intent.RefChanged(it)) },
                label = stringResource(id = designR.string.documents_ref_label),
                placeholder = stringResource(id = designR.string.documents_ref_placeholder)
            )

            Spacer(modifier = Modifier.height(32.dp))

            TripPointButton(
                text = stringResource(id = designR.string.documents_save_button),
                onClick = { onIntent(AddDocumentContract.Intent.SaveClicked) },
                enabled = uiState.title.isNotBlank() && uiState.fileUrl != null,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
