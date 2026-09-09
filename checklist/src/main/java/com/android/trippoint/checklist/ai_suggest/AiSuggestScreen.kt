package com.android.trippoint.checklist.ai_suggest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.ButtonVariant
import com.android.trippoint.core.designsystem.components.ErrorView
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AiSuggestRoute(
    tripId: String,
    viewModel: AiSuggestViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(AiSuggestContract.Intent.LoadSuggestions(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AiSuggestContract.Effect.NavigateBack -> onNavigateBack()
                AiSuggestContract.Effect.ItemsAdded -> onNavigateBack()
                is AiSuggestContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    AiSuggestScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun AiSuggestScreen(
    uiState: AiSuggestContract.State,
    onIntent: (AiSuggestContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.checklist_ai_suggest_title),
                onNavClick = { onIntent(AiSuggestContract.Intent.BackClicked) }
            )
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
                    onActionClick = { onIntent(AiSuggestContract.Intent.RegenerateClicked) }
                )
            }
            else -> {
                AiSuggestContent(uiState, onIntent, modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
private fun AiSuggestContent(
    uiState: AiSuggestContract.State,
    onIntent: (AiSuggestContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(id = designR.string.checklist_ai_suggest_subtitle, uiState.location, uiState.month),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = stringResource(id = designR.string.checklist_ai_suggest_intro),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(uiState.suggestions) { item ->
                SuggestedItemRow(item) {
                    onIntent(AiSuggestContract.Intent.ItemToggled(item.id))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TripPointButton(
            text = stringResource(id = designR.string.checklist_ai_add_all),
            onClick = { onIntent(AiSuggestContract.Intent.AddAllClicked) },
            isLoading = uiState.isAdding,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TripPointButton(
            text = stringResource(id = designR.string.checklist_ai_regenerate),
            onClick = { onIntent(AiSuggestContract.Intent.RegenerateClicked) },
            variant = ButtonVariant.Secondary,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SuggestedItemRow(
    item: AiSuggestContract.SuggestedItem,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
