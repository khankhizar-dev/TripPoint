package com.android.trippoint.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ContactSupport
import androidx.compose.material.icons.automirrored.outlined.HelpCenter
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.core.designsystem.components.SettingsListItem

@Composable
fun SupportRoute(
    onNavigateBack: () -> Unit
) {
    val viewModel: SupportViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    SupportScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun SupportScreen(
    uiState: SupportContract.State,
    onIntent: (SupportContract.Intent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Support & Help",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                SettingsListItem(
                    title = "Help Center",
                    icon = Icons.AutoMirrored.Outlined.HelpCenter,
                    onClick = { onIntent(SupportContract.Intent.HelpCenterClicked) }
                )
                SettingsListItem(
                    title = "FAQs",
                    icon = Icons.Outlined.QuestionAnswer,
                    onClick = { onIntent(SupportContract.Intent.FaqClicked) }
                )
                SettingsListItem(
                    title = "Contact Us",
                    icon = Icons.AutoMirrored.Outlined.ContactSupport,
                    onClick = { onIntent(SupportContract.Intent.ContactUsClicked) }
                )
                SettingsListItem(
                    title = "Report an issue",
                    icon = Icons.Outlined.Report,
                    onClick = { onIntent(SupportContract.Intent.ReportIssueClicked) }
                )
                SettingsListItem(
                    title = "Feedback",
                    icon = Icons.Outlined.Feedback,
                    onClick = { onIntent(SupportContract.Intent.FeedbackClicked) }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Need Help Banner (Mock F)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0F2FF) // Light Blue background
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Need help?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "We're here for you!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Placeholder for the illustration in Mock F
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SupportAgent,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
