package com.android.trippoint.checklist.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.trippoint.checklist.domain.model.Checklist
import com.android.trippoint.checklist.domain.model.ChecklistSection
import com.android.trippoint.core.designsystem.components.ErrorView
import com.android.trippoint.core.designsystem.components.FullscreenStatusView
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointCircularProgress
import com.android.trippoint.core.designsystem.components.TripPointEmptyState
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.R as designR

@Suppress("LongParameterList")
@Composable
fun ChecklistDetailsRoute(
    tripId: String,
    id: String,
    viewModel: ChecklistDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSection: (String, String, String) -> Unit,
    onNavigateToProgress: (String, String) -> Unit,
    onNavigateToAddSection: () -> Unit,
    onNavigateToAddItem: (String, String, String) -> Unit,
    onNavigateToAiSuggest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId, id) {
        viewModel.onIntent(ChecklistDetailsContract.Intent.LoadChecklist(tripId, id))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ChecklistDetailsContract.Effect.NavigateBack -> onNavigateBack()
                is ChecklistDetailsContract.Effect.NavigateToSectionDetails -> {
                    onNavigateToSection(effect.tripId, effect.checklistId, effect.id)
                }
                is ChecklistDetailsContract.Effect.NavigateToProgress -> {
                    onNavigateToProgress(effect.tripId, effect.checklistId)
                }
                ChecklistDetailsContract.Effect.NavigateToAddSection -> onNavigateToAddSection()
                is ChecklistDetailsContract.Effect.NavigateToAddItem -> {
                    onNavigateToAddItem(effect.tripId, effect.checklistId, effect.sectionId)
                }
                ChecklistDetailsContract.Effect.NavigateToAiSuggest -> onNavigateToAiSuggest()
            }
        }
    }

    ChecklistDetailsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistDetailsScreen(
    uiState: ChecklistDetailsContract.State,
    onIntent: (ChecklistDetailsContract.Intent) -> Unit
) {
    val checklist = uiState.checklist
    val isCompleted = checklist?.progress == 1f && uiState.sections.isNotEmpty()
    var showAddSectionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val title = checklist?.title 
                        ?: stringResource(id = designR.string.checklist_details_title_default)
                    Text(text = title) 
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(ChecklistDetailsContract.Intent.BackClicked) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(ChecklistDetailsContract.Intent.AiSuggestClicked) }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome, 
                            contentDescription = stringResource(id = designR.string.checklist_ai_suggest)
                        )
                    }
                    IconButton(onClick = { /* More */ }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (!isCompleted && !uiState.isLoading && uiState.sections.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onIntent(ChecklistDetailsContract.Intent.AddItemClicked) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(id = designR.string.checklist_add_item)
                    )
                }
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            uiState.error != null -> {
                ErrorView(
                    title = stringResource(id = designR.string.checklist_error_title),
                    description = uiState.error,
                    icon = Icons.Default.Error,
                    actionText = stringResource(id = designR.string.core_designsystem_retry),
                    onActionClick = { onIntent(ChecklistDetailsContract.Intent.RetryClicked) }
                )
            }
            isCompleted -> {
                ChecklistCompletedState(
                    onViewSummary = { onIntent(ChecklistDetailsContract.Intent.ProgressClicked) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                ChecklistDetailsContent(uiState, onIntent, innerPadding) {
                    showAddSectionDialog = true
                }
            }
        }

        if (showAddSectionDialog) {
            AddSectionDialog(
                onDismiss = { showAddSectionDialog = false },
                onConfirm = { name ->
                    onIntent(ChecklistDetailsContract.Intent.AddSection(name))
                    showAddSectionDialog = false
                }
            )
        }
    }
}

@Composable
private fun AddSectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add New Section") },
        text = {
            TripPointTextField(
                value = name,
                onValueChange = { name = it },
                label = "Section Name",
                placeholder = "e.g. Toiletries"
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
private fun ChecklistCompletedState(onViewSummary: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        FullscreenStatusView(
            title = stringResource(id = designR.string.checklist_completed_title),
            subtitle = stringResource(id = designR.string.checklist_completed_desc),
            imageResId = designR.drawable.illustration_success,
            actionText = stringResource(id = designR.string.checklist_view_summary),
            onActionClick = onViewSummary,
            includeBackground = false
        )
    }
}

@Composable
private fun ChecklistDetailsContent(
    uiState: ChecklistDetailsContract.State,
    onIntent: (ChecklistDetailsContract.Intent) -> Unit,
    innerPadding: PaddingValues,
    onAddSectionClick: () -> Unit
) {
    val checklist = uiState.checklist ?: return
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            ChecklistHeader(checklist, onIntent)
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        if (uiState.sections.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TripPointEmptyState(
                        title = "No sections yet",
                        subtitle = "Add your first section to start adding items.",
                        imageResId = designR.drawable.illustration_empty_trip,
                        actionText = "Add Section",
                        onActionClick = onAddSectionClick
                    )
                }
            }
        } else {
            items(uiState.sections) { section ->
                ChecklistSectionItem(section) {
                    onIntent(ChecklistDetailsContract.Intent.SectionClicked(section.id))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                TextButton(
                    onClick = onAddSectionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(id = designR.string.checklist_add_section))
                }
            }
        }
    }
}

@Composable
private fun ChecklistHeader(
    checklist: Checklist,
    onIntent: (ChecklistDetailsContract.Intent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1552465011-b4e21bf6e79a",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                text = checklist.title,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = checklist.dateRange,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            TripPointCircularProgress(
                progress = checklist.progress,
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onIntent(ChecklistDetailsContract.Intent.ProgressClicked) }
            )
        }
    }
}

@Composable
private fun ChecklistSectionItem(
    section: ChecklistSection,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${section.completedItems}/${section.totalItems}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val progress = if (section.totalItems > 0) {
                    section.completedItems.toFloat() / section.totalItems 
                } else 0f
                TripPointCircularProgress(
                    progress = progress,
                    showPercentage = false,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
