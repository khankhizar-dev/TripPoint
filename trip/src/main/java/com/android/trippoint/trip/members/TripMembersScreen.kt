package com.android.trippoint.trip.members

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.common.model.TravelerRole
import com.android.trippoint.core.common.model.TripMember
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR

@Composable
fun TripMembersRoute(
    tripId: String,
    viewModel: TripMembersViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToInvite: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(TripMembersContract.Intent.LoadMembers(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TripMembersContract.Effect.NavigateBack -> onNavigateBack()
                is TripMembersContract.Effect.NavigateToInvite -> onNavigateToInvite(effect.tripId)
                is TripMembersContract.Effect.ShowError -> { /* Handle */ }
            }
        }
    }

    TripMembersScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun TripMembersScreen(
    uiState: TripMembersContract.State,
    onIntent: (TripMembersContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.trip_members_title),
                onNavClick = { onIntent(TripMembersContract.Intent.BackClicked) }
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp)) {
                TripPointButton(
                    text = stringResource(id = designR.string.invite_people_title),
                    onClick = { onIntent(TripMembersContract.Intent.InviteClicked) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TripPointTextField(
                value = uiState.searchQuery,
                onValueChange = { onIntent(TripMembersContract.Intent.SearchMembers(it)) },
                label = "",
                placeholder = stringResource(id = designR.string.trip_members_search_placeholder),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            } else {
                Text(
                    text = stringResource(id = designR.string.trip_members_count, uiState.members.size),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.filteredMembers) { member ->
                        MemberItem(member, onIntent)
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberItem(
    member: TripMember,
    onIntent: (TripMembersContract.Intent) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Member photo would go here
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.userName ?: stringResource(id = designR.string.trip_members_unknown),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            val roleLabel = when (member.role) {
                TravelerRole.ORGANIZER -> "Organiser"
                TravelerRole.EDITOR -> "Editor"
                TravelerRole.VIEWER -> "Viewer"
            }
            Text(
                text = roleLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(id = designR.string.trip_members_remove)) },
                    onClick = {
                        onIntent(TripMembersContract.Intent.RemoveMember(member.userId))
                        showMenu = false
                    }
                )
            }
        }
    }
}
