# Open Fashion — Step-by-Step Micro-Task Master Roadmap

> **Status Legend**:
> - `[ ]` Not Started
> - `[-]` In Progress
> - `[x]` Completed
> - `[?]` Blocked / Needs Decision

---

## 🗺️ Master Execution Flow & Strategy
Our development proceeds in structured, cohesive phases so each micro-task builds on the previous one without context gaps:

```
[Phase 1: Project Foundations] ➔ [Phase 2: Database & Docker] ➔ [Phase 3: Core Design Systems] ➔ [Phase 4: End-to-End Auth] ➔ [Phase 5: Catalog] ...
```

---

## Phase 1: Workspace Foundations & Scaffolding (All 3 Apps)

### 1.1 Documentation & Project Governance
- [x] **Micro-Task 1.1.1**: Root Agent Protocol (`/AGENTS.md`)
- [x] **Micro-Task 1.1.2**: Project Context & Living Repository State (`/PROJECT_CONTEXT.md`)
- [x] **Micro-Task 1.1.3**: System Architecture Blueprint (`/ARCHITECTURE.md`)
- [x] **Micro-Task 1.1.4**: Architecture Decision Records (`/DECISIONS.md` - ADR 001 to 004)
- [x] **Micro-Task 1.1.5**: Backend Agent Guidelines (`/backend/AGENTS.md`)
- [x] **Micro-Task 1.1.6**: Android Agent Guidelines (`/mobile/AGENTS.md`)
- [x] **Micro-Task 1.1.7**: Flutter Admin Agent Guidelines (`/admin/AGENTS.md`)
- [x] **Micro-Task 1.1.8**: Master Roadmap Checklist (`/ROADMAP_CHECKLIST.md`)
- [x] **Micro-Task 1.1.9**: Antigravity Environment & Skill Inspection (Enabled 12 approved local skills)
- [x] **Micro-Task 1.1.10**: Permanent Workspace Rule Configuration (`/.agents/rules/project-workflow.md`)
- [ ] **Micro-Task 1.1.11**: Android Studio Companion Setup (Recommended on host machine for Compose Previews, Logcat & ADB)

### 1.2 Flutter Admin App Foundation (`admin/`)
- [x] **Micro-Task 1.2.1**: Scaffold Flutter multi-platform project (`flutter create --platforms=web,android,ios .`)
- [x] **Micro-Task 1.2.2**: Configure `admin/pubspec.yaml` with Riverpod, Google Fonts, Dio, FlChart, Secure Storage
- [x] **Micro-Task 1.2.3**: Create directory structure (`lib/core/`, `lib/features/`, `lib/shared/`)
- [x] **Micro-Task 1.2.4**: Create Admin Design Tokens & Palette (`admin/lib/core/theme/app_colors.dart`)
- [x] **Micro-Task 1.2.5**: Create Typography & Text Themes (`admin/lib/core/theme/app_typography.dart`)
- [x] **Micro-Task 1.2.6**: Create Theme Provider & Responsive Breakpoints (`admin/lib/core/theme/app_theme.dart` & `breakpoints.dart`)
- [x] **Micro-Task 1.2.7**: Create Root Responsive App Entry & Shell (`admin/lib/main.dart` with `ProviderScope`)

### 1.3 Backend Foundation (`backend/`)
- [x] **Micro-Task 1.3.1**: Initialize `backend/package.json` with ES Modules and core scripts (`dev`, `start`, `test`)
- [x] **Micro-Task 1.3.2**: Install and configure runtime dependencies (`express@5`, `zod`, `dotenv`, `cors`, `helmet`, `morgan`, `winston`, `bcryptjs`, `jsonwebtoken`, `socket.io`, `ioredis`, `@prisma/client`, `prisma`, `vitest`, `supertest`)
- [x] **Micro-Task 1.3.3**: Configure `backend/.gitignore` and `backend/.env.example` (Environment schema without secrets)
- [x] **Micro-Task 1.3.4**: Configure local multi-container `backend/docker-compose.yml` (PostgreSQL 16 + Redis 7)
- [x] **Micro-Task 1.3.5**: Implement fail-fast Environment Validator (`backend/src/config/env.js` using Zod)
- [x] **Micro-Task 1.3.6**: Implement structured Logger utility (`backend/src/utils/logger.js`)
- [x] **Micro-Task 1.3.7**: Implement standard API Error Classes & Global Error Handler (`backend/src/middleware/errorHandler.js`)
- [x] **Micro-Task 1.3.8**: Implement Express 5 application instance (`backend/src/app.js` with security middleware & health check)
- [x] **Micro-Task 1.3.9**: Implement HTTP + WebSocket Server bootstrap entry point (`backend/src/server.js`)

### 1.4 Android Mobile Foundation (`mobile/`) ➔ 📍 CURRENT FOCUS
- [x] **Micro-Task 1.4.1**: Update Version Catalog `mobile/gradle/libs.versions.toml` (Retrofit, OkHttp, Room, Coroutines, Navigation Compose)
- [x] **Micro-Task 1.4.2**: Configure `mobile/app/build.gradle.kts` dependencies & plugins (KSP, Serialization)
- [ ] **Micro-Task 1.4.3**: Establish Clean Architecture package structure (`core/`, `domain/`, `data/`, `presentation/`)
- [x] **Micro-Task 1.4.4**: Implement Open Fashion Compose M3 Design Tokens (`Color.kt`, `Type.kt`, `Theme.kt`)

---

## Phase 2: Database Schema & Relational Data Modeling

### 2.1 Prisma 7 Configuration & Schemas (`backend/prisma/`)
- [ ] **Micro-Task 2.1.1**: Initialize Prisma (`prisma/schema.prisma` with PostgreSQL datasource)
- [ ] **Micro-Task 2.1.2**: Define User, Profile, and Role (`CUSTOMER`, `ADMIN`) models
- [ ] **Micro-Task 2.1.3**: Define Category, Product, ProductVariant, and ProductImage models
- [ ] **Micro-Task 2.1.4**: Define Cart, CartItem, Wishlist, and WishlistItem models
- [ ] **Micro-Task 2.1.5**: Define Order, OrderItem, and Payment models
- [ ] **Micro-Task 2.1.6**: Run first Prisma migration against Docker PostgreSQL
- [ ] **Micro-Task 2.1.7**: Create database seed script (`backend/prisma/seed.js`) for demo luxury fashion products

### 2.2 Redis Client & Cache Manager
- [ ] **Micro-Task 2.2.1**: Implement Redis connection pool & event listeners (`backend/src/config/redis.js`)
- [ ] **Micro-Task 2.2.2**: Implement Cache Service with TTL & Invalidation helpers (`backend/src/services/cache.service.js`)

---

## Phase 3: Authentication & Authorization Flow (Full-Stack)

### 3.1 Backend Auth Engine
- [ ] **Micro-Task 3.1.1**: Password hashing and JWT token utility (`src/utils/password.js`, `src/utils/jwt.js`)
- [ ] **Micro-Task 3.1.2**: Zod validation schemas for Register, Login, Refresh Token (`src/modules/auth/auth.schema.js`)
- [ ] **Micro-Task 3.1.3**: Auth Repository & Auth Service with Redis Refresh Token rotation
- [ ] **Micro-Task 3.1.4**: Auth Controller & Express Router (`/api/v1/auth`)
- [ ] **Micro-Task 3.1.5**: JWT Authentication Middleware & RBAC Permission Middleware (`src/middleware/auth.js`)

### 3.2 Flutter Admin Auth
- [ ] **Micro-Task 3.2.1**: Dio HTTP Client with JWT interceptor & token refresh (`admin/lib/core/network/api_client.dart`)
- [ ] **Micro-Task 3.2.2**: Auth State Notifier with Riverpod (`admin/lib/features/auth/providers/auth_provider.dart`)
- [ ] **Micro-Task 3.2.3**: Responsive Admin Login Screen for Web & Mobile (`admin/lib/features/auth/screens/login_screen.dart`)

### 3.3 Android Mobile Auth
- [ ] **Micro-Task 3.3.1**: Encrypted Token DataStore (`mobile/app/.../core/storage/TokenStorage.kt`)
- [ ] **Micro-Task 3.3.2**: Retrofit Auth Interceptor & API Service
- [ ] **Micro-Task 3.3.3**: Auth Domain & Data Layer (`AuthRepositoryImpl.kt`, `LoginUseCase.kt`)
- [ ] **Micro-Task 3.3.4**: Auth MVI Presentation (`AuthViewModel.kt`, `LoginScreen.kt` in Jetpack Compose)

---

## Phase 4: Product Catalog, Search & Inventory Management

### 4.1 Backend Catalog Engine
- [ ] **Micro-Task 4.1.1**: Category CRUD schemas, service, controller, and routes
- [ ] **Micro-Task 4.1.2**: Product CRUD with multi-variant management & Prisma transactions
- [ ] **Micro-Task 4.1.3**: Public paginated product catalog with dynamic filtering (price, brand, size, color)
- [ ] **Micro-Task 4.1.4**: Product Redis caching middleware with automated cache bust on updates

### 4.2 Flutter Admin Product Management
- [ ] **Micro-Task 4.2.1**: Product AsyncNotifier provider (`admin/lib/features/products/providers/`)
- [ ] **Micro-Task 4.2.2**: Responsive Product Data Table with pagination, search, and stock badges (Web)
- [ ] **Micro-Task 4.2.3**: Product List Card view (Mobile Admin)
- [ ] **Micro-Task 4.2.4**: Add/Edit Product Modal Dialog with multi-image URL/picker support

### 4.3 Android Mobile Product Experience
- [ ] **Micro-Task 4.3.1**: Catalog Domain & Data layers (Room caching + Retrofit remote paging)
- [ ] **Micro-Task 4.3.2**: Home Screen Composable (Banner Carousel, Category Pills, Featured Grid)
- [ ] **Micro-Task 4.3.3**: Product Detail Screen Composable (Variant selectors, Image Pager, Sticky Add-to-Cart)

---

## Phase 5: Cart, Wishlist & Checkout

### 5.1 Backend Cart & Order Engine
- [ ] **Micro-Task 5.1.1**: Cart & Wishlist API with atomic stock check
- [ ] **Micro-Task 5.1.2**: Order Creation with Prisma interactive `$transaction` (decrement stock + generate invoice)
- [ ] **Micro-Task 5.1.3**: Payment integration / Webhook receiver

### 5.2 Android Mobile Cart & Orders
- [ ] **Micro-Task 5.2.1**: Cart Screen with quantity steppers & animated swipe-to-delete
- [ ] **Micro-Task 5.2.2**: Checkout Screen with Address selector and Payment confirmation
- [ ] **Micro-Task 5.2.3**: Order History & Live Tracking Screen with timeline

### 5.3 Flutter Admin Order Dispatch
- [ ] **Micro-Task 5.3.1**: Orders Kanban & Data Table with status transition dropdowns
- [ ] **Micro-Task 5.3.2**: Order Detail Sheet with customer info, line items, and invoice generation

---

## Phase 6: Real-Time Features & Analytics

- [ ] **Micro-Task 6.1**: Socket.io Server Gateway setup with authenticated rooms
- [ ] **Micro-Task 6.2**: Admin Real-Time Metrics & Live Order Feed
- [ ] **Micro-Task 6.3**: Flutter Admin Analytics Charts with `fl_chart` (Revenue curves, Top categories)
- [ ] **Micro-Task 6.4**: Android Live Order Status Listener via Socket.io client

---

## Phase 7: Testing, CI/CD & Production Polish

- [ ] **Micro-Task 7.1**: Backend Unit & Integration Tests (Supertest + Vitest)
- [ ] **Micro-Task 7.2**: Android Unit Tests (Turbine + MockK) & Compose UI Tests
- [ ] **Micro-Task 7.3**: Flutter Widget Tests & Provider Tests
- [ ] **Micro-Task 7.4**: Production Dockerfile multi-stage builds & GitHub Actions workflow
- [ ] **Micro-Task 7.5**: Portfolio Documentation with architecture diagrams & live demo walkthroughs

---

## 📌 Current Status Summary
- **Current Milestone**: `Phase 1.4 — Android Mobile Foundation`
- **Next Micro-Task**: `Micro-Task 1.4.2 — mobile/app/build.gradle.kts configuration`
