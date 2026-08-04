package com.android.trippoint.authentication.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.trippoint.authentication.R
import com.android.trippoint.core.designsystem.components.SplashIllustration
import com.android.trippoint.core.designsystem.components.TripPointButton
import kotlinx.coroutines.launch

data class OnboardingItem(
    val titleResId: Int,
    val subtitleResId: Int,
    val imageResId: Int
)

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val items = listOf(
        OnboardingItem(
            R.string.auth_onboarding_1_title,
            R.string.auth_onboarding_1_subtitle,
            com.android.trippoint.core.designsystem.R.drawable.ic_onboarding_plan
        ),
        OnboardingItem(
            R.string.auth_onboarding_2_title,
            R.string.auth_onboarding_2_subtitle,
            com.android.trippoint.core.designsystem.R.drawable.ic_onboarding_together
        ),
        OnboardingItem(
            R.string.auth_onboarding_3_title,
            R.string.auth_onboarding_3_subtitle,
            com.android.trippoint.core.designsystem.R.drawable.ic_onboarding_offline
        )
    )

    val pagerState = rememberPagerState(pageCount = { items.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(item = items[page])
            }

            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pager Indicator
                Row(
                    modifier = Modifier.height(50.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(items.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        }
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                val isLastPage = pagerState.currentPage == items.size - 1
                val btnText = if (isLastPage) {
                    stringResource(R.string.auth_get_started)
                } else {
                    stringResource(R.string.auth_next)
                }
                TripPointButton(
                    text = btnText,
                    onClick = {
                        if (isLastPage) {
                            onOnboardingComplete()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Skip Button
        TextButton(
            onClick = onOnboardingComplete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
        ) {
            Text(
                text = "Skip",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun OnboardingPage(item: OnboardingItem) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SplashIllustration(
            imageResId = item.imageResId,
            modifier = Modifier
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(item.titleResId),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(item.subtitleResId),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
