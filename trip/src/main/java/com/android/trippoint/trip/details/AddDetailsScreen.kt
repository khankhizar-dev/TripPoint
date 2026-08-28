package com.android.trippoint.trip.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddDetailsRoute(
    tripId: String,
    viewModel: AddDetailsViewModel,
    onNavigateBack: () -> Unit,
    onSaveAndContinue: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(AddDetailsContract.Intent.LoadTrip(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddDetailsContract.Effect.NavigateToHome -> onSaveAndContinue()
                AddDetailsContract.Effect.NavigateBack -> onNavigateBack()
                is AddDetailsContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    AddDetailsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDetailsScreen(
    uiState: AddDetailsContract.State,
    onIntent: (AddDetailsContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = designR.string.add_details_title)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddDetailsContract.Intent.BackClicked) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            uiState.sections.forEach { section ->
                DetailItem(
                    section = section,
                    onToggle = { onIntent(AddDetailsContract.Intent.ToggleSection(section.id)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))
            
            TripPointButton(
                text = stringResource(id = designR.string.add_details_save_button),
                onClick = { onIntent(AddDetailsContract.Intent.SaveAndContinueClicked) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DetailItem(
    section: AddDetailsContract.DetailSection,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = if (section.isAdded) Icons.Default.Check else Icons.Default.Add,
                contentDescription = null,
                tint = if (section.isAdded) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
