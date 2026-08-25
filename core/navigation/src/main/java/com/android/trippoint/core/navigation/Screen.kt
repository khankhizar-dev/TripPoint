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
    object ResetPassword : Screen("reset_password/{email}/{otp}") {
        fun createRoute(email: String, otp: String) = "reset_password/$email/$otp"
    }
    object ProfileSetup : Screen("profile_setup")
    object Permissions : Screen("permissions")
    object SessionExpired : Screen("session_expired")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Preferences : Screen("preferences")
    object Notifications : Screen("notifications")
    object Security : Screen("security")
    object ChangePassword : Screen("change_password")
    object Devices : Screen("devices")
    object ConnectedAccounts : Screen("connected_accounts")
    object Support : Screen("support")
    object About : Screen("about")
}
