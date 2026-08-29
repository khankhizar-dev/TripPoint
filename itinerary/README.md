# Itinerary Module (`:itinerary`)

The **Itinerary module** is a core feature of TripPoint that provides travelers with a structured, chronological view of their journey. It handles the management of trip days, activities, tasks, and notes through a high-fidelity, interactive interface.

## 📱 Features

- **Chronological Timeline (Screen A)**: A vertical, high-fidelity timeline with interactive connectors, time markers, and status chips (ON TIME, CONFIRMED, etc.).
- **Trip Days & Calendar (Screen B)**: An interactive monthly view and day-by-day list with support for drag-and-drop reordering.
- **Event Deep Dive (Screen C)**: Branded activity details including flight-specific banners (PNR, Gates, Terminals), passenger lists, and activity descriptions.
- **Add Activity Flow (Screen D)**: A streamlined creation form for adding new itinerary events with native Material 3 Date and Time pickers.
- **Task Management (Screen E)**: A dedicated task creation wizard with a 3-level priority system (Low, Medium, High).
- **Notes Workspace (Screen F)**: A centralized space for trip-related notes with real-time search functionality.
- **Filter & Sort (Screen G)**: Comprehensive filtering options by category (Events, Tasks, Notes) and sorting preferences.

## 🏗 Architecture

This module strictly adheres to the project's **MVI (Model-View-Intent)** architecture:

- **Contracts**: Defined in `*Contract.kt`, centralizing `UiState`, `UiIntent`, and `UiEffect`.
- **ViewModels**: Extend `BaseViewModel`, handling business logic and repository interactions reactively.
- **Repository**: Backed by a GraphQL network layer with auto-mapping between DTOs and Domain models.
- **Data Safety**: Features "Auto-Day Creation" logic to ensure activities always have a valid parent trip day.

## 🌐 Network (GraphQL)

The module integrates 11 specialized GraphQL operations:
- **Days**: Get all, Get specific, Create, Update, Delete.
- **Activities**: Get for day, Get detail, Create, Update, Delete.
- **Interactions**: Dedicated mutation for marking activities as completed.

## 🧪 Quality & Coverage

The itinerary module maintains **100% test coverage** for its business logic.
- **Repository Tests**: `ItineraryRepositoryImplTest` verifies all GraphQL operations and error scenarios.
- **ViewModel Tests**: Exhaustive testing of state transitions and navigation side-effects using **Turbine** and **MockK**.

**Commands**:
- Run unit tests: `./gradlew :itinerary:testDebugUnitTest`
- Linting: `./gradlew :itinerary:ktlintCheck`
- Static Analysis: `./gradlew :itinerary:detekt`
