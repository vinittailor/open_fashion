# Open Fashion — Enterprise E-Commerce Ecosystem

<p align="center">
  <b>A Production-Grade Full-Stack E-Commerce Ecosystem & Structured Learning Curriculum</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Backend-Node.js%20(ESM)%20%7C%20Express%205-green.svg" alt="Backend" />
  <img src="https://img.shields.io/badge/Database-PostgreSQL%2016%20%7C%20Prisma%207-blue.svg" alt="Database" />
  <img src="https://img.shields.io/badge/Cache-Redis%207-red.svg" alt="Redis" />
  <img src="https://img.shields.io/badge/Mobile-Android%20Kotlin%20%7C%20Compose%20M3-3DDC84.svg" alt="Android" />
  <img src="https://img.shields.io/badge/Admin-Flutter%20%7C%20Riverpod%202.0-02569B.svg" alt="Flutter" />
  <img src="https://img.shields.io/badge/Infrastructure-Docker%20Compose-2496ED.svg" alt="Docker" />
</p>

---

## 🏛️ System Architecture Overview

Open Fashion is engineered as a decoupled, multi-client ecosystem:

```
                  ┌────────────────────────────────────────┐
                  │          PostgreSQL Database           │
                  │         (Prisma 7 ORM Models)          │
                  └──────────────────▲─────────────────────┘
                                     │
                  ┌──────────────────▼─────────────────────┐
                  │              Redis Cache               │
                  │    (Session / Blacklist / Cache)       │
                  └──────────────────▲─────────────────────┘
                                     │
           ┌─────────────────────────┴────────────────────────┐
           │                                                  │
           │           Backend Application (Express 5)        │
           │      • REST API Endpoints   • Socket.io Gateway  │
           │      • Zod Validation       • JWT / RBAC Auth    │
           │      • Multer File Registry • Static CDN Hosting │
           │                                                  │
           └──────────────▲─────────────────────▲─────────────┘
                          │ (HTTPS / WSS)       │ (HTTPS / WSS)
                          │                     │
          ┌───────────────┴──────────┐   ┌──────┴────────────────────┐
          │  Customer Mobile Client  │   │  Admin Portal (Flutter)   │
          │  Native Android (Kotlin) │   │  Responsive Web & Mobile  │
          │  Jetpack Compose M3      │   │  Adaptive Layout Engine   │
          │  Clean Architecture+MVI  │   │  Dashboard, Inventory     │
          │  Retrofit, Coil 3 & Room │   │  Order Tracking           │
          └──────────────────────────┘   └───────────────────────────┘
```

---

## 📂 Sub-Application Workspaces

### 1. [`backend/`](./backend/) — Core API & Real-Time Gateway
- **Runtime**: Node.js `v26.7.0` (Native ES Modules)
- **Framework**: Express 5 (native async error routing)
- **Database & Cache**: PostgreSQL 16 & Redis 7 via Docker Compose
- **ORM & Validation**: Prisma 7 & Zod
- **Media Engine**: Multer 2.4.0 with collision-proof naming and MIME whitelist validation
- **Real-Time Engine**: Socket.io WebSocket Gateway

### 2. [`mobile/`](./mobile/) — Customer Mobile App
- **Platform**: Native Android (Kotlin `2.2.10`, AGP `9.4.0`)
- **UI Toolkit**: Jetpack Compose (Material 3 with custom 60/30/10 luxury fashion palette)
- **Architecture**: Clean Architecture (Domain, Data, Presentation) + MVI (Model-View-Intent)
- **Design System**: Centralized [`LuxuryButton`](./mobile/app/src/main/java/com/example/open_fashion/ui/components/LuxuryButton.kt), [`LuxuryTextField`](./mobile/app/src/main/java/com/example/open_fashion/ui/components/LuxuryTextField.kt), [`LuxuryBadge`](./mobile/app/src/main/java/com/example/open_fashion/ui/components/LuxuryBadge.kt), [`LuxuryCard`](./mobile/app/src/main/java/com/example/open_fashion/ui/components/LuxuryCard.kt)
- **Networking & Cache**: Retrofit 2, OkHttp 3, Coil 3 Image Loading, and Room Database with KSP

### 3. [`admin/`](./admin/) — Cross-Platform Admin Dashboard
- **Framework**: Flutter `3.41.1` & Dart `3.11.0`
- **Target**: Multi-platform (Responsive Desktop Web & Mobile)
- **State Management**: `flutter_riverpod` (v2.6.1) with `AsyncNotifier`
- **Design System**: Centralized [`LuxuryButton`](./admin/lib/core/widgets/luxury_button.dart), [`LuxuryTextField`](./admin/lib/core/widgets/luxury_text_field.dart), [`LuxuryBadge`](./admin/lib/core/widgets/luxury_badge.dart)
- **Typography & UI**: Google Fonts (*Outfit* + *Inter*), `fl_chart`, Adaptive Breakpoints

---

## 🚀 Quick Start Guide

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed & running.
- [Node.js](https://nodejs.org/) (v20+ LTS).
- [Flutter SDK](https://flutter.dev/) (v3.24+).
- [Android Studio](https://developer.android.com/studio) or Android SDK with JDK 11+.

---

### 1. Start Infrastructure & Backend Server
```bash
cd backend

# 1. Start PostgreSQL 16 & Redis 7 containers
docker compose up -d

# 2. Copy environment template
cp .env.example .env

# 3. Install dependencies
npm install

# 4. Start backend in development mode (hot-reloading)
npm run dev
```
*Health check available at: `http://localhost:5000/health`*

---

### 2. Run Flutter Admin Dashboard (Web)
```bash
cd admin

# Fetch dependencies
flutter pub get

# Launch Web Dashboard on Chrome
flutter run -d chrome
```

---

### 3. Build & Run Android Mobile App
```bash
cd mobile

# Forward port 5000 from Android emulator/device to host machine
adb reverse tcp:5000 tcp:5000

# Build debug APK
./gradlew assembleDebug

# Compile Kotlin sources (fast verification)
./gradlew compileDebugKotlin

# Run unit tests
./gradlew test
```

---

## ⚡ Current Ecosystem Capabilities

| Feature Module | Backend (Express 5 + Prisma 7) | Flutter Admin (Riverpod 2.6) | Android Mobile (Compose M3) |
|---|:---:|:---:|:---:|
| **User Registration** | ✅ Completed | ✅ Completed | ✅ Completed |
| **User Login & JWT Tokens** | ✅ Completed | ✅ Completed | ✅ Completed |
| **Token Refresh & Auto-Login** | ✅ Completed | ✅ Completed | ✅ Completed |
| **User Profile (`/me`)** | ✅ Completed | ✅ Completed | ✅ Completed |
| **Role Guard (`ADMIN`/`CUSTOMER`)** | ✅ Completed | ✅ Completed | ✅ Completed |
| **Forgot & Reset Password** | ✅ Completed | ✅ Completed | ✅ Completed |
| **Email Verification** | ✅ Completed | ✅ Completed | ✅ Completed |
| **Single File Upload & Storage Registry** | ✅ Completed | ✅ Completed | ✅ Completed |
| **Centralized Luxury Design System** | N/A | ✅ Completed | ✅ Completed |
| **Product Media Gallery (Batch)** | 📍 Next Micro-Slice | ⏳ Planned | ⏳ Planned |
| **Product & Catalog Management** | ⏳ Planned | ⏳ Planned | ⏳ Planned |
| **Shopping Cart & Checkout** | ⏳ Planned | ⏳ Planned | ⏳ Planned |
| **Real-time Order Feed (Socket.io)** | ⏳ Planned | ⏳ Planned | ⏳ Planned |

---

## 📚 Living Project Documentation

- **[System Architecture Blueprint](./ARCHITECTURE.md)** — Architectural layers, data flow, and tier boundaries.
- **[Master Roadmap & Checklist](./ROADMAP_CHECKLIST.md)** — Granular micro-task tracking and learning milestones.
- **[Architecture Decision Records (ADRs)](./DECISIONS.md)** — Formal decision log with alternatives and trade-offs (ADR-001 to ADR-007).
- **[Senior Engineering Cheatsheet](./CHEATSHEET.md)** — Deep-dive notes on OS signals, Express 5, Docker, Winston, and Riverpod.
- **[Commands & API Cheatsheet](./COMMANDS_AND_API_CHEATSHEET.md)** — Daily CLI cheat commands and full cURL request/response JSON models.

---

## 📜 License
ISC License © 2026 Open Fashion Ecosystem.
