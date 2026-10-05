# Open Fashion — Project Context & Repository State

> **Protocol Rule**: This document captures the living state of the repository. It MUST be updated whenever the tech stack, directory structure, commands, or major project directions change.

---

## 1. Project Purpose
**Open Fashion** is an enterprise-grade e-commerce application consisting of a scalable REST & WebSocket backend, a modern native Android client with luxury UI design, and a cross-platform administrative dashboard.

## 2. Learning Purpose
This project is engineered from scratch as a structured deep-learning curriculum covering:
- **Backend**: Node.js event-loop, Express 5 REST API design, relational data modeling (PostgreSQL + Prisma 7), caching and concurrency (Redis), real-time events (Socket.io), request contract validation (Zod), and containerized microservices (Docker).
- **Mobile (Android)**: Advanced Kotlin, Jetpack Compose Material 3 design systems, Clean Architecture (Domain, Data, Presentation), MVI reactive state management, asynchronous coroutines/flows, HTTP networking (Retrofit), image streaming (Coil 3), and offline-first caching (Room DB).
- **Admin**: Modern Dart syntax, Flutter widget architecture, responsive dashboards, data tables, and administrative state management with Riverpod 2.0.
- **Full-Stack Integration**: End-to-end type safety, auth tokens (JWT/OAuth), role-based permissions, and real-time state synchronization.

---

## 3. Discovered & Confirmed Repository Structure

```
open_fashion/
├── .agents/
│   ├── rules/
│   │   └── project-workflow.md
│   └── skills/                # 9 Verified Local UI/UX & Design Skill Suites
├── admin/                     # [Status: Confirmed - Active]
│   ├── lib/
│   │   ├── core/
│   │   │   ├── constants/     # api_endpoints.dart, app_strings.dart, breakpoints.dart
│   │   │   ├── network/       # api_client.dart, auth_interceptor.dart
│   │   │   ├── storage/       # secure_storage_service.dart
│   │   │   ├── theme/         # app_colors.dart, app_typography.dart, app_theme.dart
│   │   │   └── widgets/       # luxury_button.dart, luxury_text_field.dart, luxury_badge.dart
│   │   ├── features/
│   │   │   ├── auth/          # login_screen.dart, forgot_password_screen.dart
│   │   │   ├── media/         # luxury_file_upload_widget.dart, file_model.dart, file_repository.dart
│   │   │   └── users/         # user_management_screen.dart, user_management_controller.dart
│   │   └── main.dart
│   ├── test/                  # 9 unit tests passing
│   └── pubspec.yaml
├── backend/                   # [Status: Confirmed - Active]
│   ├── prisma/
│   │   └── schema.prisma      # 11 models + enums (Prisma 7 + PostgreSQL 16)
│   ├── src/
│   │   ├── config/            # env.js, prisma.js, redis.js
│   │   ├── controllers/       # auth.controller.js, user.controller.js, file.controller.js
│   │   ├── middleware/        # auth.middleware.js, errorHandler.js, role.middleware.js, upload.js, validate.middleware.js
│   │   ├── routes/            # auth.routes.js, user.routes.js, file.routes.js, index.js
│   │   ├── services/          # auth.service.js, user.service.js, file.service.js
│   │   ├── utils/             # crypto.utils.js, jwt.utils.js, logger.js
│   │   ├── validations/       # auth.validation.js, user.validation.js
│   │   ├── app.js
│   │   └── server.js
│   ├── tests/                 # 19 Vitest unit tests passing
│   ├── uploads/               # Static user uploaded media directory
│   ├── docker-compose.yml
│   └── package.json
├── mobile/                    # [Status: Confirmed - Active]
│   ├── app/
│   │   ├── src/main/java/com/example/open_fashion/
│   │   │   ├── core/          # constants (ApiEndpoints, AppStrings), network (ApiClient), storage (TokenManager)
│   │   │   ├── features/
│   │   │   │   ├── auth/      # Login, Register, Forgot Password, Email Verification
│   │   │   │   ├── media/     # FileApiService, FileRepository, FileDto, FileItem
│   │   │   │   └── profile/   # ProfileScreen, ProfileViewModel, ProfileContract
│   │   │   ├── ui/
│   │   │   │   ├── components/# LuxuryButton, LuxuryTextField, LuxuryBadge, LuxuryCard
│   │   │   │   └── theme/     # Color, Type (Editorial Serif), Theme
│   │   │   └── MainActivity.kt
│   │   ├── build.gradle.kts
│   │   └── src/test/          # Android unit tests passing
│   ├── gradle/libs.versions.toml
│   └── build.gradle.kts
├── AGENTS.md                  # Strict assistant protocol & rules (Rule 1-10)
├── ARCHITECTURE.md            # Architectural blueprint
├── DECISIONS.md               # Architecture Decision Records (ADR-001 to ADR-007)
├── ROADMAP_CHECKLIST.md       # Master granular micro-slice roadmap
├── CHEATSHEET.md              # Engineering reference notes
└── COMMANDS_AND_API_CHEATSHEET.md # CLI & cURL request/response JSON cheat notes
```

---

## 4. Active Infrastructure & Database Ports
- **PostgreSQL 16**: Port `5433` (Docker container `open_fashion_postgres`)
- **Redis 7**: Port `6379` (Docker container `open_fashion_redis`)
- **Backend API Server**: Port `5000` (Node.js Express 5)
- **Android ADB Reverse**: `adb reverse tcp:5000 tcp:5000`

---

## 5. Completed Feature Slices
1. **Slice 1.1**: User Registration (Backend + Flutter Admin + Android Mobile).
2. **Slice 1.2**: User Login & JWT Session Management with Redis Token Rotation.
3. **Slice 1.3**: Protected Routes & User Profile (`/me`) with Role Guards (`ADMIN`/`CUSTOMER`).
4. **Slice 1.4**: Password Reset (SHA-256 Token) & Email Verification.
5. **Slice 1.5**: Admin User Management Data Table with Pagination & Ban Status.
6. **Slice 2.1**: Single File Upload & Media Storage Registry (Multer + Dio Progress + Android PhotoPicker & Coil).
7. **Design System**: Centralized Luxury Component Library & Editorial Serif Typography (ADR-007).
