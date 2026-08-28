package com.android.trippoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.trippoint.authentication.splash.SplashRoute
import com.android.trippoint.core.designsystem.theme.TripPointTheme
import com.android.trippoint.core.navigation.Screen
import com.android.trippoint.core.network.NetworkModule
import com.android.trippoint.core.network.TripRemoteDataSource
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.trip.data.repository.TripRepositoryImpl
import com.android.trippoint.trip.create.CreateTripRoute
import com.android.trippoint.trip.create.CreateTripViewModel
import com.android.trippoint.trip.details.AddDetailsRoute
import com.android.trippoint.trip.details.AddDetailsViewModel
import com.android.trippoint.trip.invite.InvitePeopleRoute
import com.android.trippoint.trip.invite.InvitePeopleViewModel
import com.android.trippoint.trip.overview.TripOverviewRoute
import com.android.trippoint.trip.overview.TripOverviewViewModel
import com.android.trippoint.trip.summary.TripSummaryRoute
import com.android.trippoint.trip.summary.TripSummaryViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // We can keep the splash screen on for a bit if needed
        // splashScreen.setKeepOnScreenCondition { ... }

        enableEdgeToEdge()
        setContent {
            TripPointTheme {
                val navController = rememberNavController()
                val context = androidx.compose.ui.platform.LocalContext.current
                val preferencesManager = remember { PreferencesManager(context) }
                val tripRepository = remember {
                    val api = NetworkModule.provideTripPointApi(
                        authTokenProvider = { preferencesManager.getAuthToken() },
                        refreshTokenProvider = { preferencesManager.getRefreshToken() },
                        onTokenRefreshed = { token, refresh ->
                            preferencesManager.setAuthToken(token)
                            preferencesManager.setRefreshToken(refresh)
                        }
                    )
                    TripRepositoryImpl(TripRemoteDataSource(api))
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Splash.route) {
                            SplashRoute(
                                onNavigateToWelcome = {
                                    navController.navigate(Screen.Welcome.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                },
                                onNavigateToProfileSetup = {
                                    navController.navigate(Screen.ProfileSetup.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                },
                                onNavigateToPermissions = {
                                    navController.navigate(Screen.Permissions.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                },
                                onNavigateToHome = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Welcome.route) {
                            com.android.trippoint.authentication.onboarding.WelcomeScreen(
                                onGetStarted = {
                                    navController.navigate(Screen.Onboarding.route)
                                },
                                onSignIn = {
                                    navController.navigate(Screen.Login.route)
                                }
                            )
                        }
                        composable(Screen.Onboarding.route) {
                            com.android.trippoint.authentication.onboarding.OnboardingRoute(
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Welcome.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Login.route) {
                            com.android.trippoint.authentication.login.LoginRoute(
                                onNavigateToHome = { isProfileComplete ->
                                    val destination = if (isProfileComplete) {
                                        Screen.Home.route
                                    } else {
                                        Screen.ProfileSetup.route
                                    }
                                    navController.navigate(destination) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToSignUp = {
                                    navController.navigate(Screen.Register.route)
                                },
                                onForgotPassword = {
                                    navController.navigate(Screen.ForgotPassword.route)
                                }
                            )
                        }
                        composable(Screen.ForgotPassword.route) {
                            com.android.trippoint.authentication.forgotpassword.ForgotPasswordRoute(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToOtp = { email ->
                                    navController.navigate(Screen.Otp.createRoute(email, true))
                                }
                            )
                        }
                        composable(
                            route = Screen.ResetPassword.route,
                            arguments = listOf(
                                androidx.navigation.navArgument("email") {
                                    type = androidx.navigation.NavType.StringType
                                },
                                androidx.navigation.navArgument("otp") {
                                    type = androidx.navigation.NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            val otp = backStackEntry.arguments?.getString("otp") ?: ""
                            com.android.trippoint.authentication.forgotpassword.ResetPasswordRoute(
                                email = email,
                                otp = otp,
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Register.route) {
                            com.android.trippoint.authentication.register.RegisterRoute(
                                onNavigateToOtp = { email ->
                                    navController.navigate(Screen.Otp.createRoute(email)) {
                                        popUpTo(Screen.Register.route) { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Register.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(
                            route = Screen.Otp.route,
                            arguments = listOf(
                                androidx.navigation.navArgument("email") {
                                    type = androidx.navigation.NavType.StringType
                                },
                                androidx.navigation.navArgument("isForgotPassword") {
                                    type = androidx.navigation.NavType.BoolType
                                    defaultValue = false
                                }
                            )
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            val isForgotPassword = backStackEntry.arguments?.getBoolean("isForgotPassword") ?: false
                            com.android.trippoint.authentication.otp.OtpRoute(
                                email = email,
                                isForgotPasswordFlow = isForgotPassword,
                                onNavigateToHome = {
                                    navController.navigate(Screen.ProfileSetup.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToResetPassword = { otp ->
                                    navController.navigate(Screen.ResetPassword.createRoute(email, otp)) {
                                        popUpTo(Screen.Login.route) { inclusive = false }
                                    }
                                }
                            )
                        }
                        composable(Screen.ProfileSetup.route) {
                            com.android.trippoint.authentication.profilesetup.ProfileSetupRoute(
                                onNavigateToHome = {
                                    navController.navigate(Screen.Permissions.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Permissions.route) {
                            com.android.trippoint.authentication.permissions.PermissionsRoute(
                                onNavigateToHome = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.SessionExpired.route) {
                            com.android.trippoint.authentication.session.SessionExpiredScreen(
                                onLoginAgain = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Home.route) {
                            com.android.trippoint.ui.home.HomeRoute(
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToProfile = {
                                    navController.navigate(Screen.Profile.route)
                                },
                                onNavigateToTripDetails = { tripId ->
                                    navController.navigate(Screen.TripOverview.createRoute(tripId))
                                },
                                onNavigateToCreateTrip = {
                                    navController.navigate(Screen.CreateTrip.route)
                                }
                            )
                        }
                        composable(
                            route = Screen.TripOverview.route,
                            arguments = listOf(
                                androidx.navigation.navArgument("tripId") {
                                    type = androidx.navigation.NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                            val viewModel: TripOverviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return TripOverviewViewModel(tripRepository) as T
                                    }
                                }
                            )
                            TripOverviewRoute(
                                tripId = tripId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.CreateTrip.route) {
                            val viewModel: CreateTripViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return CreateTripViewModel(tripRepository) as T
                                    }
                                }
                            )
                            CreateTripRoute(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToAddDetails = { id ->
                                    navController.navigate(Screen.AddDetails.createRoute(id))
                                }
                            )
                        }
                        composable(
                            route = Screen.AddDetails.route,
                            arguments = listOf(
                                androidx.navigation.navArgument("tripId") {
                                    type = androidx.navigation.NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                            val viewModel: AddDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                            AddDetailsRoute(
                                tripId = tripId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onSaveAndContinue = {
                                    navController.navigate(Screen.InvitePeople.createRoute(tripId))
                                }
                            )
                        }
                        composable(
                            route = Screen.InvitePeople.route,
                            arguments = listOf(
                                androidx.navigation.navArgument("tripId") {
                                    type = androidx.navigation.NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                            val viewModel: InvitePeopleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return InvitePeopleViewModel(tripRepository) as T
                                    }
                                }
                            )
                            InvitePeopleRoute(
                                tripId = tripId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToSummary = { id ->
                                    navController.navigate(Screen.TripSummary.createRoute(id))
                                }
                            )
                        }
                        composable(
                            route = Screen.TripSummary.route,
                            arguments = listOf(
                                androidx.navigation.navArgument("tripId") {
                                    type = androidx.navigation.NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                            val viewModel: TripSummaryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return TripSummaryViewModel(tripRepository) as T
                                    }
                                }
                            )
                            TripSummaryRoute(
                                tripId = tripId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToHome = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Profile.route) {
                            com.android.trippoint.ui.settings.SettingsRoute(
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                                onNavigateToPreferences = { navController.navigate(Screen.Preferences.route) },
                                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                                onNavigateToSecurity = { navController.navigate(Screen.Security.route) },
                                onNavigateToSupport = { navController.navigate(Screen.Support.route) },
                                onNavigateToAbout = { navController.navigate(Screen.About.route) }
                            )
                        }
                        composable(Screen.EditProfile.route) {
                            com.android.trippoint.ui.settings.EditProfileRoute(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Preferences.route) {
                            com.android.trippoint.ui.settings.PreferencesRoute(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Notifications.route) {
                            com.android.trippoint.ui.settings.NotificationsRoute(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Security.route) {
                            com.android.trippoint.ui.settings.SecurityRoute(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToChangePassword = { 
                                    navController.navigate(Screen.ChangePassword.route) 
                                },
                                onNavigateToDevices = { 
                                    navController.navigate(Screen.Devices.route) 
                                },
                                onNavigateToConnectedAccounts = { 
                                    navController.navigate(Screen.ConnectedAccounts.route) 
                                }
                            )
                        }
                        composable(Screen.ChangePassword.route) {
                            com.android.trippoint.ui.settings.ChangePasswordRoute(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Devices.route) {
                            com.android.trippoint.ui.settings.DevicesRoute(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ConnectedAccounts.route) {
                            com.android.trippoint.ui.settings.ConnectedAccountsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Support.route) {
                            com.android.trippoint.ui.settings.SupportRoute(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.About.route) { com.android.trippoint.ui.settings.AboutScreen() }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TripPointTheme {
        // Greeting("Android")
    }
}
