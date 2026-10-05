# Open Fashion — Android Mobile Engineering Guidelines & Learning Rules

## 1. Domain & Responsibilities
The `mobile/` directory contains the customer-facing native Android mobile application. It is engineered with high visual fidelity, reactive state management, centralized luxury design system components, and robust offline caching.

**Strict Boundary Rule**: Never place Node.js, backend, Dart, or Flutter code inside this directory.

---

## 2. Technology Stack & Confirmed Versions
- **Language**: Kotlin `2.2.10`
- **Build System**: Gradle Version Catalog (`mobile/gradle/libs.versions.toml`), AGP `9.4.0`
- **SDK Target**: `compileSdk = 37`, `targetSdk = 37`, `minSdk = 30`
- **UI Toolkit**: Jetpack Compose with Material 3 (`androidx-compose-bom = 2026.02.01`)
- **Architecture**: Clean Architecture (Domain, Data, Presentation) + MVI (Model-View-Intent)
- **Design System**: Centralized [`LuxuryButton`](./app/src/main/java/com/example/open_fashion/ui/components/LuxuryButton.kt), [`LuxuryTextField`](./app/src/main/java/com/example/open_fashion/ui/components/LuxuryTextField.kt), [`LuxuryBadge`](./app/src/main/java/com/example/open_fashion/ui/components/LuxuryBadge.kt), [`LuxuryCard`](./app/src/main/java/com/example/open_fashion/ui/components/LuxuryCard.kt)
- **Image Loading**: Coil 3 (`coil.compose.AsyncImage`) with crossfade animations
- **Networking**: Retrofit 2 & OkHttp 3 with Kotlinx Serialization
- **Photo Picker**: Android 13+ Modern Photo Picker (`ActivityResultContracts.PickVisualMedia`)
- **Local Persistence**: Room DB with KSP & Encrypted DataStore (`TokenManager`)
- **Concurrency**: Kotlin Coroutines & Flow

---

## 3. Clean Architecture & MVI Package Layout

```
com.example.open_fashion/
├── core/
│   ├── constants/     # ApiEndpoints, AppStrings
│   ├── network/       # Retrofit builders, OkHttp interceptors, NetworkResult
│   ├── storage/       # TokenManager (Encrypted session storage)
│   └── database/      # Room database instance & TypeConverters
├── features/
│   ├── auth/          # DTOs, Repository, ViewModels, Screens
│   ├── media/         # FileApiService, FileRepository, DTOs, Domain models
│   └── profile/       # ProfileScreen, ProfileViewModel, ProfileContract
├── ui/
│   ├── components/    # LuxuryButton, LuxuryTextField, LuxuryBadge, LuxuryCard
│   └── theme/         # Color.kt (60/30/10), Type.kt (Editorial Serif), Theme.kt
└── MainActivity.kt
```

---

## 4. MVI (Model-View-Intent) Pattern Rules

Every Compose screen is governed by a strict unidirectional contract:

```
          ┌────────────────────────────────────────────────┐
          │                                                │
          ▼                                                │
   [UI Composable] ──(User Intent)──► [ViewModel]          │
          ▲                               │                │
          │                               ▼                │
          │                        [Domain UseCase]        │
          │                               │                │
          │                               ▼                │
          │                        [Data Repository]       │
          │                               │                │
          │ (New Immutable UiState)        │                │
          └───────────────────────────────┴────────────────┘
          │                                                │
          └───────────(One-Time UiEffect)──────────────────┘
```

1. **`UiState` (Immutable Data Class)**: Holds the complete, single source of truth for the screen at any instant.
2. **`UiIntent` (Sealed Interface)**: Explicit user actions (e.g. `AddToCartClicked`, `OnAvatarSelected`).
3. **`UiEffect` (Sealed Interface)**: One-off side effects handled outside recomposition (e.g. `NavigateToCheckout`, `ShowSnackbar`).

---

## 5. Kotlin & Jetpack Compose Best Practices

### 5.1. Mandatory Centralized Design System Component Reuse (ADR-007)
- **Never write raw, ad-hoc Material/Compose buttons, text fields, cards, or badges directly inside screen files.**
- All presentation screens must strictly reuse `LuxuryButton`, `LuxuryTextField`, `LuxuryBadge`, and `LuxuryCard`.

### 5.2. Kotlin Language Standards
- **Null Safety**: Leverage safe calls (`?.`), Elvis operator (`?:`), and avoid `!!` assertions entirely.
- **Sealed Classes & Interfaces**: Use exhaustive `when` expressions for states, intents, and network results.
- **Extension Functions**: Write clean, readable domain converters (e.g., `FileDto.toDomain()`).

### 5.3. Compose Recomposition & State Hoisting
- State must always flow **down**, and events must flow **up** (State Hoisting).
- Separate stateful wrapper composables (`Screen`) from stateless rendering composables (`Content`) to enable instant `@Preview` support in Android Studio.

### 5.4. Coroutines & Lifecycle Safety
- Launch UI coroutines in `viewModelScope`.
- Collect Flows in Composables using `collectAsStateWithLifecycle()` from `androidx.lifecycle.runtime.compose` to prevent collecting events while the app is in the background.

---

## 6. Confirmed Gradle Commands
- Build Debug APK: `./gradlew assembleDebug`
- Fast Kotlin Compile: `./gradlew compileDebugKotlin`
- Run Unit Tests: `./gradlew test`
- Run UI Instrumentation Tests: `./gradlew connectedCheck`
- Clean Build Cache: `./gradlew clean`
