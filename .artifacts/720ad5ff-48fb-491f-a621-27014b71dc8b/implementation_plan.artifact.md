# UI for Timeline/Itinerary Module

Implement the Day View and Trip Days UI for the new `:itinerary` module based on the high-fidelity designs.

## User Review Required

> [!IMPORTANT]
> - A new module `:itinerary` has been created.
> - The design for "Day View" (chronological vertical timeline) and "Trip Days" (calendar + day list) are being implemented.
> - Mock data is used for now as per the "start ui" instruction.

## Proposed Changes

---

### Project Structure

#### [NEW] [Module :itinerary](file:///Users/khizarkhan/StudioProjects/TripPoint/itinerary)
Created a new Android Library module for itinerary features.

#### [MODIFY] [settings.gradle.kts](file:///Users/khizarkhan/StudioProjects/TripPoint/settings.gradle.kts)
Included `:itinerary` module.

#### [MODIFY] [app/build.gradle.kts](file:///Users/khizarkhan/StudioProjects/TripPoint/app/build.gradle.kts)
Added `:itinerary` as a dependency.

---

### Navigation

#### [MODIFY] [Screen.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/core/navigation/src/main/java/com/android/trippoint/core/navigation/Screen.kt)
Add routes:
- `TripDays`: "trip_days/{tripId}"
- `Timeline`: "timeline/{tripId}/{date}"

#### [MODIFY] [MainActivity.kt](file:///Users/khizarkhan/StudioProjects/TripPoint/app/src/main/java/com/android/trippoint/MainActivity.kt)
Add `composable` entries for the new routes.

---

### Itinerary Features

#### [NEW] [Timeline Models](file:///Users/khizarkhan/StudioProjects/TripPoint/itinerary/src/main/java/com/android/trippoint/itinerary/domain/model/TimelineModels.kt)
Defined `TimelineEvent` and `TripDay` models.

#### [NEW] [Day View](file:///Users/khizarkhan/StudioProjects/TripPoint/itinerary/src/main/java/com/android/trippoint/itinerary/day)
Implemented `TimelineContract`, `TimelineViewModel`, and `TimelineScreen` with a vertical chronological timeline.

#### [NEW] [Trip Days](file:///Users/khizarkhan/StudioProjects/TripPoint/itinerary/src/main/java/com/android/trippoint/itinerary/list)
Implemented `TripDaysContract`, `TripDaysViewModel`, and `TripDaysScreen` with a month calendar and day list.

---

## Verification Plan

### Manual Verification
- Navigate to Trip Days screen and verify the calendar and list layout.
- Click on a day and verify navigation to the Day View (Timeline).
- Verify the vertical timeline styling (colors, dots, connectors).
