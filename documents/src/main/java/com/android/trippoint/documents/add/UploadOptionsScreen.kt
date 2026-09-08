package com.android.trippoint.documents.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.TripPointInteractiveCard
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import java.io.File

@Composable
fun UploadOptionsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToManual: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // For Take Photo (full resolution)
    val tempFile = remember { 
        File(context.cacheDir, "temp_doc_${System.currentTimeMillis()}.jpg") 
    }
    val tempUri = remember {
        androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Document captured to tempUri, navigate to details/manual add
            onNavigateToManual()
        }
    }

    // For Cloud/File import
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            // Handle cloud/file Uri
            onNavigateToManual()
        }
    }

    UploadOptionsScreen(
        onBackClick = onNavigateBack,
        onScanClick = onNavigateToScan,
        onUploadClick = { filePickerLauncher.launch("*/*") },
        onPhotoClick = { takePhotoLauncher.launch(tempUri) },
        onCloudClick = {
            filePickerLauncher.launch("*/*") // Cloud providers are integrated in system picker
        }
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
                title = stringResource(id = designR.string.documents_scan_title),
                subtitle = stringResource(id = designR.string.documents_scan_instructions),
                onClick = onScanClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointInteractiveCard(
                title = stringResource(id = designR.string.documents_add_title),
                subtitle = stringResource(id = designR.string.documents_upload_label),
                onClick = onUploadClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointInteractiveCard(
                title = stringResource(id = designR.string.documents_take_photo_title),
                subtitle = stringResource(id = designR.string.documents_take_photo_desc),
                onClick = onPhotoClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointInteractiveCard(
                title = stringResource(id = designR.string.documents_import_cloud_title),
                subtitle = stringResource(id = designR.string.documents_import_cloud_desc),
                onClick = onCloudClick
            )
        }
    }
}
