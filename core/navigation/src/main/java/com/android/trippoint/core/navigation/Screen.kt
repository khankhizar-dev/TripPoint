package com.android.trippoint.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Otp : Screen("otp/{email}?isForgotPassword={isForgotPassword}") {
        fun createRoute(email: String, isForgotPassword: Boolean = false) =
            "otp/$email?isForgotPassword=$isForgotPassword"
    }
    object ForgotPassword : Screen("forgot_password")
    object ResetPassword : Screen("reset_password")
    object ProfileSetup : Screen("profile_setup")
    object Home : Screen("home")
}
