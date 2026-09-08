package com.android.trippoint.budget.settlement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.TripPointInfoCard
import com.android.trippoint.core.designsystem.components.TripPointTopAppBar
import com.android.trippoint.core.designsystem.R as designR
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettlementRoute(
    tripId: String,
    viewModel: SettlementViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.onIntent(SettlementContract.Intent.LoadSettlements(tripId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SettlementContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    SettlementScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun SettlementScreen(
    uiState: SettlementContract.State,
    onIntent: (SettlementContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TripPointTopAppBar(
                title = stringResource(id = designR.string.budget_settlements_title),
                onNavClick = { onIntent(SettlementContract.Intent.BackClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = stringResource(id = designR.string.budget_member_balances),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                uiState.summary?.members?.forEach { member ->
                    MemberBalanceCard(member)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = stringResource(id = designR.string.budget_settlement_plan),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                uiState.summary?.settlements?.forEach { settlement ->
                    SettlementPlanCard(settlement)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun MemberBalanceCard(member: com.android.trippoint.core.network.SettlementMemberDto) {
    TripPointInfoCard(title = "User ${member.userId.take(8)}") {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceItem(
                    label = stringResource(id = designR.string.budget_paid), 
                    value = member.paidAmount.toString(),
                    modifier = Modifier.weight(1f)
                )
                BalanceItem(
                    label = stringResource(id = designR.string.budget_share), 
                    value = member.shareAmount.toString(),
                    modifier = Modifier.weight(1f)
                )
                BalanceItem(
                    label = stringResource(id = designR.string.budget_balance), 
                    value = member.balance.toString(),
                    modifier = Modifier.weight(1f),
                    isBold = true
                )
            }
        }
    }
}

@Composable
private fun BalanceItem(label: String, value: String, modifier: Modifier = Modifier, isBold: Boolean = false) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(
            text = value, 
            style = if (isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun SettlementPlanCard(settlement: com.android.trippoint.core.network.SettlementActionDto) {
    TripPointInfoCard(title = "") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${settlement.fromUserId.take(8)} ${stringResource(id = designR.string.budget_owes)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stringResource(id = designR.string.budget_to)} ${settlement.toUserId.take(8)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = settlement.amount.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
