package com.android.trippoint.trip.invite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun InvitePeopleRoute(
    tripId: String,
    viewModel: InvitePeopleViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSummary: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(InvitePeopleContract.Intent.LoadTrip(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is InvitePeopleContract.Effect.NavigateToSummary -> onNavigateToSummary(effect.tripId)
                InvitePeopleContract.Effect.NavigateBack -> onNavigateBack()
                is InvitePeopleContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    InvitePeopleScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitePeopleScreen(
    uiState: InvitePeopleContract.State,
    onIntent: (InvitePeopleContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = designR.string.invite_people_title)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(InvitePeopleContract.Intent.BackClicked) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            TripPointTextField(
                value = uiState.manualInput,
                onValueChange = { onIntent(InvitePeopleContract.Intent.ManualInputChanged(it)) },
                label = stringResource(id = designR.string.invite_people_manual_label),
                placeholder = stringResource(id = designR.string.invite_people_manual_placeholder),
                trailingIcon = {
                    TextButton(onClick = { onIntent(InvitePeopleContract.Intent.ManualInviteClicked) }) {
                        Text(stringResource(id = designR.string.invite_people_button))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(id = designR.string.invite_people_contacts_title), 
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointTextField(
                value = uiState.searchQuery,
                onValueChange = { onIntent(InvitePeopleContract.Intent.SearchQueryChanged(it)) },
                label = "",
                placeholder = stringResource(id = designR.string.invite_people_search_placeholder),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.people) { person ->
                    PersonItem(
                        person = person,
                        onInvite = { onIntent(InvitePeopleContract.Intent.InviteClicked(person.id)) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { onIntent(InvitePeopleContract.Intent.InviteViaLink) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Link, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = designR.string.invite_people_via_link))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TripPointButton(
                text = stringResource(id = designR.string.create_trip_next_button),
                onClick = { onIntent(InvitePeopleContract.Intent.NextClicked) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PersonItem(
    person: InvitePeopleContract.Person,
    onInvite: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                if (person.photoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = person.photoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = person.name, style = MaterialTheme.typography.bodyLarge)
        }
        
        Button(
            onClick = onInvite,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (person.isInvited) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                },
                contentColor = if (person.isInvited) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            )
        ) {
            Text(
                text = if (person.isInvited) stringResource(id = designR.string.invite_people_invited) 
                       else stringResource(id = designR.string.invite_people_button)
            )
        }
    }
}
