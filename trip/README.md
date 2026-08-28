# Module :trip

The **Trip Workspace** module is the core feature area of the application, managing everything related to travel planning and tracking.

## 📦 Features

- **Trip List**: A categorized dashboard (Upcoming, In Progress, Completed, Drafts, Archived) with real-time search.
- **Creation Wizard**: A multi-step flow for high-conversion trip setup:
  - **Basic Info**: Name, Destination, and native Date Picker integration.
  - **Modular Details**: Optional sections for Itinerary, Bookings, Tasks, etc.
  - **Invitations**: Search from contacts or manual entry (Email/Phone).
  - **Summary**: Final review with stat cards and traveler overview.
- **Trip Overview**: Detailed view with dynamic progress tracking and lifecycle status management.

## 🏗 Architecture

This module follows a strict **MVI (Model-View-Intent)** pattern:
- **Contract**: Defines the `UiState`, `UiIntent`, and `UiEffect` for each screen.
- **ViewModel**: Inherits from `BaseViewModel` to handle intents and emit state.
- **Repository**: Managed in `data/repository`, it interacts with the `TripRemoteDataSource` using GraphQL.

## 🧪 Testing

The module maintains **100% logic coverage** across all ViewModels and Repositories.
- **Unit Tests**: Located in `src/test/java`.
- **Key Tools**: MockK (mocking), Turbine (Flow testing).
- **Run command**: `./gradlew :trip:testDebugUnitTest`

## 🛠 Integration

To use this module in another part of the app:
1. Ensure the `:trip` project dependency is added.
2. Register the routes in the main `NavHost` using the definitions in `:core:navigation`.
3. Provide the `TripRepository` to the ViewModels via your DI pattern.
