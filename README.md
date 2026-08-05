# TripPoint Android

TripPoint is a modern Android travel application built with Kotlin, Jetpack Compose, and Clean Architecture. It focuses on providing a seamless experience for planning and tracking trips.

## 🚀 Tech Stack

- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVI (Model-View-Intent) & Clean Architecture
- **Dependency Injection**: Hilt (Planned)
- **Networking**: Apollo GraphQL (Planned)
- **Local Storage**: Room & EncryptedSharedPreferences (Planned)
- **Navigation**: Navigation Compose
- **Design System**: Custom design system in `:core:designsystem`

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

### Modules

- **`:app`**: The main entry point. Handles top-level navigation and basic configuration.
- **`:authentication`**: Manages the user lifecycle (Splash, Onboarding, Login, Registration, OTP, Forgot Password).
- **`:core:common`**: Contains base components like `BaseViewModel` for MVI.
- **`:core:designsystem`**: The central repository for all UI components (`TripPointButton`, `TripPointTextField`, `OtpInput`, `PasswordStrengthIndicator`, `FullscreenStatusView`), tokens, and premium illustrations.
- **`:core:navigation`**: Centralized screen definitions and navigation routes.
- **`:core:network`**: Apollo GraphQL configuration (In progress).
- **`:core:database`**: Local data persistence.

## 🛠 Features (In Progress)

- [x] **Branded Splash Screen**: Smooth transitions with staged initialization.
- [x] **Premium Onboarding**: 3-page interactive pager with detailed, transparent-background illustrations.
- [x] **MVI Architecture**: Fully reactive UI using `UiState`, `UiIntent`, and `UiEffect`.
- [x] **Login System**: Email/Password validation, social login UI, and error/success states.
- [x] **Registration System**: Multi-field registration with real-time password strength feedback and complexity enforcement.
- [x] **OTP Verification**: Secure 6-digit code entry with an integrated resend timer and success transitions.
- [x] **Forgot Password Flow**: Complete recovery flow including email entry, link sent illustration, and secure password reset.
- [ ] **Trip Dashboard**.

## 📖 Development Guidelines

- **MVI Pattern**: Every screen must extend `BaseViewModel` and handle intents reactively.
- **Design System**: Use `TripPointTheme.colorScheme` and `TripPointTheme.dimensions`. Avoid hardcoded HEX colors.
- **Illustrations**: Place high-quality vectors in `:core:designsystem` using the `illustration_*.xml` naming convention.

---

Built with ❤️ for travelers.
