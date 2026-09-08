# TripPoint Android

TripPoint is a modern Android travel application built with Kotlin, Jetpack Compose, and Clean Architecture. It focuses on providing a seamless experience for planning and tracking trips.

## 🚀 Tech Stack

- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVI (Model-View-Intent) & Clean Architecture
- **Dependency Injection**: Hilt (Planned)
- **Networking**: Retrofit & GraphQL (100% migrated for Budget & Expenses)
- **Local Storage**: Room & EncryptedSharedPreferences (`androidx.security:security-crypto` for secure UUID & Token storage)
- **Navigation**: Navigation Compose (Modularized & Multi-module aware)
- **Design System**: Centralized design system in `:core:designsystem`

## ⚙️ CI/CD & Quality

The project uses a production-grade quality pipeline:
- **Linting**: Android Lint, **Detekt** (code smells), and **Ktlint** (formatting) ensure high code quality.
- **Testing**: JUnit 4, Robolectric, MockK, and Turbine for Flow testing.
- **Commands**:
    - Run all tests: `./gradlew testDebugUnitTest`
    - Check formatting: `./gradlew ktlintCheck`
    - Static analysis: `./gradlew detekt`

## 🏗 Architecture

The project follows a multi-module architecture to promote scalability and maintainability:

### Core Modules
- **`:core:common`**: Contains base components like `BaseViewModel` for MVI and universal domain models (`Trip`, `Traveler`).
- **`:core:designsystem`**: The central repository for all UI components (`TripPointButton`, `TripCard`, `TripStatusChip`), tokens, and premium illustrations.
- **`:core:navigation`**: Centralized screen definitions and navigation routes.
- **`:core:network`**: GraphQL configuration and centralized remote data sources.
- **`:core:database`**: Local data persistence and secure preference management.

### Feature Modules
- **`:app`**: The main entry point. Handles top-level navigation and app initialization.
- **`:authentication`**: Manages the user lifecycle (Splash, Onboarding, Login, Registration, OTP, Forgot Password, Profile Setup, Permissions).
- **`:trip`**: **The Trip Workspace**. Manages the end-to-end trip lifecycle (List, Creation flow, Overview, Invitations, Status Management).
- **`:itinerary`**: **The Travel Companion**. Handles the granular trip schedule (Timeline, Trip Days, Activity Management, Tasks, and Notes).
- **`:booking`**: **The Booking Hub**. Manages travel reservations including Flights, Hotels, and Transportation with advanced intake methods.
- **`:budget`**: **The Finance Center**. Tracks trip expenses, manages budgets, and provides category-wise breakdown of spending.

## 🛠 Features

### User Lifecycle & Auth
- [x] **Branded Splash Screen**: Smooth transitions with intelligent persistent routing.
- [x] **Premium Onboarding**: 3-page interactive pager with detailed illustrations.
- [x] **Secure Auth**: Full Login/Registration system with OTP verification and real-time password strength feedback. Features **persistent sessions** and intelligent cross-device profile syncing.
- [x] **Profile Setup**: 5-step personalization wizard with dropdown preference selection. Automatically skips once completed.
- [x] **Permissions Wizard**: Branded **dialog-based** requests for system access, providing context before asking.

### Trip Workspace
- [x] **Trip Dashboard**: live-syncing list with 5 status categories (Upcoming, In Progress, Completed, Drafts, Archived).
- [x] **Search & Filter**: Real-time searching and tab-based status filtering.
- [x] **Guided Creation**: Multi-step flow (`Create Trip` -> `Add Details` -> `Invite People` -> `Trip Summary`).
- [x] **Rich Invitations**: Search from contacts or manual entry (Email/Phone) with immediate feedback.
- [x] **Lifecycle Management**: Move trips between statuses, archive, or delete via a centralized action menu.
- [x] **Dynamic Progress**: Real-time progress calculation based on task completion.

### Itinerary & Timeline
- [x] **Chronological Timeline**: Vertical high-fidelity view with status tracking.
- [x] **Day Management**: Monthly calendar view with day-by-day organization.
- [x] **Activity Deep Dive**: Specialized views for Flights, Tasks, and Notes.
- [x] **Universal Creation**: Speed Dial FAB for quick access to Events, Tasks, and Notes.
- [x] **Interactive Tasks**: Mark activities as completed directly from the timeline with live backend syncing.
- [x] **Advanced Filtering**: Filter itinerary by category and sort by priority or time.

### Booking Ecosystem
- [x] **Unified Booking List**: Aggregated view of all travel reservations across multiple trips.
- [x] **Multi-modal Intake**: Four ways to add bookings:
    - **Manual Entry**: High-fidelity form with Date/Time pickers and PostgreSQL JSONB-compatible details.
    - **PNR / Reference No**: Quick fetch directly from the backend via reference code.
    - **Scan Ticket**: AI-ready scanner frame for e-tickets and boarding passes.
    - **Import from Email**: Sync travel confirmations from Gmail and Outlook.
- [x] **Booking Details**: Comprehensive view with itinerary segments, airline/provider info, and total costs.
- [x] **Traveller Management**: Dynamic passenger list with support for adding/removing travellers and seat assignments.
- [x] **Management Actions**: Quick access to edit, share, delete, or manage baggage and seating.

### Budget & Expense Tracking
- [x] **Budget Overview**: High-fidelity summary card with total/spent amounts and real-time progress tracking.
- [x] **Category Breakdown**: Detailed spending analysis for Food, Transport, Activities, etc.
- [x] **Global Budget List**: Manage financial plans across all active and upcoming trips.
- [x] **Spending Trends**: Dynamic bar charts and AI-powered spending insights.
- [x] **Financial Reports**: Export trip expenses to PDF and CSV formats.
- [x] **AI Receipt Scanner**: High-fidelity animated camera interface for automatic expense entry.

### Travel Document Locker
- [x] **Secure Storage**: Encrypted locker for Passports, Visas, Tickets, and Insurance.
- [x] **High-Fidelity UI**: Interactive cards with favoriting, sharing, and expiry tracking.
- [x] **AI Document Scan**: Dark-themed scanner with alignment guides and auto-capture.
- [x] **Categories Grid**: Visual organization with real-time document counts per type.
- [x] **Batch Management**: Long-press selection mode for bulk actions.
- [x] **Granular Discovery**: Search and filter by category, expiry date, or issuer.

## 📖 Development Guidelines

- **MVI Pattern**: Every screen must extend `BaseViewModel` and handle intents reactively.
- **Design System**: Use `TripPointTheme.colorScheme` and `TripPointTheme.dimensions`. **Zero hardcoded strings** - use universal `strings.xml`.
- **Data Formats**: Use **ISO 8601 LocalDateTime** (`yyyy-MM-ddTHH:mm:ss`) for all API date-time fields to ensure backend compatibility.
- **Testing**: Maintain high logic coverage (current `:trip`, `:itinerary`, `:booking`, `:budget`, and `:documents` modules at 100%).
- **Linting**: Ensure all code is **Detekt** and **Ktlint** compliant before committing.

---

Built with ❤️ for travelers.
