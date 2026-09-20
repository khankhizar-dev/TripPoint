# TripPoint Android

TripPoint is a modern Android travel application built with Kotlin, Jetpack Compose, and Clean Architecture. It focuses on providing a seamless experience for planning and tracking trips.

## 🚀 Tech Stack

- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVI (Model-View-Intent) & Clean Architecture
- **Dependency Injection**: Hilt (Planned)
- **Networking**: Retrofit & GraphQL (**100% Production Migrated** for all modules)
- **Real-time**: GraphQL Subscriptions over WebSockets for Chat and Live Notifications
- **Local Storage**: Room & EncryptedSharedPreferences (`androidx.security:security-crypto` for secure session storage)
- **Navigation**: Navigation Compose (Modularized & Multi-module aware)
- **Design System**: Centralized design system in `:core:designsystem`

## ⚙️ CI/CD & Quality

The project uses a production-grade quality pipeline:
- **Linting**: Android Lint, **Detekt** (code smells), and **Ktlint** (formatting) ensure high code quality.
- **Testing**: Over **120 logic-based unit tests** achieving 100% coverage in core business modules.
- **Commands**:
    - Run all tests: `./gradlew testDebugUnitTest`
    - Check formatting: `./gradlew ktlintCheck`
    - Static analysis: `./gradlew detekt`

## 🏗 Architecture

The project follows a multi-module architecture to promote scalability and maintainability:

### Core Modules
- **`:core:common`**: Contains base components like `BaseViewModel` for MVI and universal domain models (`Trip`, `Traveler`).
- **`:core:designsystem`**: The central repository for all UI components, tokens, and premium illustrations.
- **`:core:navigation`**: Centralized screen definitions and navigation routes.
- **`:core:network`**: Unified GraphQL configuration and centralized remote data sources.
- **`:core:database`**: Local data persistence and secure preference management.

### Feature Modules
- **`:app`**: The main entry point. Handles top-level navigation and app initialization.
- **`:authentication`**: Manages the user lifecycle (Splash, Onboarding, Login, Registration, Profile Setup, Security).
- **`:trip`**: **The Trip Workspace**. Manages the end-to-end trip lifecycle, including Collaboration, Chat, and Activity Feeds.
- **`:itinerary`**: **The Travel Companion**. Handles the granular trip schedule, activities, tasks, and notes.
- **`:booking`**: **The Booking Hub**. Manages travel reservations with AI scanning and email import capabilities.
- **`:budget`**: **The Finance Center**. Tracks trip expenses, manages budgets, and provides spending analytics.
- **`:checklist`**: **The Plan Executor**. Smart, collaborative checklists with AI-powered suggestions.
- **`:notification`**: **The Communication Center**. Centralized hub for alerts, smart reminders, and channel management.

## 🛠 Features

### Collaboration & Social Hub
- [x] **Real-time Discussion**: Premium chat interface with adaptive message bubbles and an advanced **Reply System** for focused threads.
- [x] **Chronological Activity Feed**: Visual audit trail logging all trip actions (e.g., "Rohan updated task") with category-specific iconography.
- [x] **Member Management**: Unified workspace for inviting collaborators, assigning roles (Organizer, Editor, Viewer), and managing access.
- [x] **Omnichannel Invitations**: "Invite via Link" functionality that integrates with the native Android share sheet (WhatsApp, Slack, SMS, Email).

### Notification & Communication System
- [x] **Unified Notification Center**: High-fidelity feed categorized by "All", "Unread", and "Mentions" with real-time sync.
- [x] **Immersive Detail Views**: Rich notification cards with contextual imagery and data-driven key details (e.g., Flight Gate, New Departure Time).
- [x] **Smart Reminders**: Priority-based reminder dashboard with "One-Tap Actions" to snooze or mark as done.
- [x] **Channel Control**: Granular toggles for Push, Email, In-app, and SMS channels with integrated "Quiet Hours" and "Daily Digest" scheduling.
- [x] **Communication History**: Searchable audit log of past notifications with bulk clear utilities.

### User Lifecycle & Auth
- [x] **Secure Auth**: Full system with OTP verification and real-time backend synchronization. Features **persistent sessions** and cross-device profile syncing.
- [x] **Profile Setup**: 5-step personalization wizard with automated navigation skipping once completed.
- [x] **Permissions Wizard**: Modern **modal dialog-based** requests that provide context before triggering system prompts.

### Trip Workspace
- [x] **Trip Dashboard**: Live-syncing list with 5 status categories (Upcoming, In Progress, Completed, Drafts, Archived).
- [x] **Real-time Overview**: Command center featuring live **Budget vs. Spent** stats and chronological **Task Progress** bars.
- [x] **Lifecycle Management**: Move trips between statuses, archive, or delete via a centralized action menu.

### Booking Ecosystem
- [x] **Multi-modal Intake**: Four advanced ways to add bookings:
    - **Manual Entry**: High-fidelity form with PostgreSQL JSONB-compatible details.
    - **PNR / Reference No**: Instant fetch from backend via reference code.
    - **Scan Ticket**: AI-ready scanner frame for boarding passes and e-tickets.
    - **Import from Email**: Direct sync from Gmail/Outlook confirmations.
- [x] **Traveller Management**: Dynamic passenger list with seat assignment and provider tracking.

### Budget & Expense Tracking
- [x] **Intelligent Aggregation**: Real-time spending calculation cross-referencing individual transactions with budget category analytics.
- [x] **AI Receipt Scanner**: Animated camera interface for automated, data-aware expense entry.
- [x] **Financial Reports**: Export utility for trip expenses to PDF and CSV formats.

### Travel Document Locker
- [x] **Secure Storage**: Encrypted locker with favoriting, sharing, and proactive expiry tracking.
- [x] **AI Document Scan**: Dark-themed scanner with alignment guides and auto-capture logic.

### Smart Checklist Management
- [x] **Optimistic Batch Sync**: Highly responsive interactions with an intelligent **Floating Save Bar** for background GraphQL synchronization.
- [x] **Advanced Template System**: Three-tier template hierarchy (**SYSTEM**, **USER**, **TRIP**) for reusable travel blueprints.
- [x] **AI Packing Assistant**: Smart suggestions mapped to destination and month with logic-aware regeneration.
- [x] **Progress Analytics**: Circular dashboard with sectional breakdown and completion celebrations.

## 📖 Development Guidelines

- **MVI Pattern**: Every screen must extend `BaseViewModel` and handle state/effects reactively.
- **Design System**: Use `TripPointTheme.colorScheme`. **Zero hardcoded strings** - use universal `strings.xml`.
- **API Standards**: 
    - Use **GraphQL** for all network operations.
    - Use **ISO 8601 LocalDateTime** (`yyyy-MM-ddTHH:mm:ssZ`) for all timestamps.
- **Testing**: Maintain 100% logic coverage for all ViewModels, Repositories, and Data Sources.
- **Linting**: Ensure all code is **Detekt** and **Ktlint** compliant before committing.

---

Built with ❤️ for travelers.
