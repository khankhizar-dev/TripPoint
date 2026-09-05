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
    object TripList : Screen("trip_list")
    object TripOverview : Screen("trip_overview/{tripId}") {
        fun createRoute(tripId: String) = "trip_overview/$tripId"
    }
    object CreateTrip : Screen("create_trip")
    object AddDetails : Screen("add_details/{tripId}") {
        fun createRoute(tripId: String) = "add_details/$tripId"
    }
    object InvitePeople : Screen("invite_people/{tripId}") {
        fun createRoute(tripId: String) = "invite_people/$tripId"
    }
    object TripSummary : Screen("trip_summary/{tripId}") {
        fun createRoute(tripId: String) = "trip_summary/$tripId"
    }
    object TripDays : Screen("trip_days/{tripId}") {
        fun createRoute(tripId: String) = "trip_days/$tripId"
    }
    object Timeline : Screen("timeline/{tripId}/{date}") {
        fun createRoute(tripId: String, date: String) = "timeline/$tripId/$date"
    }
    object EventDetails : Screen("event_details/{tripId}/{dayId}/{activityId}") {
        fun createRoute(tripId: String, dayId: String, activityId: String) = 
            "event_details/$tripId/$dayId/$activityId"
    }
    object AddEvent : Screen("add_event/{tripId}/{date}") {
        fun createRoute(tripId: String, date: String) = "add_event/$tripId/$date"
    }
    object AddTask : Screen("add_task/{tripId}/{date}") {
        fun createRoute(tripId: String, date: String) = "add_task/$tripId/$date"
    }
    object Notes : Screen("notes/{tripId}") {
        fun createRoute(tripId: String) = "notes/$tripId"
    }
    object AddNote : Screen("add_note/{tripId}") {
        fun createRoute(tripId: String) = "add_note/$tripId"
    }
    object FilterSort : Screen("filter_sort/{tripId}") {
        fun createRoute(tripId: String) = "filter_sort/$tripId"
    }
    object Bookings : Screen("bookings?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) "bookings?tripId=$tripId" else "bookings"
    }
    object BookingDetails : Screen("booking_details/{bookingId}?tripId={tripId}") {
        fun createRoute(tripId: String, bookingId: String) = "booking_details/$bookingId?tripId=$tripId"
    }
    object AddTraveller : Screen("add_traveller/{bookingId}?tripId={tripId}") {
        fun createRoute(tripId: String, bookingId: String) = "add_traveller/$bookingId?tripId=$tripId"
    }
    object AddBookingOptions : Screen("add_booking_options?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "add_booking_options?tripId=$tripId"
        } else {
            "add_booking_options"
        }
    }
    object CreateBooking : Screen("create_booking?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "create_booking?tripId=$tripId"
        } else {
            "create_booking"
        }
    }
    object PnrIntake : Screen("pnr_intake?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "pnr_intake?tripId=$tripId"
        } else {
            "pnr_intake"
        }
    }
    object ScanTicket : Screen("scan_ticket?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "scan_ticket?tripId=$tripId"
        } else {
            "scan_ticket"
        }
    }
    object ImportEmail : Screen("import_email?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "import_email?tripId=$tripId"
        } else {
            "import_email"
        }
    }
    object BookingFilter : Screen("booking_filter/{tripId}") {
        fun createRoute(tripId: String) = "booking_filter/$tripId"
    }
    object BookingItinerary : Screen("booking_itinerary/{tripId}/{bookingId}") {
        fun createRoute(tripId: String, bookingId: String) = "booking_itinerary/$tripId/$bookingId"
    }
    object ManageBooking : Screen("manage_booking/{tripId}/{bookingId}") {
        fun createRoute(tripId: String, bookingId: String) = "manage_booking/$tripId/$bookingId"
    }
}
