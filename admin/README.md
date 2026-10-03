# Open Fashion — Admin Portal (Flutter)

A cross-platform administrative dashboard engineered with Flutter for responsive Desktop Web and Mobile store operations.

---

## 🏛️ Architecture & State Management

- **Framework**: Flutter `3.41.1` & Dart `3.11.0`
- **State Management**: `flutter_riverpod` (v2.6.1) using `AsyncNotifier` pattern (see [ADR-004](../DECISIONS.md#adr-004-state-management-for-admin-dashboard-flutter-riverpod))
- **Networking**: `dio` with central interceptors and error mappings
- **Secure Persistence**: `flutter_secure_storage` for encrypted JWT storage
- **Design System**: Custom typography (*Outfit* display + *Inter* body), luxury palette (`#111111` Dark, `#D4AF37` Gold accent), and responsive breakpoints (`Mobile < 600px`, `Tablet 600-1024px`, `Desktop > 1024px`).
- **Centralized Constants**:
  - API Endpoints: [`lib/core/constants/api_endpoints.dart`](file:///c:/Vicky/open_fashion/admin/lib/core/constants/api_endpoints.dart)
  - UI Strings: [`lib/core/constants/app_strings.dart`](file:///c:/Vicky/open_fashion/admin/lib/core/constants/app_strings.dart)

---

## 🚀 Running the Dashboard

### 1. Web (Chrome)
```bash
flutter run -d chrome
```

### 2. Mobile Device / Emulator
```bash
flutter run
```

### 3. Run Test Suite
```bash
flutter test
```

---

## 📂 Directory Structure

```
lib/
├── core/
│   ├── constants/       # api_endpoints.dart, app_strings.dart, breakpoints.dart
│   ├── network/         # api_client.dart (Dio client with Auth interceptors)
│   ├── storage/         # secure_storage_service.dart
│   └── theme/           # app_colors.dart, app_typography.dart, app_theme.dart
├── features/
│   ├── auth/            # LoginScreen, ForgotPasswordScreen, AuthController, AuthRepository
│   ├── profile/         # Profile chip, UserRepository
│   ├── products/        # (Upcoming) Product CRUD & SKU variant management
│   └── orders/          # (Upcoming) Real-time Order feed & fulfillment
└── main.dart            # ProviderScope entry & responsive AdminShellScreen
```
