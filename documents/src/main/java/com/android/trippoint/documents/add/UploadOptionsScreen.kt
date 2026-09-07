package com.android.trippoint.documents.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.TripPointInteractiveCard
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR

@Composable
fun UploadOptionsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToManual: () -> Unit
) {
    UploadOptionsScreen(
        onBackClick = onNavigateBack,
        onScanClick = onNavigateToScan,
        onUploadClick = onNavigateToManual, // For now, navigate to manual add
        onPhotoClick = { /* Handle */ },
        onCloudClick = { /* Handle */ }
    )
}

@Composable
fun UploadOptionsScreen(
    onBackClick: () -> Unit,
    onScanClick: () -> Unit,
    onUploadClick: () -> Unit,
    onPhotoClick: () -> Unit,
    onCloudClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.documents_add_title),
                onNavClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointInteractiveCard(
                title = "Scan Document",
                subtitle = "Use camera to scan",
                onClick = onScanClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointInteractiveCard(
                title = "Upload from Device",
                subtitle = "Choose file from device",
                onClick = onUploadClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointInteractiveCard(
                title = "Take Photo",
                subtitle = "Capture document",
                onClick = onPhotoClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointInteractiveCard(
                title = "Import from Cloud",
                subtitle = "Google Drive, Dropbox etc.",
                onClick = onCloudClick
            )
        }
    }
}
