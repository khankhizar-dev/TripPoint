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
import com.android.trippoint.booking.add.*
import com.android.trippoint.booking.create.CreateBookingRoute
import com.android.trippoint.booking.create.CreateBookingViewModel
import com.android.trippoint.booking.details.*
import com.android.trippoint.booking.domain.repository.BookingRepository
import com.android.trippoint.booking.filter.BookingFilterRoute
import com.android.trippoint.booking.filter.BookingFilterViewModel
import com.android.trippoint.booking.list.BookingListRoute
import com.android.trippoint.booking.list.BookingListViewModel
import com.android.trippoint.budget.list.*
import com.android.trippoint.budget.overview.*
import com.android.trippoint.budget.trends.*
import com.android.trippoint.budget.reports.*
import com.android.trippoint.budget.expense.add.*
import com.android.trippoint.budget.expense.list.*
import com.android.trippoint.budget.expense.scanner.*
import com.android.trippoint.documents.list.*
import com.android.trippoint.documents.add.*
import com.android.trippoint.documents.details.*
import com.android.trippoint.documents.categories.*
import com.android.trippoint.documents.domain.repository.DocumentRepository
import com.android.trippoint.budget.domain.repository.BudgetRepository
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
    budgetRepository: BudgetRepository,
    documentRepository: DocumentRepository,
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
        budgetNavGraph(navController, budgetRepository)
        documentsNavGraph(navController, documentRepository)
    }
}

private fun NavGraphBuilder.documentsNavGraph(
    navController: NavHostController,
    documentRepository: DocumentRepository
) {
    composable(Screen.Documents.route) {
        val viewModel: DocumentListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DocumentListViewModel(documentRepository) as T
                }
            }
        )
        DocumentListRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDetails = { id -> 
                navController.navigate(Screen.DocumentDetails.createRoute(id)) 
            },
            onNavigateToCategories = { navController.navigate(Screen.DocumentCategories.route) },
            onNavigateToAdd = { navController.navigate(Screen.DocumentUploadOptions.route) }
        )
    }
    composable(
        route = Screen.DocumentDetails.route,
        arguments = listOf(androidx.navigation.navArgument("documentId") { type = androidx.navigation.NavType.StringType })
    ) { backStackEntry ->
        val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
        val viewModel: DocumentDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DocumentDetailsViewModel(documentRepository) as T
                }
            }
        )
        DocumentDetailsRoute(
            documentId = documentId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(Screen.DocumentUploadOptions.route) {
        UploadOptionsRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToScan = { /* TODO */ },
            onNavigateToManual = { navController.navigate(Screen.AddDocument.route) }
        )
    }
    composable(Screen.DocumentCategories.route) {
        val viewModel: DocumentCategoriesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DocumentCategoriesViewModel(documentRepository) as T
                }
            }
        )
        DocumentCategoriesRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDocumentsByType = { /* TODO: Pass type to list */ }
        )
    }
    composable(Screen.AddDocument.route) {
        val viewModel: AddDocumentViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddDocumentViewModel(documentRepository) as T
                }
            }
        )
        AddDocumentRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
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
            onGetStarted = { navController.navigate(Screen.Onboarding.route) },
            onSignIn = { navController.navigate(Screen.Login.route) }
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
                val destination = if (isProfileComplete) Screen.Home.route else Screen.ProfileSetup.route
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToSignUp = { navController.navigate(Screen.Register.route) },
            onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
        )
    }
    composable(Screen.ForgotPassword.route) {
        com.android.trippoint.authentication.forgotpassword.ForgotPasswordRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToOtp = { email -> navController.navigate(Screen.Otp.createRoute(email, true)) }
        )
    }
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
            email = email, otp = otp,
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
            email = email, isForgotPasswordFlow = isForgotPassword,
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
}

@Suppress("LongMethod")
private fun NavGraphBuilder.tripNavGraph(navController: NavHostController, tripRepository: TripRepository) {
    composable(Screen.Home.route) {
        com.android.trippoint.ui.home.HomeRoute(
            onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
            onNavigateToTripDetails = { tripId -> navController.navigate(Screen.TripOverview.createRoute(tripId)) },
            onNavigateToCreateTrip = { navController.navigate(Screen.CreateTrip.route) },
            onNavigateToBookings = { tripId -> 
                navController.navigate(Screen.Bookings.createRoute(tripId))
            },
            onNavigateToBudgets = { tripId ->
                navController.navigate(Screen.Budgets.createRoute(tripId))
            }
        )
    }
    composable(
        route = Screen.TripOverview.route,
        arguments = listOf(androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType })
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
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToTimeline = { id -> navController.navigate(Screen.TripDays.createRoute(id)) },
            onNavigateToBookings = { id -> navController.navigate(Screen.Bookings.createRoute(id)) },
            onNavigateToAddTask = { id -> navController.navigate(Screen.AddTask.createRoute(id, "today")) },
            onNavigateToAddNote = { id -> navController.navigate(Screen.AddNote.createRoute(id)) },
            onNavigateToAddBooking = { id -> navController.navigate(Screen.AddBookingOptions.createRoute(id)) },
            onNavigateToBudgets = { id -> navController.navigate(Screen.Budgets.createRoute(id)) }
        )
    }
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
    composable(
        route = Screen.AddDetails.route,
        arguments = listOf(androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType })
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: AddDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        AddDetailsRoute(
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onSaveAndContinue = { navController.navigate(Screen.InvitePeople.createRoute(tripId)) },
            onNavigateToItinerary = { id -> navController.navigate(Screen.TripDays.createRoute(id)) },
            onNavigateToTasks = { id -> navController.navigate(Screen.AddTask.createRoute(id, "today")) },
            onNavigateToNotes = { id -> navController.navigate(Screen.Notes.createRoute(id)) }
        )
    }
    composable(
        route = Screen.InvitePeople.route,
        arguments = listOf(androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType })
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
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToSummary = { id -> navController.navigate(Screen.TripSummary.createRoute(id)) }
        )
    }
    composable(
        route = Screen.TripSummary.route,
        arguments = listOf(androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType })
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
            tripId = tripId, viewModel = viewModel,
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
            onNavigateToDocuments = { navController.navigate(Screen.Documents.route) },
            onNavigateToSupport = { navController.navigate(Screen.Support.route) },
            onNavigateToAbout = { navController.navigate(Screen.About.route) }
        )
    }
    composable(Screen.EditProfile.route) {
        com.android.trippoint.ui.settings.EditProfileRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Preferences.route) {
        com.android.trippoint.ui.settings.PreferencesRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Notifications.route) {
        com.android.trippoint.ui.settings.NotificationsRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Security.route) {
        com.android.trippoint.ui.settings.SecurityRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword.route) },
            onNavigateToDevices = { navController.navigate(Screen.Devices.route) },
            onNavigateToConnectedAccounts = { navController.navigate(Screen.ConnectedAccounts.route) }
        )
    }
    composable(Screen.ChangePassword.route) {
        com.android.trippoint.ui.settings.ChangePasswordRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Devices.route) {
        com.android.trippoint.ui.settings.DevicesRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.ConnectedAccounts.route) {
        com.android.trippoint.ui.settings.ConnectedAccountsScreen(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Support.route) {
        com.android.trippoint.ui.settings.SupportRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.About.route) { com.android.trippoint.ui.settings.AboutScreen() }
}

@Suppress("LongMethod")
private fun NavGraphBuilder.itineraryNavGraph(
    navController: NavHostController, 
    itineraryRepository: ItineraryRepository
) {
    composable(
        route = Screen.TripDays.route,
        arguments = listOf(androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType })
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
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDayTimeline = { id, date -> navController.navigate(Screen.Timeline.createRoute(id, date)) }
        )
    }
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
            tripId = tripId, date = date, viewModel = viewModel,
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
            tripId = tripId, dayId = dayId, activityId = activityId,
            viewModel = viewModel, onNavigateBack = { navController.popBackStack() }
        )
    }
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
            tripId = tripId, date = date, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onTaskAdded = { navController.popBackStack() }
        )
    }
    composable(
        route = Screen.Notes.route,
        arguments = listOf(androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType })
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
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddNote = { id -> navController.navigate(Screen.AddNote.createRoute(id)) }
        )
    }
    composable(
        route = Screen.AddNote.route,
        arguments = listOf(androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType })
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
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNoteAdded = { navController.popBackStack() }
        )
    }
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
            tripId = tripId, date = date, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onEventAdded = { navController.popBackStack() }
        )
    }
    composable(
        route = Screen.FilterSort.route,
        arguments = listOf(androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType })
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
        FilterSortRoute(tripId = tripId, viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
    }
}

@Suppress("LongMethod")
private fun NavGraphBuilder.bookingNavGraph(
    navController: NavHostController, 
    bookingRepository: BookingRepository,
    tripRepository: TripRepository
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
            onNavigateToDetails = { tId, bId -> navController.navigate(Screen.BookingDetails.createRoute(tId, bId)) },
            onNavigateToCreate = { id -> navController.navigate(Screen.AddBookingOptions.createRoute(id)) },
            onNavigateToFilter = { id -> navController.navigate(Screen.BookingFilter.createRoute(id)) },
            onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
        )
    }
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
            tripId = tripId, bookingId = bookingId, viewModel = viewModel,
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
            tripId = tripId, bookingId = bookingId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
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
            tripId = tripId, bookingId = bookingId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
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
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToManualEntry = { id -> navController.navigate(Screen.CreateBooking.createRoute(id)) },
            onNavigateToPnrEntry = { id -> navController.navigate(Screen.PnrIntake.createRoute(id)) },
            onNavigateToScanTicket = { id -> navController.navigate(Screen.ScanTicket.createRoute(id)) },
            onNavigateToImportEmail = { id -> navController.navigate(Screen.ImportEmail.createRoute(id)) }
        )
    }
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
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onBookingAdded = { navController.popBackStack() }
        )
    }
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
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onBookingAdded = { navController.popBackStack() }
        )
    }
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
            tripId = tripId, viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onSuccess = { navController.popBackStack() }
        )
    }
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
        CreateBookingRoute(tripId = tripId, viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
    }
    composable(
        route = Screen.BookingFilter.route,
        arguments = listOf(androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType })
    ) {
        val viewModel: BookingFilterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        BookingFilterRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onFiltersApplied = { navController.popBackStack() }
        )
    }
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
            tripId = tripId, bookingId = bookingId,
            viewModel = viewModel, onNavigateBack = { navController.popBackStack() }
        )
    }
}

@Suppress("LongMethod")
private fun NavGraphBuilder.budgetNavGraph(
    navController: NavHostController,
    budgetRepository: BudgetRepository
) {
    composable(
        route = Screen.Budgets.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") {
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId")
        val viewModel: BudgetListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BudgetListViewModel(budgetRepository) as T
                }
            }
        )
        BudgetListRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDetails = { tId, bId -> 
                navController.navigate(Screen.BudgetOverview.createRoute(tId, bId)) 
            },
            onNavigateToCreate = { 
                navController.navigate(Screen.CreateBudget.createRoute(tripId)) 
            }
        )
    }
    composable(
        route = Screen.BudgetOverview.route,
        arguments = listOf(
            androidx.navigation.navArgument("budgetId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("tripId") {
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""
        val viewModel: BudgetOverviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BudgetOverviewViewModel(budgetRepository) as T
                }
            }
        )
        BudgetOverviewRoute(
            tripId = tripId,
            budgetId = budgetId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddExpense = { bId -> 
                navController.navigate(Screen.AddExpense.createRoute(bId)) 
            },
            onNavigateToExpenses = { bId ->
                navController.navigate(Screen.ExpenseList.createRoute(bId))
            },
            onNavigateToTrends = { tId, bId ->
                navController.navigate(Screen.SpendingTrends.createRoute(tId, bId))
            },
            onNavigateToReports = { tId, bId ->
                navController.navigate(Screen.BudgetReports.createRoute(tId, bId))
            },
            onNavigateToScanner = { bId ->
                navController.navigate(Screen.ReceiptScanner.createRoute(bId))
            }
        )
    }
    composable(
        route = Screen.SpendingTrends.route,
        arguments = listOf(
            androidx.navigation.navArgument("budgetId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("tripId") {
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""
        val viewModel: SpendingTrendsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SpendingTrendsViewModel(budgetRepository) as T
                }
            }
        )
        SpendingTrendsRoute(
            tripId = tripId,
            budgetId = budgetId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(
        route = Screen.BudgetReports.route,
        arguments = listOf(
            androidx.navigation.navArgument("budgetId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("tripId") {
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""
        val viewModel: BudgetReportsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BudgetReportsViewModel(budgetRepository) as T
                }
            }
        )
        BudgetReportsRoute(
            tripId = tripId,
            budgetId = budgetId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(
        route = Screen.AddExpense.route,
        arguments = listOf(androidx.navigation.navArgument("budgetId") { type = androidx.navigation.NavType.StringType })
    ) { backStackEntry ->
        val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""
        val viewModel: AddExpenseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddExpenseViewModel(budgetRepository) as T
                }
            }
        )
        AddExpenseRoute(
            budgetId = budgetId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(
        route = Screen.ExpenseList.route,
        arguments = listOf(androidx.navigation.navArgument("budgetId") { type = androidx.navigation.NavType.StringType })
    ) { backStackEntry ->
        val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""
        val viewModel: ExpenseListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseListViewModel(budgetRepository) as T
                }
            }
        )
        ExpenseListRoute(
            budgetId = budgetId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddExpense = { bId ->
                navController.navigate(Screen.AddExpense.createRoute(bId))
            }
        )
    }
    composable(
        route = Screen.ReceiptScanner.route,
        arguments = listOf(androidx.navigation.navArgument("budgetId") { type = androidx.navigation.NavType.StringType })
    ) { backStackEntry ->
        val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""
        val viewModel: ReceiptScannerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        ReceiptScannerRoute(
            budgetId = budgetId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToConfirm = { receipt ->
                // TODO: Navigate to confirmation screen or pre-fill Add Expense
                navController.popBackStack()
            }
        )
    }
}
