# Open Fashion — System Architecture & Design Blueprint

## 1. High-Level Ecosystem Overview

**Open Fashion** is designed as a decoupled, multi-client e-commerce ecosystem:
- **`backend/`**: Central single source of truth for business logic, persistence, authorization, and real-time state.
- **`mobile/`**: Customer-facing native Android mobile application optimized for ultra-smooth UI interactions and offline-first capabilities.
- **`admin/`**: Merchant- and administrator-facing Flutter application with responsive layouts running seamlessly on Desktop Web and Mobile.

```
                  ┌────────────────────────────────────────┐
                  │          PostgreSQL Database           │
                  │    (Prisma 6 Relational Models)        │
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
           │                                                  │
           └──────────────▲─────────────────────▲─────────────┘
                          │ (HTTPS / WSS)       │ (HTTPS / WSS)
                          │                     │
          ┌───────────────┴──────────┐   ┌──────┴────────────────────┐
          │  Customer Mobile Client  │   │  Admin Portal (Flutter)   │
          │  Native Android (Kotlin) │   │  Responsive Web & Mobile  │
          │  Jetpack Compose M3      │   │  Adaptive Layout Engine   │
          │  Clean Architecture+MVI  │   │  Dashboard, Inventory     │
          │  Retrofit & Room Cache   │   │  Order Tracking           │
          └──────────────────────────┘   └───────────────────────────┘
```

---

## 2. Confirmed Architecture

*(Only components currently confirmed and verified in the repository)*

- **Root Workspace**: 3 distinct folders (`backend/`, `mobile/`, `admin/`).
- **Mobile Foundation**: Gradle project with Android Gradle Plugin `9.4.0`, Kotlin `2.2.10`, Compose BOM `2026.02.01`, Material 3, minSdk `30`, targetSdk `37`.
- **Documentation Standards**: Root-level governance defined in `AGENTS.md` and `PROJECT_CONTEXT.md`.

---

## 3. Proposed Architecture

### 3.1. Communication & Boundary Rules
1. **Direct Database Access**: Only `backend/` communicates directly with PostgreSQL and Redis. No client application directly touches databases.
   - *Production Value*: Eliminates security vulnerabilities and enforces uniform business rules.
   - *Learning Value*: Teaches strict tier-isolation and database access security.
2. **API Contracts**: Communication occurs over HTTPS (REST API) for transactional commands/queries and WSS (Socket.io) for real-time order/inventory events.
   - *Production Value*: Standardized, versioned API contracts avoid client-breaking changes.
   - *Learning Value*: Teaches robust client-server contract synchronization.

---

### 3.2. Sub-System Architectural Layers

#### A. Backend Architecture (`backend/`)
- **Transport / Routing Layer**: Express 5 routers handling HTTP routes and middleware execution.
- **Validation Layer**: Zod schema validators intercepting requests *before* reaching business logic.
- **Controller Layer**: Extracts request payloads, calls services, formats HTTP response codes/envelopes.
- **Service Layer (Domain Logic)**: Pure business rules, pricing calculation, order processing, stock reservation.
- **Repository / Data Access Layer**: Prisma 6 ORM client executing typed queries against PostgreSQL and Redis.
- **Real-Time Gateway**: Socket.io server handling room-based broadcasting (e.g., admin order updates).

> **Why this design?**
> - *Production Value*: Separation of concerns allows testing business logic in isolation without spinning up HTTP servers.
> - *Learning Value*: Teaches how real enterprise Node.js applications avoid the "fat controller" anti-pattern.

#### B. Mobile Architecture (`mobile/`)
- **Presentation Layer (Jetpack Compose M3 + MVI)**:
  - UI Composables observe immutable `UiState` and emit explicit user `Intent`s.
  - `ViewModel` reduces intents and orchestrates use cases.
  - One-time side effects emitted as `UiEffect` (Navigation, Snackbars).
- **Domain Layer (Pure Kotlin)**:
  - `Model`s (pure domain entities).
  - `UseCase`s (single-responsibility operations e.g., `AddToCartUseCase`).
  - `Repository` interfaces (contracts defining data operations).
- **Data Layer**:
  - `RepositoryImpl` implementations orchestrating network & local cache.
  - `RemoteDataSource` (Retrofit HTTP interfaces + DTOs).
  - `LocalDataSource` (Room DB Entities + DAOs).

> **Why this design?**
> - *Production Value*: Decoupled presentation from data sources enables offline-first operation, seamless mocking, and crash-resilient UI state.
> - *Learning Value*: Teaches industry-standard Android Clean Architecture and predictable state flow with MVI.

#### C. Admin Architecture (`admin/` - Flutter Responsive Web & Mobile)
- **Responsive Presentation Layer**:
  - Adaptive layout wrappers (`LayoutBuilder`, Breakpoint observers).
  - Desktop Web UI: Collapsible `NavigationRail`, multi-column data tables, filter toolbars.
  - Mobile UI: Bottom `NavigationBar`, card-based list views, slide-over detail sheets.
- **Business Logic / State Layer**:
  - State management provider/cubit managing auth state, inventory tables, and order dispatch queues.
- **Service / Network Layer**:
  - HTTP client communicating with backend admin endpoints using bearer tokens.

> **Why this design?**
> - *Production Value*: Allows operations teams to manage the catalog and orders from office workstations or mobile devices on the go without maintaining two separate codebases.
> - *Learning Value*: Teaches responsive UI principles in Flutter, widget lifecycles, and multi-platform compilation.

---

### 3.3. Cross-Cutting Concerns

#### A. Authentication & Authorization
- **Mechanism**: Stateless JWT access tokens + Redis-backed refresh token rotation.
- **Role-Based Access Control (RBAC)**:
  - `CUSTOMER`: Access to mobile endpoints (profile, orders, cart, wishlist).
  - `ADMIN`: Access to administrative endpoints (products CRUD, category management, order statuses, analytics).
- *Production & Learning Value*: Teaches secure session management without database-heavy token lookups on every request.

#### B. Validation Ownership
- **Client-Side**: Immediate UX feedback (non-empty fields, valid email formatting).
- **Backend (Zod)**: **Authoritative validation**. Every request payload is strictly parsed and sanitized before reaching controllers.
- *Production & Learning Value*: Never trust client input; guarantees database integrity.

#### C. Error-Handling Paradigm
- **Backend**: Centralized asynchronous error-handling middleware returning standardized error envelopes:
  ```json
  {
    "success": false,
    "error": {
      "code": "RESOURCE_NOT_FOUND",
      "message": "Product with ID 123 does not exist"
    }
  }
  ```
- **Mobile & Admin**: Typed `Result<T>` or `NetworkResponse<T>` handling `Success`, `Error`, and `Loading` states predictably.

---

## 4. Open Architecture Questions
1. **Flutter State Management**: Evaluate `flutter_bloc` vs `flutter_riverpod` for the Admin app (`DECISION REQUIRED`).
2. **Real-Time Transport Protocol**: Confirm Socket.io room isolation model for customer vs admin event streams.
3. **Database Migration Strategy**: Standardize automated migration deployment scripts for Prisma in Docker.
