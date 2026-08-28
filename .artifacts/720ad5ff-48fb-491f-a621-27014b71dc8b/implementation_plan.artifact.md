# UI for Trip List and Trip Overview

Implement the Trip List and Trip Overview screens based on the high-fidelity designs, following the project's MVI architecture and design system guidelines.

## User Review Required

> [!IMPORTANT]
> - I will update the design system tokens (colors and typography) to match the provided developer handoff exactly.
> - New components will be added to `:core:designsystem`.
> - Screens will be implemented in `:app` under `com.android.trippoint.ui.trips`.
> - Strings will be added to the universal `strings.xml` in the `:app` module.

## Proposed Changes

---

### Core Components & Design System

#### [MODIFY] [Color.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/core/designsystem/src/main/java/com/android/trippoint/core/designsystem/theme/Color.kt)
Update color tokens to match the design handoff:
- Primary: #2563EB
- Secondary: #1D89B1
- Accent: #F59E0B
- Surface: #FFFFFF
- Background: #F8FAFC
- TextPrimary: #0F172A
- TextSecondary: #64748B
- Border: #E2E8F0
- Error: #EF4444
- Success: #22C55E

#### [MODIFY] [Type.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/core/designsystem/src/main/java/com/android/trippoint/core/designsystem/theme/Type.kt)
Update typography to match the design handoff:
- Display Large: 32/40 Bold
- Headline Medium: 20/28 SemiBold
- Title Medium: 16/24 Medium
- Body Large: 14/20 Regular
- Body Medium: 12/16 Regular
- Label: 11/16 Medium

#### [NEW] [TripCard.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/core/designsystem/src/main/java/com/android/trippoint/core/designsystem/components/TripCard.kt)
Implement the trip card component for the list.

#### [NEW] [TripStatusChip.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/core/designsystem/src/main/java/com/android/trippoint/core/designsystem/components/TripStatusChip.kt)
Implement status chips (Upcoming, In Progress, Completed).

---

### Data Models

#### [NEW] [Trip.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/core/common/src/main/java/com/android/trippoint/core/common/model/Trip.kt)
Define `Trip`, `Traveler`, and `TripStatus` models.

---

### Strings

#### [MODIFY] [strings.xml](file:///Users/khizarkhan/StudioProjects/TripPoint/app/src/main/res/values/strings.xml)
Add all strings for Trip List and Trip Overview (e.g., titles, tabs, labels, actions).

---

### Trip List Feature

#### [NEW] [TripListContract.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/app/src/main/java/com/android/trippoint/ui/trips/TripListContract.kt)
Define `State`, `Intent`, and `Effect` for the Trip List.

#### [NEW] [TripListViewModel.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/app/src/main/java/com/android/trippoint/ui/trips/TripListViewModel.kt)
Implement logic for fetching trips and handling intents.

#### [NEW] [TripListScreen.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/app/src/main/java/com/android/trippoint/ui/trips/TripListScreen.kt)
Implement the Compose UI for the Trip List.

---

### Trip Overview Feature

#### [NEW] [TripOverviewContract.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/app/src/main/java/com/android/trippoint/ui/trips/TripOverviewContract.kt)
Define `State`, `Intent`, and `Effect` for the Trip Overview.

#### [NEW] [TripOverviewViewModel.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/app/src/main/java/com/android/trippoint/ui/trips/TripOverviewViewModel.kt)
Implement logic for fetching trip details and handling intents.

#### [NEW] [TripOverviewScreen.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/app/src/main/java/com/android/trippoint/ui/trips/TripOverviewScreen.kt)
Implement the Compose UI for the Trip Overview.

---

## Verification Plan

### Automated Tests
- N/A (UI implementation focusing on Compose)

### Manual Verification
- Render Compose Previews for both screens.
- Verify that strings are correctly pulled from `strings.xml`.
- Verify that colors and typography match the design handoff.
