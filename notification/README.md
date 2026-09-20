# Notification Module

The `:notification` module provides a comprehensive system for managing all user communications, trip alerts, and smart reminders within the TripPoint application.

## 🚀 Key Features

### 1. Unified Notification Center
*   **Categorized Feed**: Centralized list of all communications with high-fidelity filtering ("All", "Unread", "Mentions").
*   **Real-time Synchronization**: Fully integrated with GraphQL Subscriptions for instant live alerts.
*   **Immersive Detail View**: Category-specific detail screens with contextual imagery and data-driven key information (e.g., flight gate updates, booking confirmation codes).

### 2. Smart Reminders
*   **Actionable Dashboard**: Priority-ranked reminder system with dedicated tabs for Upcoming, Overdue, and Done items.
*   **Productivity Utilities**: Native support for "Snooze" and "Mark as Done" actions directly from the list.
*   **Visual Urgency**: Material 3 error states and high-contrast styling for overdue travel requirements (e.g., visa document submissions).

### 3. Preferences & Quiet Hours
*   **Omnichannel Toggles**: Granular control over Push, Email, In-app, and SMS delivery channels.
*   **Downtime Management**: Integrated "Quiet Hours" scheduling to prevent non-critical alerts during user-defined periods.
*   **Daily Digest**: Configurable summary alerts to reduce notification fatigue.

### 4. Searchable History
*   **Audit Trail**: Chronological log of past notifications categorized by date (Today, Yesterday, Older).
*   **Full-Text Search**: Instant search capability across notification titles and messages.
*   **Bulk Management**: Quick-clear utilities for history maintenance.

## 🏗 Architecture & Tech Stack

*   **Pattern**: MVI (Model-View-Intent) using `BaseViewModel`.
*   **UI**: 100% Jetpack Compose with Material 3 components.
*   **Networking**: Production-ready GraphQL integration via `NotificationRemoteDataSource`.
*   **Real-time**: Reactive updates using Kotlin `Flow` and `MutableSharedFlow`.
*   **Quality**: Achievements 100% logic coverage in unit tests.

## 📖 Integration

To use notifications in other modules, inject the `NotificationRepository` and use the following screen routes:

- `Screen.Notifications`: Primary Notification Center.
- `Screen.NotificationDetail`: Specific notification deep-dive.
- `Screen.NotificationPreferences`: User setting configuration.
- `Screen.Reminders`: Active task/reminder dashboard.

---
Built with Clean Architecture principles for maximum scalability.
