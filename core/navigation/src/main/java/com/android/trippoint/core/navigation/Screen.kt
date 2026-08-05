package com.android.trippoint.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Otp : Screen("otp/{email}") {
        fun createRoute(email: String) = "otp/$email"
    }
    object Home : Screen("home")
}
