package com.android.trippoint.checklist.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointCircularProgress
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChecklistProgressRoute(
    tripId: String,
    checklistId: String,
    viewModel: ChecklistProgressViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCompleted: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, checklistId) {
        viewModel.onIntent(ChecklistProgressContract.Intent.LoadProgress(tripId, checklistId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ChecklistProgressContract.Effect.NavigateBack -> onNavigateBack()
                is ChecklistProgressContract.Effect.NavigateToCompleted -> {
                    onNavigateToCompleted(effect.tripId, effect.checklistId)
                }
            }
        }
    }

    ChecklistProgressScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ChecklistProgressScreen(
    uiState: ChecklistProgressContract.State,
    onIntent: (ChecklistProgressContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.checklist_progress_title),
                onNavClick = { onIntent(ChecklistProgressContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else {
            ChecklistProgressContent(uiState, onIntent, innerPadding)
        }
    }
}

@Composable
private fun ChecklistProgressContent(
    uiState: ChecklistProgressContract.State,
    onIntent: (ChecklistProgressContract.Intent) -> Unit,
    innerPadding: PaddingValues
) {
    val checklist = uiState.checklist ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointCircularProgress(
                progress = checklist.progress,
                modifier = Modifier.size(160.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(id = designR.string.checklist_progress_good_job),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Sections",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        items(uiState.sections) { section ->
            SectionProgressItem(section)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
            
            TripPointButton(
                text = stringResource(id = designR.string.checklist_view_completed),
                onClick = { onIntent(ChecklistProgressContract.Intent.ViewCompletedClicked) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionProgressItem(section: ChecklistSection) {
    val progress = if (section.totalItems > 0) section.completedItems.toFloat() / section.totalItems else 0f
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (progress >= 1f) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.outlineVariant,
                        shape = MaterialTheme.shapes.small
                    )
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = section.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        
        Text(
            text = "${section.completedItems}/${section.totalItems}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
