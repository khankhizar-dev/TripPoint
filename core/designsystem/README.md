# Module :core:designsystem

The **Design System** is the single source of truth for the app's visual identity. It contains all atoms, molecules, and organisms used across all feature modules.

## 🎨 Foundations

- **Colors**: Defined in `theme/Color.kt`, using branded tokens (Primary Blue #2563EB).
- **Typography**: Defined in `theme/Type.kt`, mapped to Material 3 tokens.
- **Dimensions**: Centrally managed standard spacing and component sizing.

## 🧩 Components

A growing library of reusable Jetpack Compose components:
- **Atoms**: `TripPointButton`, `TripPointTextField`, `TripStatusChip`.
- **Molecules**: `TripCard`, `OtpInput`, `PasswordStrengthIndicator`.
- **Templates**: `FullscreenStatusView`, `LoadingView`, `ErrorView`.

## 🖼 Assets

- **Universal Strings**: `src/main/res/values/strings.xml` serves as the universal localization file to ensure zero hardcoded strings in the codebase.
- **Illustrations**: Branded high-quality vectors with consistent naming (`illustration_*`).

## 🛠 Usage

Feature modules should strictly use the components and theme provided here:
```kotlin
TripPointTheme {
    TripPointButton(text = stringResource(R.string.my_label), ...)
}
```
Avoid using raw `MaterialTheme` or hardcoded Hex/DP values outside of this module.
