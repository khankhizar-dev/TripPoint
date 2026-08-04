package com.android.trippoint.authentication.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.trippoint.authentication.R
import com.android.trippoint.core.designsystem.components.SplashIllustration
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.ButtonVariant

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SplashIllustration(
            imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_welcome,
            modifier = Modifier
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.auth_welcome_title),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.auth_welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            TripPointButton(
                text = stringResource(R.string.auth_get_started),
                onClick = onGetStarted
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripPointButton(
                text = stringResource(R.string.auth_sign_in),
                onClick = onSignIn,
                variant = ButtonVariant.Text
            )
        }
    }
}
