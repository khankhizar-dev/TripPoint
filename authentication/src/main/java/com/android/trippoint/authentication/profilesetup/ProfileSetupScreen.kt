package com.android.trippoint.authentication.profilesetup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.R
import com.android.trippoint.core.designsystem.components.FullscreenStatusView
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointDropdown
import com.android.trippoint.core.designsystem.components.TripPointTextField

@Composable
fun ProfileSetupRoute(
    onNavigateToHome: () -> Unit,
    viewModel: ProfileSetupViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileSetupContract.Effect.NavigateToHome -> onNavigateToHome()
                is ProfileSetupContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    ProfileSetupScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Suppress("LongMethod")
@Composable
fun ProfileSetupScreen(
    uiState: ProfileSetupContract.State,
    onIntent: (ProfileSetupContract.Intent) -> Unit
) {
    if (uiState.isSuccess) {
        FullscreenStatusView(
            title = stringResource(R.string.auth_profile_setup_success_title),
            subtitle = stringResource(R.string.auth_profile_setup_success_subtitle),
            imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_welcome,
            actionText = stringResource(R.string.auth_profile_setup_go_home),
            onActionClick = { onIntent(ProfileSetupContract.Intent.NextClicked) }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.currentStep != ProfileSetupContract.Step.PHOTO) {
                    IconButton(onClick = { onIntent(ProfileSetupContract.Intent.BackClicked) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                if (uiState.currentStep == ProfileSetupContract.Step.PHOTO) {
                    TextButton(onClick = { onIntent(ProfileSetupContract.Intent.SkipPhotoClicked) }) {
                        Text(text = stringResource(R.string.auth_profile_setup_skip))
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (uiState.currentStep) {
                    ProfileSetupContract.Step.PHOTO -> PhotoStep(uiState, onIntent)
                    ProfileSetupContract.Step.ABOUT -> AboutStep(uiState, onIntent)
                    ProfileSetupContract.Step.PREFERENCES -> PreferencesStep(uiState, onIntent)
                    ProfileSetupContract.Step.REVIEW -> ReviewStep(uiState, onIntent)
                }
            }

            // Bottom Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                TripPointButton(
                    text = if (uiState.currentStep == ProfileSetupContract.Step.REVIEW) {
                        stringResource(R.string.auth_profile_setup_save_continue)
                    } else {
                        stringResource(com.android.trippoint.authentication.R.string.auth_next)
                    },
                    onClick = { onIntent(ProfileSetupContract.Intent.NextClicked) },
                    enabled = !uiState.isLoading
                )
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun PhotoStep(
    uiState: ProfileSetupContract.State,
    onIntent: (ProfileSetupContract.Intent) -> Unit
) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        text = stringResource(R.string.auth_profile_setup_photo_title),
        style = MaterialTheme.typography.headlineLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.auth_profile_setup_photo_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(48.dp))
    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .clickable { /* Pick photo */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AboutStep(
    uiState: ProfileSetupContract.State,
    onIntent: (ProfileSetupContract.Intent) -> Unit
) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        text = stringResource(R.string.auth_profile_setup_about_title),
        style = MaterialTheme.typography.headlineLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.auth_profile_setup_about_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(48.dp))
    TripPointTextField(
        value = uiState.fullName,
        onValueChange = { onIntent(ProfileSetupContract.Intent.FullNameChanged(it)) },
        label = "Full Name",
        leadingIcon = { Icon(Icons.Default.Person, null) }
    )
    Spacer(modifier = Modifier.height(24.dp))
    TripPointTextField(
        value = uiState.username,
        onValueChange = { onIntent(ProfileSetupContract.Intent.UsernameChanged(it)) },
        label = "Username",
        leadingIcon = { Icon(Icons.Default.AlternateEmail, null) }
    )
}

@Composable
private fun PreferencesStep(
    uiState: ProfileSetupContract.State,
    onIntent: (ProfileSetupContract.Intent) -> Unit
) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        text = stringResource(R.string.auth_profile_setup_pref_title),
        style = MaterialTheme.typography.headlineLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.auth_profile_setup_pref_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(48.dp))
    
    TripPointDropdown(
        value = uiState.country,
        onValueChange = { onIntent(ProfileSetupContract.Intent.CountryChanged(it)) },
        label = "Country",
        options = listOf("United States", "United Kingdom", "India", "Canada", "Germany")
    )
    Spacer(modifier = Modifier.height(24.dp))
    TripPointDropdown(
        value = uiState.currency,
        onValueChange = { onIntent(ProfileSetupContract.Intent.CurrencyChanged(it)) },
        label = "Currency",
        options = listOf("USD ($)", "EUR (€)", "GBP (£)", "INR (₹)", "CAD ($)")
    )
    Spacer(modifier = Modifier.height(24.dp))
    TripPointDropdown(
        value = uiState.language,
        onValueChange = { onIntent(ProfileSetupContract.Intent.LanguageChanged(it)) },
        label = "Language",
        options = listOf("English", "Spanish", "French", "German", "Hindi")
    )
    Spacer(modifier = Modifier.height(24.dp))
    TripPointDropdown(
        value = uiState.timezone,
        onValueChange = { onIntent(ProfileSetupContract.Intent.TimezoneChanged(it)) },
        label = "Time Zone",
        options = listOf("UTC-05:00 (EST)", "UTC+00:00 (GMT)", "UTC+05:30 (IST)", "UTC+01:00 (CET)")
    )
}

@Composable
private fun ReviewStep(
    uiState: ProfileSetupContract.State,
    onIntent: (ProfileSetupContract.Intent) -> Unit
) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        text = stringResource(R.string.auth_profile_setup_review_title),
        style = MaterialTheme.typography.headlineLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.auth_profile_setup_review_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(48.dp))
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ReviewSection(
            title = "Personal Details",
            items = listOf("Full Name" to uiState.fullName, "Username" to uiState.username),
            onEditClick = {
                onIntent(
                    ProfileSetupContract.Intent.EditStepClicked(ProfileSetupContract.Step.ABOUT)
                )
            }
        )
        ReviewSection(
            title = "Preferences",
            items = listOf(
                "Country" to uiState.country,
                "Currency" to uiState.currency,
                "Language" to uiState.language,
                "Time Zone" to uiState.timezone
            ),
            onEditClick = {
                onIntent(
                    ProfileSetupContract.Intent.EditStepClicked(
                        ProfileSetupContract.Step.PREFERENCES
                    )
                )
            }
        )
    }
}

@Composable
private fun ReviewSection(
    title: String,
    items: List<Pair<String, String>>,
    onEditClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Edit",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onEditClick() }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = label, style = MaterialTheme.typography.bodyMedium)
                Text(
                text = value.ifEmpty { "Not set" },
                style = MaterialTheme.typography.bodyMedium
            )
            }
        }
    }
}
