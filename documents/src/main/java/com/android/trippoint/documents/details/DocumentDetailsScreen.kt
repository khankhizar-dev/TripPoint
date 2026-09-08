package com.android.trippoint.documents.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.AlertVariant
import com.android.trippoint.core.designsystem.components.IconButtonVariant
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointAlert
import com.android.trippoint.core.designsystem.components.TripPointChip
import com.android.trippoint.core.designsystem.components.TripPointIconButton
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.documents.domain.model.Document
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DocumentDetailsRoute(
    tripId: String,
    documentId: String,
    viewModel: DocumentDetailsViewModel,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(tripId, documentId) {
        viewModel.onIntent(DocumentDetailsContract.Intent.LoadDocument(tripId, documentId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                DocumentDetailsContract.Effect.NavigateBack -> onNavigateBack()
                is DocumentDetailsContract.Effect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    DocumentDetailsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun DocumentDetailsScreen(
    uiState: DocumentDetailsContract.State,
    onIntent: (DocumentDetailsContract.Intent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = uiState.document?.title ?: "Document Details",
                onNavClick = { onIntent(DocumentDetailsContract.Intent.BackClicked) }
            )
        },
        bottomBar = {
            uiState.document?.let { document ->
                DocumentDetailsBottomBar(
                    isFavorite = document.isFavorite,
                    onIntent = onIntent
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                TripPointAlert(message = uiState.error, variant = AlertVariant.Error)
            }
        } else {
            uiState.document?.let { document ->
                DocumentDetailsContent(document, innerPadding)
            }
        }
    }
}

@Composable
private fun DocumentDetailsContent(document: Document, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // PDF Icon Card
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "${document.title}.${document.fileExtension ?: "pdf"}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "${document.fileSize ?: "0.0 MB"} • Added on ${document.createdAt}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Tags
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TripPointChip(text = document.type.name, isSelected = true, onClick = {})
            Spacer(modifier = Modifier.width(8.dp))
            TripPointChip(text = "India", isSelected = false, onClick = {}) // Mock region
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Details Cards
        TripPointInfoCard(title = "Expiry Date") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = document.expiryDate ?: "Never",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "(3 years left)", // Mock logic
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TripPointInfoCard(title = "Document Number") {
            Text(
                text = document.referenceNumber ?: "N/A", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TripPointInfoCard(title = "Issued By") {
            Text(text = "Govt. of India", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DocumentDetailsBottomBar(isFavorite: Boolean, onIntent: (DocumentDetailsContract.Intent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TripPointIconButton(
            icon = Icons.Default.Share,
            onClick = { onIntent(DocumentDetailsContract.Intent.ShareClicked) }
        )
        TripPointIconButton(
            icon = Icons.Default.Download,
            onClick = { onIntent(DocumentDetailsContract.Intent.DownloadClicked) }
        )
        TripPointIconButton(
            icon = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
            onClick = { onIntent(DocumentDetailsContract.Intent.FavoriteClicked) }
        )
        TripPointIconButton(
            icon = Icons.Default.Delete,
            onClick = { onIntent(DocumentDetailsContract.Intent.DeleteClicked) },
            variant = IconButtonVariant.Destructive
        )
    }
}
