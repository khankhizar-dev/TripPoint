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
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "bookings?tripId=$tripId"
        } else {
            "bookings"
        }
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
    object Budgets : Screen("budgets?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "budgets?tripId=$tripId"
        } else {
            "budgets"
        }
    }
    object BudgetOverview : Screen("budget_overview/{budgetId}?tripId={tripId}") {
        fun createRoute(tripId: String, budgetId: String) = "budget_overview/$budgetId?tripId=$tripId"
    }
    object BudgetSettlements : Screen("budget_settlements/{tripId}") {
        fun createRoute(tripId: String) = "budget_settlements/$tripId"
    }
    object CreateBudget : Screen("create_budget?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "create_budget?tripId=$tripId"
        } else {
            "create_budget"
        }
    }
    object AddExpense : Screen("add_expense/{budgetId}?tripId={tripId}") {
        fun createRoute(tripId: String, budgetId: String) = "add_expense/$budgetId?tripId=$tripId"
    }
    object ExpenseDetails : Screen("expense_details/{expenseId}?tripId={tripId}") {
        fun createRoute(tripId: String, expenseId: String) = "expense_details/$expenseId?tripId=$tripId"
    }
    object ExpenseList : Screen("expense_list/{budgetId}?tripId={tripId}") {
        fun createRoute(tripId: String, budgetId: String) = "expense_list/$budgetId?tripId=$tripId"
    }
    object SpendingTrends : Screen("spending_trends/{budgetId}?tripId={tripId}") {
        fun createRoute(tripId: String, budgetId: String) = "spending_trends/$budgetId?tripId=$tripId"
    }
    object BudgetReports : Screen("budget_reports/{budgetId}?tripId={tripId}") {
        fun createRoute(tripId: String, budgetId: String) = "budget_reports/$budgetId?tripId=$tripId"
    }
    object ReceiptScanner : Screen("receipt_scanner/{budgetId}") {
        fun createRoute(budgetId: String) = "receipt_scanner/$budgetId"
    }
    object Documents : Screen("documents?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "documents?tripId=$tripId"
        } else {
            "documents"
        }
    }
    object DocumentCategories : Screen("document_categories?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "document_categories?tripId=$tripId"
        } else {
            "document_categories"
        }
    }
    object DocumentDetails : Screen("document_details/{documentId}?tripId={tripId}") {
        fun createRoute(documentId: String, tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "document_details/$documentId?tripId=$tripId"
        } else {
            "document_details/$documentId"
        }
    }
    object DocumentUploadOptions : Screen("document_upload_options?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "document_upload_options?tripId=$tripId"
        } else {
            "document_upload_options"
        }
    }
    object AddDocument : Screen("add_document?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "add_document?tripId=$tripId"
        } else {
            "add_document"
        }
    }
    object DocumentScan : Screen("document_scan?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "document_scan?tripId=$tripId"
        } else {
            "document_scan"
        }
    }
    object DocumentScanConfirm : Screen("document_scan_confirm?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "document_scan_confirm?tripId=$tripId"
        } else {
            "document_scan_confirm"
        }
    }
    object DocumentSearch : Screen("document_search?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "document_search?tripId=$tripId"
        } else {
            "document_search"
        }
    }
    object Checklists : Screen("checklists?tripId={tripId}") {
        fun createRoute(tripId: String? = null) = if (!tripId.isNullOrBlank()) {
            "checklists?tripId=$tripId"
        } else {
            "checklists"
        }
    }
    object ChecklistDetails : Screen("checklist_details/{checklistId}") {
        fun createRoute(id: String) = "checklist_details/$id"
    }
    object ChecklistItems : Screen("checklist_items/{checklistId}/{sectionId}") {
        fun createRoute(checklistId: String, sectionId: String) = "checklist_items/$checklistId/$sectionId"
    }
    object AddChecklistItem : Screen("add_checklist_item/{checklistId}/{sectionId}") {
        fun createRoute(checklistId: String, sectionId: String) = "add_checklist_item/$checklistId/$sectionId"
    }
    object ChecklistProgress : Screen("checklist_progress/{tripId}/{checklistId}") {
        fun createRoute(tripId: String, checklistId: String) = "checklist_progress/$tripId/$checklistId"
    }
    object ChecklistTemplates : Screen("checklist_templates/{tripId}") {
        fun createRoute(tripId: String) = "checklist_templates/$tripId"
    }
    object ChecklistAiSuggest : Screen("checklist_ai_suggest/{tripId}") {
        fun createRoute(tripId: String) = "checklist_ai_suggest/$tripId"
    }
}
