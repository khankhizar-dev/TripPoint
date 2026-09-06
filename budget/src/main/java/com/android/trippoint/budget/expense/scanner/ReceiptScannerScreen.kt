package com.android.trippoint.budget.expense.scanner

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FlashOn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ReceiptScannerRoute(
    budgetId: String,
    viewModel: ReceiptScannerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToConfirm: (ReceiptScannerContract.ScannedReceipt) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(budgetId) {
        viewModel.onIntent(ReceiptScannerContract.Intent.LoadBudgetId(budgetId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ReceiptScannerContract.Effect.NavigateBack -> onNavigateBack()
                is ReceiptScannerContract.Effect.NavigateToConfirmExpense -> {
                    onNavigateToConfirm(effect.receipt)
                }
            }
        }
    }

    ReceiptScannerScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ReceiptScannerScreen(
    uiState: ReceiptScannerContract.State,
    onIntent: (ReceiptScannerContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.budget_scanner_title),
                onNavClick = { onIntent(ReceiptScannerContract.Intent.BackClicked) },
                actions = {
                    IconButton(onClick = { /* Toggle Flash */ }) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Flash",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = designR.string.budget_scanner_instructions),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            ScannerFrame(isProcessing = uiState.isProcessing)

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isProcessing) {
                ProcessingOverlay()
            } else {
                TripPointButton(
                    text = stringResource(id = designR.string.budget_scanner_capture),
                    onClick = { onIntent(ReceiptScannerContract.Intent.CaptureClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = Icons.Default.Camera
                )
            }
        }
    }
}

@Composable
private fun ScannerFrame(isProcessing: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.8f))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        // High-fidelity corner markers
        ScannerCorners()

        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.02f)
                    .align(Alignment.TopCenter)
                    .offset(y = (scanLineY * 450).dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
private fun ScannerCorners() {
    // This would be simplified for now, but following design system patterns
    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // Corner logic...
    }
}

@Composable
private fun ProcessingOverlay() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = designR.string.budget_scanner_processing),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
