package com.android.trippoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.trippoint.authentication.splash.SplashRoute
import com.android.trippoint.core.designsystem.theme.TripPointTheme
import com.android.trippoint.core.navigation.Screen

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
