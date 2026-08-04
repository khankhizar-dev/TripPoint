# TripPoint Android

TripPoint is a modern Android travel application built with Kotlin, Jetpack Compose, and Clean Architecture. It focuses on providing a seamless experience for planning and tracking trips.

## 🚀 Tech Stack

- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVI (Model-View-Intent) & Clean Architecture
- **Dependency Injection**: Hilt (Planned)
- **Networking**: Apollo GraphQL
- **Local Storage**: Room & EncryptedSharedPreferences
- **Navigation**: Navigation Compose
- **Design System**: Custom design system in `:core:designsystem`

## ⚙️ CI/CD

The project uses **GitHub Actions** for continuous integration and delivery:
- **Build & Test**: Automatically triggered on every push to `main` or `develop` branches and on pull requests.
- **Artifacts**: Debug APKs are generated and uploaded as artifacts for every successful build.
- **Linting**: Android Lint, **Detekt** (code smells), and **Ktlint** (formatting) are run to ensure high code quality.
- **Code Coverage**: JaCoCo is integrated to provide test coverage reports for all modules.

## 🏗 Architecture

The project follows a multi-module architecture to promote scalability and maintainability:

### Modules

- **`:app`**: The main entry point of the application. Handles top-level configuration and MainActivity.
- **`:authentication`**: Manages the user lifecycle (Splash, Login, Registration).
- **`:core:common`**: Contains base classes, utilities, and common interfaces used across modules.
- **`:core:designsystem`**: The central repository for all UI components, tokens (colors, typography, spacing), and themes.
- **`:core:navigation`**: Centralized navigation definitions and screen definitions.
- **`:core:network`**: Apollo GraphQL configuration and networking logic.
- **`:core:database`**: Local data persistence using Room.

## 🛠 Features (In Progress)

- [x] Branded Splash Screen with custom illustration and smooth transitions.
- [ ] Authentication System (Email/Password, Social Login).
- [ ] Trip Dashboard.
- [ ] Destination Exploration.

## 📖 Development Guidelines

- **MVI Pattern**: Every screen should follow the `UiState`, `UiIntent`, and `UiEffect` pattern using `BaseViewModel`.
- **Theming**: Always use `TripPointTheme.colorScheme` and `TripPointTheme.dimensions` instead of hardcoded values.
- **Localization**: All strings must be kept in module-specific `strings.xml` files.

---

Built with ❤️ for travelers.
