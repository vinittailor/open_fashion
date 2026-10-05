# Open Fashion — Flutter Admin Dashboard (Web & Mobile)

> Cross-platform responsive administrative back-office portal built with Flutter, Riverpod 2.6, Dio, and modern luxury design system components.

---

## 🛠️ Tech Stack & Architecture

- **Framework**: Flutter `3.41.1` & Dart `3.11.0`
- **Target Platforms**: Responsive Desktop Web (Chrome/Edge/Safari) & Mobile (iOS/Android)
- **State Management**: `flutter_riverpod` (v2.6.1) with `StateNotifier` / `AsyncNotifier`
- **Design System**: Centralized [`LuxuryButton`](./lib/core/widgets/luxury_button.dart), [`LuxuryTextField`](./lib/core/widgets/luxury_text_field.dart), [`LuxuryBadge`](./lib/core/widgets/luxury_badge.dart), [`LuxuryFileUploadWidget`](./lib/features/media/presentation/widgets/luxury_file_upload_widget.dart)
- **Networking**: Dio (Multipart uploads, JWT Auth interceptor, stream progress callbacks)
- **Data Tables & Charts**: Custom responsive data tables with pagination, `fl_chart`
- **Storage**: `flutter_secure_storage` (JWT access/refresh tokens)

---

## 📁 Project Directory Structure

```
admin/lib/
├── core/
│   ├── constants/         # ApiEndpoints, AppStrings, Breakpoints
│   ├── network/           # ApiClient, AuthInterceptor
│   ├── storage/           # TokenStorage (Secure storage)
│   ├── theme/             # AppColors, AppTypography, AppTheme
│   └── widgets/           # LuxuryButton, LuxuryTextField, LuxuryBadge
└── features/
    ├── auth/              # Admin Login, Forgot Password
    ├── media/             # File upload widget, file models & repository
    └── users/             # User management data table, status toggle, role filter
```

---

## 🚀 Getting Started

### 1. Install Dependencies
```bash
flutter pub get
```

### 2. Run Admin Web in Chrome
```bash
flutter run -d chrome
```

### 3. Run Unit & Widget Tests
```bash
flutter test
```
