package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Top Navigation Bar with theme switching support (Section 24.5).
 */
enum class TopAppBarNavIcon {
    None,
    Back,
    Menu
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPointTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navIcon: TopAppBarNavIcon = TopAppBarNavIcon.Back,
    onNavClick: (() -> Unit)? = null,
    isDark: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val containerColor = if (isDark) {
        MaterialTheme.colorScheme.onSurface 
    } else {
        Color.Transparent
    }
    val contentColor = if (isDark) {
        MaterialTheme.colorScheme.surface 
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val navigationIcon: @Composable () -> Unit = {
        if (navIcon != TopAppBarNavIcon.None && onNavClick != null) {
            IconButton(onClick = onNavClick) {
                val icon: ImageVector = when (navIcon) {
                    TopAppBarNavIcon.Back -> Icons.AutoMirrored.Filled.ArrowBack
                    TopAppBarNavIcon.Menu -> Icons.Default.Menu
                    else -> Icons.AutoMirrored.Filled.ArrowBack
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor
                )
            }
        }
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        )
    )
}
