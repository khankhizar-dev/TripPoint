# Module :authentication

The **Authentication** module handles the entire user onboarding and security lifecycle, ensuring a secure and branded entry point into the app.

## 📦 Features

- **Splash & Routing**: Intelligent initialization that routes users based on their session state (Auth -> Setup -> Permissions -> Home).
- **Onboarding**: A premium 3-page interactive guide for new users.
- **Identity Management**:
  - Secure Login & Registration.
  - OTP (One-Time Password) verification with resend logic.
  - Forgot Password & Reset recovery flows.
- **Profile Setup**: A 5-step personalization wizard to collect user preferences and metadata.
- **Permissions**: A dedicated UI to handle system-level permission requests (Location, Notifications, etc.) with branded context.

## 🏗 Architecture

Follows the standard project-wide **MVI** structure. Each sub-feature (e.g., `login`, `register`) has its own Contract, ViewModel, and Screen.

## 🛠 Quality

- Fully compliant with **Detekt** and **Ktlint**.
- Verified with unit tests covering complex routing and validation logic.
- Run command: `./gradlew :authentication:testDebugUnitTest`
