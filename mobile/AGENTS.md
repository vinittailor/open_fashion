# Open Fashion — Android Mobile Engineering Guidelines & Learning Rules

## 1. Domain & Responsibilities
The `mobile/` directory contains the customer-facing native Android mobile application. It is engineered with high visual fidelity, reactive state management, and robust offline caching.

**Strict Boundary Rule**: Never place Node.js, backend, Dart, or Flutter code inside this directory.

---

## 2. Technology Stack & Confirmed Versions
- **Language**: Kotlin `2.2.10`
- **Build System**: Gradle Version Catalog (`mobile/gradle/libs.versions.toml`), AGP `9.4.0`
- **SDK Target**: `compileSdk = 37`, `targetSdk = 37`, `minSdk = 30`
- **UI Toolkit**: Jetpack Compose with Material 3 (`androidx-compose-bom = 2026.02.01`)
- **Architecture**: Clean Architecture (Domain, Data, Presentation) + MVI (Model-View-Intent)
- **Networking**: Retrofit & OkHttp (`[Planned]`)
- **Local Persistence**: Room DB (`[Planned]`)
- **Concurrency**: Kotlin Coroutines & Flow

---

## 3. Clean Architecture & MVI Package Layout

```
com.example.open_fashion/
├── core/
│   ├── network/       # Retrofit builders, OkHttp interceptors, NetworkResult
│   ├── database/      # Room database instance & TypeConverters
│   ├── theme/         # Color.kt, Theme.kt, Type.kt (Material 3 tokens)
│   └── components/    # Reusable Compose buttons, inputs, loading skeletons
├── feature/
│   ├── auth/
│   ├── catalog/
│   ├── cart/
│   └── order/
│       ├── data/          # DTOs, DAOs, RepositoryImpl, RemoteDataSource
│       ├── domain/        # Pure Models, UseCases, Repository Interfaces
│       └── presentation/  # Composables, ViewModel, ScreenContract (State/Intent/Effect)
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
2. **`UiIntent` (Sealed Interface)**: Explicit user actions (e.g. `AddToCartClicked`, `SearchQueryChanged`).
3. **`UiEffect` (Sealed Interface)**: One-off side effects handled outside recomposition (e.g. `NavigateToCheckout`, `ShowSnackbar`).

---

## 5. Kotlin & Jetpack Compose Best Practices

### 5.1. Kotlin Language Standards
- **Null Safety**: Leverage safe calls (`?.`), Elvis operator (`?:`), and avoid `!!` assertions entirely.
- **Sealed Classes & Interfaces**: Use exhaustive `when` expressions for states, intents, and network results.
- **Extension Functions**: Write clean, readable domain converters (e.g., `ProductDto.toDomain()`).

### 5.2. Compose Recomposition & State
- State must always flow **down**, and events must flow **up** (State Hoisting).
- Use `remember` and `rememberSaveable` appropriately to retain state across recompositions and configuration changes.
- Avoid passing `ViewModel` instances into leaf Composables; pass state and event lambdas for maximum reusability and previewability (`@Preview`).

### 5.3. Coroutines & Lifecycle Safety
- Launch UI coroutines in `viewModelScope`.
- Collect Flows in Composables using `collectAsStateWithLifecycle()` from `androidx.lifecycle.runtime.compose` to prevent collecting events while the app is in the background.

---

## 6. Confirmed Gradle Commands
- Build Debug APK: `./gradlew assembleDebug`
- Run Unit Tests: `./gradlew test`
- Run Android UI Instrumentation Tests: `./gradlew connectedCheck`
- Clean Build Cache: `./gradlew clean`
