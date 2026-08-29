# TripPoint Android

TripPoint is a modern Android travel application built with Kotlin, Jetpack Compose, and Clean Architecture. It focuses on providing a seamless experience for planning and tracking trips.

## 🚀 Tech Stack

- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVI (Model-View-Intent) & Clean Architecture
- **Dependency Injection**: Hilt (Planned)
- **Networking**: Retrofit & GraphQL (Standardized via `GraphQlRequest`)
- **Local Storage**: Room & EncryptedSharedPreferences (`androidx.security:security-crypto`)
- **Navigation**: Navigation Compose
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

## 🛠 Features

### User Lifecycle & Auth
- [x] **Branded Splash Screen**: Smooth transitions with intelligent persistent routing.
- [x] **Premium Onboarding**: 3-page interactive pager with detailed illustrations.
- [x] **Secure Auth**: Full Login/Registration system with OTP verification and real-time password strength feedback.
- [x] **Profile Setup**: 5-step personalization wizard with dropdown preference selection.
- [x] **Permissions Wizard**: Branded step-by-step requests for system access.

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

## 📖 Development Guidelines

- **MVI Pattern**: Every screen must extend `BaseViewModel` and handle intents reactively.
- **Design System**: Use `TripPointTheme.colorScheme` and `TripPointTheme.dimensions`. **Zero hardcoded strings** - use universal `strings.xml`.
- **Testing**: Maintain high logic coverage (current `:trip` and `:itinerary` modules at 100%).

---

Built with ❤️ for travelers.
