package com.android.trippoint.documents.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ScanDocumentRoute(
    tripId: String,
    viewModel: ScanDocumentViewModel,
    onNavigateBack: () -> Unit,
    onDocumentCaptured: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(ScanDocumentContract.Intent.LoadTripId(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ScanDocumentContract.Effect.NavigateBack -> onNavigateBack()
                is ScanDocumentContract.Effect.DocumentCaptured -> {
                    onDocumentCaptured(effect.uri)
                }
            }
        }
    }

    ScanDocumentScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ScanDocumentScreen(
    uiState: ScanDocumentContract.State,
    onIntent: (ScanDocumentContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            ScanTopBar(
                isFlashEnabled = uiState.isFlashEnabled,
                onBackClick = { onIntent(ScanDocumentContract.Intent.BackClicked) },
                onFlashClick = { onIntent(ScanDocumentContract.Intent.FlashToggled) }
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = stringResource(id = designR.string.documents_scan_instructions),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            DocumentScannerFrame(isProcessing = uiState.isProcessing)

            Spacer(modifier = Modifier.weight(1f))

            ScanControls(
                isAutoCapture = uiState.isAutoCaptureEnabled,
                onCaptureClick = { onIntent(ScanDocumentContract.Intent.CaptureClicked) },
                onAutoCaptureToggle = { onIntent(ScanDocumentContract.Intent.AutoCaptureToggled(it)) }
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun ScanTopBar(
    isFlashEnabled: Boolean,
    onBackClick: () -> Unit,
    onFlashClick: () -> Unit
) {
    TripPointTopAppBar(
        title = stringResource(id = designR.string.documents_scan_title),
        onNavClick = onBackClick,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        actions = {
            IconButton(onClick = onFlashClick) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Flash",
                    tint = if (isFlashEnabled) Color.Yellow else Color.White
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
private fun DocumentScannerFrame(isProcessing: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .aspectRatio(0.7f)
            .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isProcessing) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        
        // Scan Corners
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // High fidelity corners would be drawn here
        }
    }
}

@Composable
private fun ScanControls(
    isAutoCapture: Boolean,
    onCaptureClick: () -> Unit,
    onAutoCaptureToggle: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Checkbox(
                checked = isAutoCapture,
                onCheckedChange = onAutoCaptureToggle,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = Color.White
                )
            )
            Text(
                text = stringResource(id = designR.string.documents_scan_auto_capture),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .padding(4.dp)
        ) {
            Button(
                onClick = onCaptureClick,
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) { }
        }
    }
}
