# Checklist Module

The Checklist module provides a smart and customizable way for travelers to plan their trips, ensuring they never forget essential items or tasks.

## ✨ Features

- **My Checklists**: Manage multiple checklists tied to specific trips. Track overall progress with live indicators.
- **Templates**: Start quickly with curated templates for various trip types like Weekend Getaways, Family Vacations, and Business Trips.
- **Checklist Details**: Deep-dive into specific checklists with sectional organization (e.g., Packing, Documents, Pre-trip).
- **Categorized Items**: Add items to logical categories like Clothing and Electronics. Supports essential tagging and personal notes.
- **Progress Dashboard**: Dedicated high-fidelity view for overall completion and section-wise status.
- **AI Packing Assistant**: Get intelligent item suggestions based on your destination and travel dates.
- **Smart Reminders**: Set specific alerts for critical items to stay on track.

## 🏗 Architecture

This module follows the **MVI (Model-View-Intent)** pattern and is built with **Clean Architecture** principles:

- **UI Layer**: Jetpack Compose screens and ViewModels.
- **Domain Layer**: Pure Kotlin models and repository interfaces.
- **Data Layer**: Repository implementations fetching data from GraphQL and local sources.

## 🎨 UI & Design

- **Design System**: Leverages premium components from `:core:designsystem` like `TripPointCircularProgress` and `TripPointInteractiveCard`.
- **Resources**: 100% localized via `strings.xml`. Zero hardcoded strings.
- **Animations**: Subtle transitions between states and high-fidelity progress updates.

## 🧪 Quality

- **Linting**: 100% compliant with **Detekt** and **Ktlint**.
- **Commands**:
    - Run tests: `./gradlew :checklist:testDebugUnitTest`
    - Check quality: `./gradlew :checklist:detekt :checklist:ktlintCheck`
