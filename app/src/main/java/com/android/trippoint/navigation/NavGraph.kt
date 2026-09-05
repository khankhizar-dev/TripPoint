package com.android.trippoint.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.trippoint.authentication.splash.SplashRoute
import com.android.trippoint.booking.add.AddBookingOptionsRoute
import com.android.trippoint.booking.add.AddBookingOptionsViewModel
import com.android.trippoint.booking.add.ImportEmailRoute
import com.android.trippoint.booking.add.ImportEmailViewModel
import com.android.trippoint.booking.add.PnrIntakeRoute
import com.android.trippoint.booking.add.PnrIntakeViewModel
import com.android.trippoint.booking.add.ScanTicketRoute
import com.android.trippoint.booking.add.ScanTicketViewModel
import com.android.trippoint.booking.create.CreateBookingRoute
import com.android.trippoint.booking.create.CreateBookingViewModel
import com.android.trippoint.booking.details.AddTravellerRoute
import com.android.trippoint.booking.details.AddTravellerViewModel
import com.android.trippoint.booking.details.BookingDetailsRoute
import com.android.trippoint.booking.details.BookingDetailsViewModel
import com.android.trippoint.booking.details.BookingItineraryRoute
import com.android.trippoint.booking.details.BookingItineraryViewModel
import com.android.trippoint.booking.details.ManageBookingRoute
import com.android.trippoint.booking.details.ManageBookingViewModel
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.booking.filter.BookingFilterRoute
import com.android.trippoint.booking.filter.BookingFilterViewModel
import com.android.trippoint.booking.list.BookingListRoute
import com.android.trippoint.booking.list.BookingListViewModel
import com.android.trippoint.core.navigation.Screen
import com.android.trippoint.itinerary.add.AddEventRoute
import com.android.trippoint.itinerary.add.AddEventViewModel
import com.android.trippoint.itinerary.day.TimelineRoute
import com.android.trippoint.itinerary.day.TimelineViewModel
import com.android.trippoint.itinerary.details.EventDetailsRoute
import com.android.trippoint.itinerary.details.EventDetailsViewModel
import com.android.trippoint.itinerary.domain.repository.ItineraryRepository
import com.android.trippoint.itinerary.filter.FilterSortRoute
import com.android.trippoint.itinerary.filter.FilterSortViewModel
import com.android.trippoint.itinerary.list.TripDaysRoute
import com.android.trippoint.itinerary.list.TripDaysViewModel
import com.android.trippoint.itinerary.notes.AddNoteRoute
import com.android.trippoint.itinerary.notes.AddNoteViewModel
import com.android.trippoint.itinerary.notes.NotesRoute
import com.android.trippoint.itinerary.notes.NotesViewModel
import com.android.trippoint.itinerary.task.AddTaskRoute
import com.android.trippoint.itinerary.task.AddTaskViewModel
import com.android.trippoint.trip.create.CreateTripRoute
import com.android.trippoint.trip.create.CreateTripViewModel
import com.android.trippoint.trip.details.AddDetailsRoute
import com.android.trippoint.trip.details.AddDetailsViewModel
import com.android.trippoint.trip.domain.repository.TripRepository
import com.android.trippoint.trip.invite.InvitePeopleRoute
import com.android.trippoint.trip.invite.InvitePeopleViewModel
import com.android.trippoint.trip.overview.TripOverviewRoute
import com.android.trippoint.trip.overview.TripOverviewViewModel
import com.android.trippoint.trip.summary.TripSummaryRoute
import com.android.trippoint.trip.summary.TripSummaryViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    tripRepository: TripRepository,
    itineraryRepository: ItineraryRepository,
    bookingRepository: BookingRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        authNavGraph(navController)
        tripNavGraph(navController, tripRepository)
        itineraryNavGraph(navController, itineraryRepository)
        bookingNavGraph(navController, bookingRepository, tripRepository)
    }
}

private fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    addSplashDestination(navController)
    addWelcomeDestination(navController)
    addOnboardingDestination(navController)
    addLoginDestination(navController)
    addForgotPasswordDestination(navController)
    addResetPasswordDestination(navController)
    addRegisterDestination(navController)
    addOtpDestination(navController)
    addProfileSetupDestination(navController)
    addPermissionsDestination(navController)
    addSessionExpiredDestination(navController)
}

private fun NavGraphBuilder.addSplashDestination(navController: NavHostController) {
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
}

private fun NavGraphBuilder.addWelcomeDestination(navController: NavHostController) {
    composable(Screen.Welcome.route) {
        com.android.trippoint.authentication.onboarding.WelcomeScreen(
            onGetStarted = { navController.navigate(Screen.Onboarding.route) },
            onSignIn = { navController.navigate(Screen.Login.route) }
        )
    }
}

private fun NavGraphBuilder.addOnboardingDestination(navController: NavHostController) {
    composable(Screen.Onboarding.route) {
        com.android.trippoint.authentication.onboarding.OnboardingRoute(
            onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.addLoginDestination(navController: NavHostController) {
    composable(Screen.Login.route) {
        com.android.trippoint.authentication.login.LoginRoute(
            onNavigateToHome = { isProfileComplete ->
                val destination = if (isProfileComplete) Screen.Home.route else Screen.ProfileSetup.route
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToSignUp = { navController.navigate(Screen.Register.route) },
            onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
        )
    }
}

private fun NavGraphBuilder.addForgotPasswordDestination(navController: NavHostController) {
    composable(Screen.ForgotPassword.route) {
        com.android.trippoint.authentication.forgotpassword.ForgotPasswordRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToOtp = { email -> navController.navigate(Screen.Otp.createRoute(email, true)) }
        )
    }
}

private fun NavGraphBuilder.addResetPasswordDestination(navController: NavHostController) {
    composable(
        route = Screen.ResetPassword.route,
        arguments = listOf(
            androidx.navigation.navArgument("email") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("otp") { type = androidx.navigation.NavType.StringType }
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
}

private fun NavGraphBuilder.addRegisterDestination(navController: NavHostController) {
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
}

private fun NavGraphBuilder.addOtpDestination(navController: NavHostController) {
    composable(
        route = Screen.Otp.route,
        arguments = listOf(
            androidx.navigation.navArgument("email") { type = androidx.navigation.NavType.StringType },
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
}

private fun NavGraphBuilder.addProfileSetupDestination(navController: NavHostController) {
    composable(Screen.ProfileSetup.route) {
        com.android.trippoint.authentication.profilesetup.ProfileSetupRoute(
            onNavigateToHome = {
                navController.navigate(Screen.Permissions.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.addPermissionsDestination(navController: NavHostController) {
    composable(Screen.Permissions.route) {
        com.android.trippoint.authentication.permissions.PermissionsRoute(
            onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.addSessionExpiredDestination(navController: NavHostController) {
    composable(Screen.SessionExpired.route) {
        com.android.trippoint.authentication.session.SessionExpiredScreen(
            onLoginAgain = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.tripNavGraph(navController: NavHostController, tripRepository: TripRepository) {
    addHomeDestination(navController)
    addTripOverviewDestination(navController, tripRepository)
    addCreateTripDestination(navController, tripRepository)
    addAddDetailsDestination(navController)
    addInvitePeopleDestination(navController, tripRepository)
    addTripSummaryDestination(navController, tripRepository)
    addProfileDestination(navController)
    addEditProfileDestination(navController)
    addPreferencesDestination(navController)
    addNotificationsDestination(navController)
    addSecurityDestination(navController)
    addChangePasswordDestination(navController)
    addDevicesDestination(navController)
    addConnectedAccountsDestination(navController)
    addSupportDestination(navController)
    addAboutDestination()
}

private fun NavGraphBuilder.addHomeDestination(navController: NavHostController) {
    composable(Screen.Home.route) {
        com.android.trippoint.ui.home.HomeRoute(
            onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
            onNavigateToTripDetails = { tripId -> 
                navController.navigate(Screen.TripOverview.createRoute(tripId)) 
            },
            onNavigateToCreateTrip = { navController.navigate(Screen.CreateTrip.route) },
            onNavigateToBookings = { tripId -> 
                navController.navigate(Screen.Bookings.createRoute(tripId))
            }
        )
    }
}

private fun NavGraphBuilder.addTripOverviewDestination(
    navController: NavHostController, 
    tripRepository: TripRepository
) {
    composable(
        route = Screen.TripOverview.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: TripOverviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TripOverviewViewModel(tripRepository) as T
                }
            }
        )
        TripOverviewRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToTimeline = { id -> navController.navigate(Screen.TripDays.createRoute(id)) },
            onNavigateToBookings = { id -> navController.navigate(Screen.Bookings.createRoute(id)) },
            onNavigateToAddTask = { id -> navController.navigate(Screen.AddTask.createRoute(id, "today")) },
            onNavigateToAddNote = { id -> navController.navigate(Screen.AddNote.createRoute(id)) },
            onNavigateToAddBooking = { id -> navController.navigate(Screen.AddBookingOptions.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addCreateTripDestination(
    navController: NavHostController, 
    tripRepository: TripRepository
) {
    composable(Screen.CreateTrip.route) {
        val viewModel: CreateTripViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CreateTripViewModel(tripRepository) as T
                }
            }
        )
        CreateTripRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddDetails = { id -> navController.navigate(Screen.AddDetails.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addAddDetailsDestination(navController: NavHostController) {
    composable(
        route = Screen.AddDetails.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: AddDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        AddDetailsRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onSaveAndContinue = { navController.navigate(Screen.InvitePeople.createRoute(tripId)) },
            onNavigateToItinerary = { id -> navController.navigate(Screen.TripDays.createRoute(id)) },
            onNavigateToTasks = { id -> navController.navigate(Screen.AddTask.createRoute(id, "today")) },
            onNavigateToNotes = { id -> navController.navigate(Screen.Notes.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addInvitePeopleDestination(
    navController: NavHostController, 
    tripRepository: TripRepository
) {
    composable(
        route = Screen.InvitePeople.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: InvitePeopleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return InvitePeopleViewModel(tripRepository) as T
                }
            }
        )
        InvitePeopleRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToSummary = { id -> navController.navigate(Screen.TripSummary.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addTripSummaryDestination(
    navController: NavHostController, 
    tripRepository: TripRepository
) {
    composable(
        route = Screen.TripSummary.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: TripSummaryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
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
}

private fun NavGraphBuilder.addProfileDestination(navController: NavHostController) {
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
}

private fun NavGraphBuilder.addEditProfileDestination(navController: NavHostController) {
    composable(Screen.EditProfile.route) {
        com.android.trippoint.ui.settings.EditProfileRoute(onNavigateBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.addPreferencesDestination(navController: NavHostController) {
    composable(Screen.Preferences.route) {
        com.android.trippoint.ui.settings.PreferencesRoute(onNavigateBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.addNotificationsDestination(navController: NavHostController) {
    composable(Screen.Notifications.route) {
        com.android.trippoint.ui.settings.NotificationsRoute(onNavigateBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.addSecurityDestination(navController: NavHostController) {
    composable(Screen.Security.route) {
        com.android.trippoint.ui.settings.SecurityRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword.route) },
            onNavigateToDevices = { navController.navigate(Screen.Devices.route) },
            onNavigateToConnectedAccounts = { navController.navigate(Screen.ConnectedAccounts.route) }
        )
    }
}

private fun NavGraphBuilder.addChangePasswordDestination(navController: NavHostController) {
    composable(Screen.ChangePassword.route) {
        com.android.trippoint.ui.settings.ChangePasswordRoute(onNavigateBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.addDevicesDestination(navController: NavHostController) {
    composable(Screen.Devices.route) {
        com.android.trippoint.ui.settings.DevicesRoute(onNavigateBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.addConnectedAccountsDestination(navController: NavHostController) {
    composable(Screen.ConnectedAccounts.route) {
        com.android.trippoint.ui.settings.ConnectedAccountsScreen(onNavigateBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.addSupportDestination(navController: NavHostController) {
    composable(Screen.Support.route) {
        com.android.trippoint.ui.settings.SupportRoute(onNavigateBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.addAboutDestination() {
    composable(Screen.About.route) { com.android.trippoint.ui.settings.AboutScreen() }
}

private fun NavGraphBuilder.itineraryNavGraph(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    addTripDaysDestination(navController, itineraryRepository)
    addTimelineDestination(navController, itineraryRepository)
    addEventDetailsDestination(navController, itineraryRepository)
    addAddTaskDestination(navController, itineraryRepository)
    addNotesDestination(navController, itineraryRepository)
    addAddNoteDestination(navController, itineraryRepository)
    addAddEventDestination(navController, itineraryRepository)
    addFilterSortDestination(navController, itineraryRepository)
}

private fun NavGraphBuilder.addTripDaysDestination(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    composable(
        route = Screen.TripDays.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: TripDaysViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TripDaysViewModel(itineraryRepository) as T
                }
            }
        )
        TripDaysRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDayTimeline = { id, date -> 
                navController.navigate(Screen.Timeline.createRoute(id, date)) 
            }
        )
    }
}

private fun NavGraphBuilder.addTimelineDestination(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    composable(
        route = Screen.Timeline.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("date") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val date = backStackEntry.arguments?.getString("date") ?: ""
        val viewModel: TimelineViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TimelineViewModel(itineraryRepository) as T
                }
            }
        )
        TimelineRoute(
            tripId = tripId,
            date = date,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToEventDetails = { tId, dId, eId ->
                navController.navigate(Screen.EventDetails.createRoute(tId, dId, eId))
            },
            onNavigateToAddEvent = { id, d -> navController.navigate(Screen.AddEvent.createRoute(id, d)) },
            onNavigateToAddTask = { id, d -> navController.navigate(Screen.AddTask.createRoute(id, d)) },
            onNavigateToAddNote = { id -> navController.navigate(Screen.AddNote.createRoute(id)) },
            onNavigateToFilter = { id -> navController.navigate(Screen.FilterSort.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addEventDetailsDestination(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    composable(
        route = Screen.EventDetails.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("dayId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("activityId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val dayId = backStackEntry.arguments?.getString("dayId") ?: ""
        val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
        val viewModel: EventDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EventDetailsViewModel(itineraryRepository) as T
                }
            }
        )
        EventDetailsRoute(
            tripId = tripId,
            dayId = dayId,
            activityId = activityId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addAddTaskDestination(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    composable(
        route = Screen.AddTask.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("date") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val date = backStackEntry.arguments?.getString("date") ?: ""
        val viewModel: AddTaskViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddTaskViewModel(itineraryRepository) as T
                }
            }
        )
        AddTaskRoute(
            tripId = tripId,
            date = date,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onTaskAdded = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addNotesDestination(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    composable(
        route = Screen.Notes.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: NotesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotesViewModel(itineraryRepository) as T
                }
            }
        )
        NotesRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddNote = { id -> navController.navigate(Screen.AddNote.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addAddNoteDestination(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    composable(
        route = Screen.AddNote.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: AddNoteViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddNoteViewModel(itineraryRepository) as T
                }
            }
        )
        AddNoteRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNoteAdded = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addAddEventDestination(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    composable(
        route = Screen.AddEvent.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("date") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val date = backStackEntry.arguments?.getString("date") ?: ""
        val viewModel: AddEventViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddEventViewModel(itineraryRepository) as T
                }
            }
        )
        AddEventRoute(
            tripId = tripId,
            date = date,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onEventAdded = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addFilterSortDestination(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    composable(
        route = Screen.FilterSort.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: FilterSortViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FilterSortViewModel(itineraryRepository) as T
                }
            }
        )
        FilterSortRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.bookingNavGraph(
    navController: NavHostController, 
    bookingRepository: BookingRepository,
    tripRepository: TripRepository
) {
    addBookingListDestination(navController, bookingRepository)
    addBookingDetailsDestination(navController, bookingRepository)
    addAddTravellerDestination(navController, bookingRepository)
    addBookingItineraryDestination(navController, bookingRepository)
    addManageBookingDestination(navController, bookingRepository)
    addAddBookingOptionsDestination(navController)
    addPnrIntakeDestination(navController, bookingRepository)
    addScanTicketDestination(navController, bookingRepository)
    addImportEmailDestination(navController, bookingRepository)
    addCreateBookingDestination(navController, bookingRepository, tripRepository)
    addBookingFilterDestination(navController)
}

private fun NavGraphBuilder.addBookingListDestination(
    navController: NavHostController, 
    bookingRepository: BookingRepository
) {
    composable(
        route = Screen.Bookings.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { 
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId")
        val viewModel: BookingListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookingListViewModel(bookingRepository) as T
                }
            }
        )
        BookingListRoute(
            tripId = tripId ?: "",
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToDetails = { tId, bId -> 
                navController.navigate(Screen.BookingDetails.createRoute(tId, bId)) 
            },
            onNavigateToCreate = { id -> navController.navigate(Screen.AddBookingOptions.createRoute(id)) },
            onNavigateToFilter = { id -> navController.navigate(Screen.BookingFilter.createRoute(id)) },
            onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
        )
    }
}

private fun NavGraphBuilder.addBookingDetailsDestination(
    navController: NavHostController, 
    bookingRepository: BookingRepository
) {
    composable(
        route = Screen.BookingDetails.route,
        arguments = listOf(
            androidx.navigation.navArgument("bookingId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("tripId") { 
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
        val viewModel: BookingDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookingDetailsViewModel(bookingRepository) as T
                }
            }
        )
        BookingDetailsRoute(
            tripId = tripId,
            bookingId = bookingId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToItinerary = { tId, bId -> 
                navController.navigate(Screen.BookingItinerary.createRoute(tId, bId)) 
            },
            onNavigateToManage = { tId, bId -> 
                navController.navigate(Screen.ManageBooking.createRoute(tId, bId)) 
            },
            onNavigateToAddTraveller = { tId, bId ->
                navController.navigate(Screen.AddTraveller.createRoute(tId, bId))
            }
        )
    }
}

private fun NavGraphBuilder.addAddTravellerDestination(
    navController: NavHostController, 
    bookingRepository: BookingRepository
) {
    composable(
        route = Screen.AddTraveller.route,
        arguments = listOf(
            androidx.navigation.navArgument("bookingId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
        val viewModel: AddTravellerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddTravellerViewModel(bookingRepository) as T
                }
            }
        )
        AddTravellerRoute(
            tripId = tripId,
            bookingId = bookingId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addBookingItineraryDestination(
    navController: NavHostController, 
    bookingRepository: BookingRepository
) {
    composable(
        route = Screen.BookingItinerary.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("bookingId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
        val viewModel: BookingItineraryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookingItineraryViewModel(bookingRepository) as T
                }
            }
        )
        BookingItineraryRoute(
            tripId = tripId,
            bookingId = bookingId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addManageBookingDestination(
    navController: NavHostController, 
    bookingRepository: BookingRepository
) {
    composable(
        route = Screen.ManageBooking.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("bookingId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
        val viewModel: ManageBookingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ManageBookingViewModel(bookingRepository) as T
                }
            }
        )
        ManageBookingRoute(
            tripId = tripId,
            bookingId = bookingId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addAddBookingOptionsDestination(navController: NavHostController) {
    composable(
        route = Screen.AddBookingOptions.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { 
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: AddBookingOptionsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        AddBookingOptionsRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToManualEntry = { id -> navController.navigate(Screen.CreateBooking.createRoute(id)) },
            onNavigateToPnrEntry = { id -> navController.navigate(Screen.PnrIntake.createRoute(id)) },
            onNavigateToScanTicket = { id -> navController.navigate(Screen.ScanTicket.createRoute(id)) },
            onNavigateToImportEmail = { id -> navController.navigate(Screen.ImportEmail.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addPnrIntakeDestination(
    navController: NavHostController, 
    bookingRepository: BookingRepository
) {
    composable(
        route = Screen.PnrIntake.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { 
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: PnrIntakeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PnrIntakeViewModel(bookingRepository) as T
                }
            }
        )
        PnrIntakeRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onBookingAdded = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addScanTicketDestination(
    navController: NavHostController, 
    bookingRepository: BookingRepository
) {
    composable(
        route = Screen.ScanTicket.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { 
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: ScanTicketViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ScanTicketViewModel(bookingRepository) as T
                }
            }
        )
        ScanTicketRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onBookingAdded = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addImportEmailDestination(
    navController: NavHostController, 
    bookingRepository: BookingRepository
) {
    composable(
        route = Screen.ImportEmail.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { 
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: ImportEmailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ImportEmailViewModel(bookingRepository) as T
                }
            }
        )
        ImportEmailRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onSuccess = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addCreateBookingDestination(
    navController: NavHostController, 
    bookingRepository: BookingRepository,
    tripRepository: TripRepository
) {
    composable(
        route = Screen.CreateBooking.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { 
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: CreateBookingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CreateBookingViewModel(bookingRepository, tripRepository) as T
                }
            }
        )
        CreateBookingRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addBookingFilterDestination(navController: NavHostController) {
    composable(
        route = Screen.BookingFilter.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) {
        val viewModel: BookingFilterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        BookingFilterRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onFiltersApplied = { navController.popBackStack() }
        )
    }
}
