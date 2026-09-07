# Travel Documents Module

The `:documents` module provides a secure and organized "Document Locker" for storing and managing critical travel files.

## 🛠 Features

### Document Locker
- [x] **My Documents**: Aggregated list with search, recent files, and high-fidelity tab filtering (All, Recent, Favorites, Shared).
- [x] **Category Hub**: Grid-based navigation with real-time document counts for Passports, Visas, Tickets, etc.
- [x] **Multi-Select Mode**: Long-press to enter selection mode for batch sharing or deletion.

### Capture & Management
- [x] **Multi-modal Intake**: Upload from device, take photo, or import from cloud services.
- [x] **AI Document Scan**: Dark-themed camera interface with animated alignment guides and auto-capture support.
- [x] **Document Details**: Deep-dive view with metadata tracking (expiry alerts, reference numbers) and management actions (Share, Download, Favorite).

### Smart Search
- [x] **Granular Discovery**: Advanced search & filter screen with category, expiry, and issuer-based chips.

## 🏗 Architecture
- **MVI (Model-View-Intent)**: Robust reactive state handling across all screens.
- **Clean Architecture**: Decoupled domain models and repository patterns.
- **Detekt/Ktlint**: 100% compliant with project-wide static analysis rules.

## 🧪 Testing
- **100% Logic Coverage**: Comprehensive testing suite with 57 passed unit tests.
- Run tests: `./gradlew :documents:testDebugUnitTest`
