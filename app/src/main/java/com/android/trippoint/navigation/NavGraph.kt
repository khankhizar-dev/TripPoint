package com.android.trippoint.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.trippoint.authentication.forgotpassword.ForgotPasswordRoute
import com.android.trippoint.authentication.forgotpassword.ResetPasswordRoute
import com.android.trippoint.authentication.login.LoginRoute
import com.android.trippoint.authentication.onboarding.OnboardingRoute
import com.android.trippoint.authentication.onboarding.WelcomeScreen
import com.android.trippoint.authentication.otp.OtpRoute
import com.android.trippoint.authentication.permissions.PermissionsRoute
import com.android.trippoint.authentication.profilesetup.ProfileSetupRoute
import com.android.trippoint.authentication.register.RegisterRoute
import com.android.trippoint.authentication.session.SessionExpiredScreen
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
import com.android.trippoint.budget.create.CreateBudgetRoute
import com.android.trippoint.budget.create.CreateBudgetViewModel
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.budget.expense.add.AddExpenseRoute
import com.android.trippoint.budget.expense.add.AddExpenseViewModel
import com.android.trippoint.budget.expense.details.ExpenseDetailsRoute
import com.android.trippoint.budget.expense.details.ExpenseDetailsViewModel
import com.android.trippoint.budget.expense.list.ExpenseListRoute
import com.android.trippoint.budget.expense.list.ExpenseListViewModel
import com.android.trippoint.budget.expense.scanner.ReceiptScannerRoute
import com.android.trippoint.budget.expense.scanner.ReceiptScannerViewModel
import com.android.trippoint.budget.list.BudgetListRoute
import com.android.trippoint.budget.list.BudgetListViewModel
import com.android.trippoint.budget.overview.BudgetOverviewRoute
import com.android.trippoint.budget.overview.BudgetOverviewViewModel
import com.android.trippoint.budget.reports.BudgetReportsRoute
import com.android.trippoint.budget.reports.BudgetReportsViewModel
import com.android.trippoint.budget.settlement.SettlementRoute
import com.android.trippoint.budget.settlement.SettlementViewModel
import com.android.trippoint.budget.trends.SpendingTrendsRoute
import com.android.trippoint.budget.trends.SpendingTrendsViewModel
import com.android.trippoint.core.navigation.Screen
import com.android.trippoint.documents.add.AddDocumentRoute
import com.android.trippoint.documents.add.AddDocumentViewModel
import com.android.trippoint.documents.add.UploadOptionsRoute
import com.android.trippoint.documents.categories.DocumentCategoriesRoute
import com.android.trippoint.documents.categories.DocumentCategoriesViewModel
import com.android.trippoint.documents.details.DocumentDetailsRoute
import com.android.trippoint.documents.details.DocumentDetailsViewModel
import com.android.trippoint.documents.domain.repository.DocumentRepository
import com.android.trippoint.documents.list.DocumentListRoute
import com.android.trippoint.documents.list.DocumentListViewModel
import com.android.trippoint.documents.scan.ScanDocumentRoute
import com.android.trippoint.documents.scan.ScanDocumentViewModel
import com.android.trippoint.documents.search.SearchFilterRoute
import com.android.trippoint.documents.search.SearchFilterViewModel
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
import com.android.trippoint.ui.home.HomeRoute
import com.android.trippoint.ui.settings.AboutScreen
import com.android.trippoint.ui.settings.ChangePasswordRoute
import com.android.trippoint.ui.settings.ConnectedAccountsScreen
import com.android.trippoint.ui.settings.DevicesRoute
import com.android.trippoint.ui.settings.EditProfileRoute
import com.android.trippoint.ui.settings.NotificationsRoute
import com.android.trippoint.ui.settings.PreferencesRoute
import com.android.trippoint.ui.settings.SecurityRoute
import com.android.trippoint.ui.settings.SettingsRoute
import com.android.trippoint.ui.settings.SupportRoute

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
        WelcomeScreen(
            onGetStarted = { navController.navigate(Screen.Onboarding.route) },
            onSignIn = { navController.navigate(Screen.Login.route) }
        )
    }
}

private fun NavGraphBuilder.addOnboardingDestination(navController: NavHostController) {
    composable(Screen.Onboarding.route) {
        OnboardingRoute(
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
        LoginRoute(
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
        ForgotPasswordRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToOtp = { email ->
                navController.navigate(Screen.Otp.createRoute(email, true))
            }
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
        ResetPasswordRoute(
            email = email, otp = otp,
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
        RegisterRoute(
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
        OtpRoute(
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
}

private fun NavGraphBuilder.addProfileSetupDestination(navController: NavHostController) {
    composable(Screen.ProfileSetup.route) {
        ProfileSetupRoute(
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
        PermissionsRoute(
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
        SessionExpiredScreen(
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
        HomeRoute(
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
            onNavigateToAddExpense = { id -> navController.navigate(Screen.Budgets.createRoute(id)) },
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
        SettingsRoute(
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
        EditProfileRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Preferences.route) {
        PreferencesRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Notifications.route) {
        NotificationsRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Security.route) {
        SecurityRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword.route) },
            onNavigateToDevices = { navController.navigate(Screen.Devices.route) },
            onNavigateToConnectedAccounts = { navController.navigate(Screen.ConnectedAccounts.route) }
        )
    }
    composable(Screen.ChangePassword.route) {
        ChangePasswordRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Devices.route) {
        DevicesRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.ConnectedAccounts.route) {
        ConnectedAccountsScreen(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.Support.route) {
        SupportRoute(onNavigateBack = { navController.popBackStack() })
    }
    composable(Screen.About.route) { AboutScreen() }
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
            onNavigateToDetails = { tId, bId -> 
                navController.navigate(Screen.BookingDetails.createRoute(tId, bId)) 
            },
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
            onNavigateToAddExpense = { tId, bId -> 
                navController.navigate(Screen.AddExpense.createRoute(tId, bId)) 
            },
            onNavigateToExpenses = { tId, bId ->
                navController.navigate(Screen.ExpenseList.createRoute(tId, bId))
            },
            onNavigateToTrends = { tId, bId ->
                navController.navigate(Screen.SpendingTrends.createRoute(tId, bId))
            },
            onNavigateToReports = { tId, bId ->
                navController.navigate(Screen.BudgetReports.createRoute(tId, bId))
            },
            onNavigateToSettlements = { tId ->
                navController.navigate(Screen.BudgetSettlements.createRoute(tId))
            },
            onNavigateToScanner = { bId ->
                navController.navigate(Screen.ReceiptScanner.createRoute(bId))
            },
            onNavigateToSetupBudget = { tId ->
                navController.navigate(Screen.CreateBudget.createRoute(tId))
            }
        )
    }
    composable(
        route = Screen.BudgetSettlements.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: SettlementViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettlementViewModel(budgetRepository) as T
                }
            }
        )
        SettlementRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(
        route = Screen.CreateBudget.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") {
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val viewModel: CreateBudgetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CreateBudgetViewModel(budgetRepository) as T
                }
            }
        )
        CreateBudgetRoute(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onBudgetCreated = { bId ->
                navController.navigate(Screen.BudgetOverview.createRoute(tripId, bId)) {
                    popUpTo(Screen.CreateBudget.route) { inclusive = true }
                }
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
        val viewModel: AddExpenseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddExpenseViewModel(budgetRepository) as T
                }
            }
        )
        AddExpenseRoute(
            tripId = tripId,
            budgetId = budgetId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(
        route = Screen.ExpenseList.route,
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
        val viewModel: ExpenseListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseListViewModel(budgetRepository) as T
                }
            }
        )
        ExpenseListRoute(
            tripId = tripId,
            budgetId = budgetId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddExpense = { tId, bId ->
                navController.navigate(Screen.AddExpense.createRoute(tId, bId))
            },
            onNavigateToDetails = { tId, eId ->
                navController.navigate(Screen.ExpenseDetails.createRoute(tId, eId))
            }
        )
    }
    composable(
        route = Screen.ExpenseDetails.route,
        arguments = listOf(
            androidx.navigation.navArgument("tripId") { type = androidx.navigation.NavType.StringType },
            androidx.navigation.navArgument("expenseId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
        val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""
        val viewModel: ExpenseDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseDetailsViewModel(budgetRepository) as T
                }
            }
        )
        ExpenseDetailsRoute(
            tripId = tripId,
            expenseId = expenseId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToEdit = { tId, eId ->
                // TODO: Reuse Add/Edit screen
            }
        )
    }
    composable(
        route = Screen.ReceiptScanner.route,
        arguments = listOf(
            androidx.navigation.navArgument("budgetId") { type = androidx.navigation.NavType.StringType }
        )
    ) { backStackEntry ->
        val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""
        val viewModel: ReceiptScannerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        ReceiptScannerRoute(
            budgetId = budgetId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToConfirm = {
                navController.popBackStack()
            }
        )
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
            onNavigateToAdd = { navController.navigate(Screen.DocumentUploadOptions.route) },
            onNavigateToSearch = { navController.navigate(Screen.DocumentSearch.route) }
        )
    }
    composable(Screen.DocumentScan.route) {
        val viewModel: ScanDocumentViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        ScanDocumentRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onDocumentCaptured = { /* TODO */ }
        )
    }
    composable(Screen.DocumentSearch.route) {
        val viewModel: SearchFilterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        SearchFilterRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onFiltersApplied = { /* TODO */ }
        )
    }
    composable(
        route = Screen.DocumentDetails.route,
        arguments = listOf(
            androidx.navigation.navArgument("documentId") { type = androidx.navigation.NavType.StringType }
        )
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
            onNavigateToScan = { navController.navigate(Screen.DocumentScan.route) },
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
