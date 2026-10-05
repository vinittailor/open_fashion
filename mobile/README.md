# Open Fashion — Native Android Mobile Client

> Luxury fashion e-commerce customer mobile application built with Native Android, Kotlin, Jetpack Compose Material 3, Clean Architecture, and MVI.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin `2.2.10`
- **Build System**: Gradle Version Catalog (`libs.versions.toml`), Android Gradle Plugin `9.4.0`
- **Min SDK**: `30` (Android 11) | **Target SDK**: `37` (Android 16)
- **UI Toolkit**: Jetpack Compose (Material 3) with custom 60/30/10 luxury fashion palette
- **Architecture**: Clean Architecture (Domain, Data, Presentation) + MVI (Model-View-Intent)
- **Design System**: Centralized [`LuxuryButton`](./app/src/main/java/com/example/open_fashion/ui/components/LuxuryButton.kt), [`LuxuryTextField`](./app/src/main/java/com/example/open_fashion/ui/components/LuxuryTextField.kt), [`LuxuryBadge`](./app/src/main/java/com/example/open_fashion/ui/components/LuxuryBadge.kt), [`LuxuryCard`](./app/src/main/java/com/example/open_fashion/ui/components/LuxuryCard.kt)
- **Image Loading**: Coil 3 (`coil.compose.AsyncImage`) with crossfade animations & luxury shimmer
- **Networking**: Retrofit 2, OkHttp 3, Kotlinx Serialization
- **Photo Picker**: Android 13+ Modern Photo Picker (`ActivityResultContracts.PickVisualMedia`)
- **Persistence**: Room Database (KSP) & Encrypted SharedPreferences (`TokenManager`)

---

## 📱 Project Directory Structure

```
mobile/app/src/main/java/com/example/open_fashion/
├── core/
│   ├── constants/         # ApiEndpoints, AppStrings
│   ├── network/           # ApiClient, AuthInterceptor, NetworkResult
│   └── storage/           # TokenManager (Encrypted session storage)
├── features/
│   ├── auth/              # Registration, Login, Forgot Password, OTP Verification
│   ├── media/             # FileApiService, FileRepository, DTOs, Domain models
│   └── profile/           # ProfileScreen, ProfileViewModel, ProfileContract
└── ui/
    ├── components/        # LuxuryButton, LuxuryTextField, LuxuryBadge, LuxuryCard
    └── theme/             # Color, Type (Serif pairing), Theme (Open_fashionTheme)
```

---

## 🚀 Getting Started

### 1. Reverse ADB Port (for local backend communication)
```bash
adb reverse tcp:5000 tcp:5000
```

### 2. Fast Kotlin Source Compilation
```bash
./gradlew compileDebugKotlin
```

### 3. Run Unit Tests
```bash
./gradlew test
```

### 4. Build Debug APK
```bash
./gradlew assembleDebug
```
