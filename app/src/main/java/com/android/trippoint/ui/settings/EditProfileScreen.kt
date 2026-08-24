package com.android.trippoint.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.android.trippoint.authentication.data.repository.AuthRepositoryImpl
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.network.AuthRemoteDataSource
import com.android.trippoint.core.network.NetworkModule
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditProfileRoute(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val (viewModel, uiState) = rememberEditProfileViewModel(context)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditProfileContract.Effect.NavigateBack -> onNavigateBack()
                is EditProfileContract.Effect.ShowError -> { /* Show error toast */ }
            }
        }
    }

    EditProfileScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun rememberEditProfileViewModel(
    context: android.content.Context
): Pair<EditProfileViewModel, EditProfileContract.State> {
    val preferencesManager = androidx.compose.runtime.remember { PreferencesManager(context) }
    val authRemoteDataSource = androidx.compose.runtime.remember {
        val api = NetworkModule.provideTripPointApi(
            authTokenProvider = { preferencesManager.getAuthToken() },
            refreshTokenProvider = { preferencesManager.getRefreshToken() },
            onTokenRefreshed = { token, refresh ->
                preferencesManager.setAuthToken(token)
                preferencesManager.setRefreshToken(refresh)
            }
        )
        AuthRemoteDataSource(api)
    }
    val authRepository = androidx.compose.runtime.remember {
        AuthRepositoryImpl(authRemoteDataSource, preferencesManager)
    }

    val viewModel: EditProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditProfileViewModel(authRepository) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    
    return Pair(viewModel, uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    uiState: EditProfileContract.State,
    onIntent: (EditProfileContract.Intent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { onIntent(EditProfileContract.Intent.PhotoSelected(it.toString())) }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(it))
                        onIntent(EditProfileContract.Intent.DobChanged(formattedDate))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.size(48.dp)) // To balance the back button
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Avatar with Edit Button
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { photoPickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.profilePhotoUri != null) {
                            AsyncImage(
                                model = uiState.profilePhotoUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = uiState.fullName.firstOrNull()?.toString()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Edit Photo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                TripPointTextField(
                    value = uiState.fullName,
                    onValueChange = { onIntent(EditProfileContract.Intent.FullNameChanged(it)) },
                    label = "Full Name"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TripPointTextField(
                    value = uiState.email,
                    onValueChange = { onIntent(EditProfileContract.Intent.EmailChanged(it)) },
                    label = "Email",
                    enabled = false // Usually email isn't directly editable without verification
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TripPointTextField(
                    value = uiState.phone,
                    onValueChange = { onIntent(EditProfileContract.Intent.PhoneChanged(it)) },
                    label = "Phone Number"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TripPointTextField(
                    value = uiState.dob,
                    onValueChange = { },
                    label = "Date of Birth",
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.clickable { showDatePicker = true }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TripPointTextField(
                    value = uiState.nationality,
                    onValueChange = { onIntent(EditProfileContract.Intent.NationalityChanged(it)) },
                    label = "Nationality"
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                TripPointButton(
                    text = "Save Changes",
                    onClick = { onIntent(EditProfileContract.Intent.SaveClicked) },
                    enabled = !uiState.isLoading
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
