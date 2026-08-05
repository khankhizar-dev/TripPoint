package com.android.trippoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
                            com.android.trippoint.authentication.onboarding.OnboardingScreen(
                                onOnboardingComplete = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Welcome.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Login.route) {
                            com.android.trippoint.authentication.login.LoginRoute(
                                onNavigateToHome = {
                                    navController.navigate(Screen.Home.route) {
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
                        composable(Screen.ResetPassword.route) {
                            com.android.trippoint.authentication.forgotpassword.ResetPasswordRoute(
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Register.route) {
                            com.android.trippoint.authentication.register.RegisterRoute(
                                onNavigateToHome = {
                                    // In a real app, we might navigate to OTP first
                                    // For now, let's say registration success leads to OTP
                                    navController.navigate(Screen.Otp.createRoute("user@example.com")) {
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
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToResetPassword = {
                                    navController.navigate(Screen.ResetPassword.route) {
                                        popUpTo(Screen.Login.route) { inclusive = false }
                                    }
                                }
                            )
                        }
                        composable(Screen.Home.route) {
                            Greeting(name = "Home Screen")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TripPointTheme {
        Greeting("Android")
    }
}