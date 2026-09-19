# Open Fashion — Project Context & Repository State

> **Protocol Rule**: This document captures the living state of the repository. It MUST be updated whenever the tech stack, directory structure, commands, or major project directions change.

---

## 1. Project Purpose
**Open Fashion** is an enterprise-grade e-commerce application consisting of a scalable REST & WebSocket backend, a modern native Android client with high-end UI design, and a cross-platform administrative dashboard.

## 2. Learning Purpose
This project is engineered from scratch as a structured deep-learning curriculum covering:
- **Backend**: Node.js event-loop, Express 5 REST API design, relational data modeling (PostgreSQL + Prisma 7), caching and concurrency (Redis), real-time events (Socket.io), request contract validation (Zod), and containerized microservices (Docker).
- **Mobile (Android)**: Advanced Kotlin, Jetpack Compose Material 3 design systems, Clean Architecture (Domain, Data, Presentation), MVI reactive state management, asynchronous coroutines/flows, HTTP networking (Retrofit), and offline-first caching (Room DB).
- **Admin**: Modern Dart syntax, Flutter widget architecture, responsive dashboards, data tables, and administrative state management with Riverpod 2.0.
- **Full-Stack Integration**: End-to-end type safety, auth tokens (JWT/OAuth), role-based permissions, and real-time state synchronization.

---

## 3. Discovered & Confirmed Repository Structure

```
open_fashion/
├── .agents/
│   └── rules/
│       └── project-workflow.md# [Status: Confirmed - Active]
├── admin/                     # [Status: Confirmed - Initialized]
│   ├── lib/
│   │   ├── core/
│   │   │   ├── constants/breakpoints.dart
│   │   │   └── theme/app_colors.dart, app_typography.dart, app_theme.dart
│   │   └── main.dart
│   ├── test/widget_test.dart
│   └── pubspec.yaml
├── backend/                   # [Status: Confirmed - Initialized]
│   ├── src/
│   │   ├── config/env.js
│   │   ├── middleware/errorHandler.js
│   │   ├── utils/logger.js
│   │   ├── app.js
│   │   └── server.js
│   ├── .env.example
│   ├── .gitignore
│   ├── docker-compose.yml
│   └── package.json
├── mobile/                    # [Status: Confirmed - Initialized]
│   ├── app/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/example/open_fashion/
│   │       ├── MainActivity.kt
│   │       └── ui/theme/
│   ├── gradle/
│   │   └── libs.versions.toml
│   ├── build.gradle.kts
│   └── settings.gradle.kts
├── AGENTS.md                  # [Status: Confirmed]
├── ARCHITECTURE.md            # [Status: Confirmed]
├── CHEATSHEET.md              # [Status: Confirmed]
├── COMMANDS_AND_API_CHEATSHEET.md # [Status: Confirmed]
├── DECISIONS.md               # [Status: Confirmed]
├── PROJECT_CONTEXT.md         # [Status: Confirmed - Updated]
└── ROADMAP_CHECKLIST.md       # [Status: Confirmed - Updated]
```

---

## 4. Sub-Application Status & Confirmed Specifications

### 4.1. `backend/`
- **Current Status**: `Confirmed` (Foundation Complete & Live-Tested)
- **Runtime & Tools**:
  - Node.js: `v26.7.0` (ES Modules) `[Confirmed]`
  - Express: `^5.2.1` `[Confirmed]`
  - Prisma CLI & Client: `7.10.0` `[Confirmed]`
  - Docker Desktop: `29.8.0` `[Confirmed]`
  - PostgreSQL Container: `postgres:16-alpine` on port `5432` `[Confirmed - Healthy]`
  - Redis Container: `redis:7-alpine` on port `6379` `[Confirmed - Healthy]`
  - Socket.io: `^4.8.3` `[Confirmed]`
  - Zod: `^4.6.5` / `^3.24.2` `[Confirmed]`
  - Winston: `^3.19.0` `[Confirmed]`
- **Known Entry Points**:
  - `src/server.js` (HTTP + WebSocket Bootstrap) `[Confirmed]`
  - `src/app.js` (Express App Instance) `[Confirmed]`
- **Confirmed Commands**:
  - `npm run dev`: Start dev server with `--watch --env-file=.env` `[Confirmed]`
  - `npm test`: Vitest test runner `[Confirmed]`
  - `docker compose up -d`: Launch PostgreSQL + Redis `[Confirmed]`
- **Environment Variables (Names Only - No Values)**:
  - `NODE_ENV`, `PORT`, `DATABASE_URL`, `REDIS_URL`, `JWT_SECRET`, `JWT_EXPIRES_IN`, `JWT_REFRESH_SECRET`, `JWT_REFRESH_EXPIRES_IN`, `CORS_ORIGINS`

### 4.2. `mobile/`
- **Current Status**: `Confirmed` (Android Jetpack Compose Project Initialized)
- **Runtime & Tools**:
  - Android Gradle Plugin: `9.4.0` `[Confirmed]`
  - Kotlin: `2.2.10` `[Confirmed]`
  - Compile SDK: `37` `[Confirmed]`
  - Target SDK: `37` `[Confirmed]`
  - Min SDK: `30` `[Confirmed]`
  - Compose BOM: `2026.02.01` `[Confirmed]`
  - Version Catalog: `mobile/gradle/libs.versions.toml` configured with Retrofit, Room, Coroutines `[Confirmed]`
- **Companion Tool**:
  - Android Studio (Recommended external companion on host machine for Compose Interactive Previews, ADB, and visual Logcat).
- **Known Entry Points**:
  - `com.example.open_fashion.MainActivity` `[Confirmed]`
- **Confirmed Commands**:
  - `./gradlew assembleDebug`, `./gradlew test`, `./gradlew connectedCheck` `[Confirmed]`

### 4.3. `admin/`
- **Current Status**: `Confirmed` (Flutter Admin Foundation Complete & Tested)
- **Runtime & Tools**:
  - Flutter SDK: `3.41.1` `[Confirmed]`
  - Dart SDK: `3.11.0` `[Confirmed]`
  - State Management: `flutter_riverpod: ^2.6.1` (ADR-004) `[Confirmed]`
  - Typography: `google_fonts: ^6.2.1` (*Outfit* + *Inter*) `[Confirmed]`
  - Networking: `dio: ^5.8.0+1` `[Confirmed]`
  - Secure Storage: `flutter_secure_storage: ^9.2.4` `[Confirmed]`
  - Charts: `fl_chart: ^0.70.2` `[Confirmed]`
- **Known Entry Points**:
  - `lib/main.dart` (Responsive `AdminShellScreen` wrapped in `ProviderScope`) `[Confirmed]`
- **Confirmed Commands**:
  - `flutter run -d chrome` (Web Dashboard) `[Confirmed]`
  - `flutter test` (`All tests passed!`) `[Confirmed]`

---

## 5. Enabled Antigravity Skills & Integrations

| Skill / Integration Name | Scope | Purpose | Status |
|---|---|---|:---:|
| `riverpod-inspector` | `admin/` | State management inspection | Active |
| `vitest-supertest-runner` | `backend/` | In-memory API integration testing | Active |
| `line-by-line-pedagogy` | All Folders | Line-by-line concept breakdown | Active |
| `project-workflow-rule` | Workspace | Enforces strict mentor protocol | Active |
| `android-cli` | `mobile/` | Android SDK diagnostics & Gradle tasks | Active |
| `node-backend-runtime` | `backend/` | Node.js ESM execution & verification | Active |
| `prisma-cli-tools` | `backend/` | Database modeling & migrations | Active |
| `docker-compose-infra` | `backend/` | Containerized DB & Redis orchestration | Active |
| `flutter-sdk-plugin` | `admin/` | Multi-platform Flutter compilation | Active |
| `git-diff-analyzer` | Workspace | Diff-based code inspection | Active |
| `antigravity-guide` | Workspace | AGY tool reference | Active |
| `agy-customizations` | Workspace | Customization & rule management | Active |

---

## 6. External Integrations (Planned)
- **Payment Gateway**: Stripe / Razorpay sandbox `[Planned - DECISION REQUIRED]`
- **Image/Media Storage**: Cloudinary / AWS S3 `[Planned - DECISION REQUIRED]`
- **Push Notifications**: Firebase Cloud Messaging (FCM) `[Planned]`
