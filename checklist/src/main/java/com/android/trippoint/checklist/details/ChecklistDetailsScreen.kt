package com.android.trippoint.checklist.details

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MoreVert
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
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointCircularProgress
import com.android.trippoint.core.designsystem.R as designR

@Composable
fun ChecklistDetailsRoute(
    id: String,
    viewModel: ChecklistDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSection: (String) -> Unit,
    onNavigateToAddSection: () -> Unit,
    onNavigateToAddItem: () -> Unit,
    onNavigateToAiSuggest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(id) {
        viewModel.onIntent(ChecklistDetailsContract.Intent.LoadChecklist(id))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ChecklistDetailsContract.Effect.NavigateBack -> onNavigateBack()
                is ChecklistDetailsContract.Effect.NavigateToSectionDetails -> {
                    onNavigateToSection(effect.id)
                }
                ChecklistDetailsContract.Effect.NavigateToAddSection -> onNavigateToAddSection()
                ChecklistDetailsContract.Effect.NavigateToAddItem -> onNavigateToAddItem()
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val title = uiState.checklist?.title 
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
            FloatingActionButton(
                onClick = { onIntent(ChecklistDetailsContract.Intent.AddItemClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = designR.string.checklist_add_item))
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else {
            ChecklistDetailsContent(uiState, onIntent, innerPadding)
        }
    }
}

@Composable
private fun ChecklistDetailsContent(
    uiState: ChecklistDetailsContract.State,
    onIntent: (ChecklistDetailsContract.Intent) -> Unit,
    innerPadding: PaddingValues
) {
    val checklist = uiState.checklist ?: return
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            ChecklistHeader(checklist)
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        items(uiState.sections) { section ->
            ChecklistSectionItem(section) {
                onIntent(ChecklistDetailsContract.Intent.SectionClicked(section.id))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            TextButton(
                onClick = { onIntent(ChecklistDetailsContract.Intent.AddSectionClicked) },
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

@Composable
private fun ChecklistHeader(checklist: Checklist) {
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
                modifier = Modifier.size(80.dp)
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
            
            TripPointCircularProgress(
                progress = if (section.totalItems > 0) section.completedItems.toFloat() / section.totalItems else 0f,
                showPercentage = false,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
