# Implementation Plan - Unit Testing for 100% Coverage

This plan covers the implementation and update of unit tests for the core authentication and networking components to ensure 100% coverage of the recently added logic.

## Proposed Changes

### [Core Network]

#### [MODIFY] [AuthRemoteDataSourceTest.kt](file:///D:/Workspace/TripPoint/android/core/network/src/test/java/com/android/trippoint/core/network/AuthRemoteDataSourceTest.kt)
- Update mock JSON responses to match the new `User` model (including `profilePhotoUrl`, `country`, etc.).
- Add test case for `updateProfile` mutation.
- Update `getMe` test case to include new profile fields.

### [Authentication Domain & Data]

#### [NEW] [AuthRepositoryImplTest.kt](file:///D:/Workspace/TripPoint/android/authentication/src/test/java/com/android/trippoint/authentication/data/repository/AuthRepositoryImplTest.kt)
- Test all repository methods: `login`, `register`, `updateProfile`, `verifyEmailOtp` (including the `111111` bypass), `getMe`, `logout`, and persistence methods.
- Mock `AuthRemoteDataSource` and `PreferencesManager`.

#### [NEW] [GetMeUseCaseTest.kt](file:///D:/Workspace/TripPoint/android/authentication/src/test/java/com/android/trippoint/authentication/domain/usecase/GetMeUseCaseTest.kt)
- Test the simple invocation of `repository.getMe()`.

### [ViewModels]

#### [MODIFY] [LoginViewModelTest.kt](file:///D:/Workspace/TripPoint/android/authentication/src/test/java/com/android/trippoint/authentication/login/LoginViewModelTest.kt)
- Update successful login test to assert `NavigateToHome` with the `isProfileComplete` boolean.

#### [MODIFY] [RegisterViewModelTest.kt](file:///D:/Workspace/TripPoint/android/authentication/src/test/java/com/android/trippoint/authentication/register/RegisterViewModelTest.kt)
- Update navigation to `NavigateToOtp` instead of `NavigateToHome`.

#### [MODIFY] [ProfileSetupViewModelTest.kt](file:///D:/Workspace/TripPoint/android/authentication/src/test/java/com/android/trippoint/authentication/profilesetup/ProfileSetupViewModelTest.kt)
- Update `saveProfile` test to verify call to `authRepository.updateProfile(input)` instead of the old mock logic.
- Verify name splitting logic (firstName/lastName).

#### [NEW] [HomeViewModelTest.kt](file:///D:/Workspace/TripPoint/android/app/src/test/java/com/android/trippoint/ui/home/HomeViewModelTest.kt)
- Test `loadUser` success and failure states.
- Test `logout` navigation.

### [OTP]

#### [MODIFY] [OtpViewModelTest.kt](file:///D:/Workspace/TripPoint/android/authentication/src/test/java/com/android/trippoint/authentication/otp/OtpViewModelTest.kt)
- Ensure tests pass with the new default OTP `111111`.

## Verification Plan

### Automated Tests
- Run all unit tests in the `:core:network`, `:authentication`, and `:app` modules.
- Command: `./gradlew test`
