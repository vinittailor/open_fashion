# Open Fashion — Senior Engineering Concepts & Learning Cheatsheet

> **Purpose**: This living cheatsheet preserves all deep-dive engineering principles, architectural patterns, and computer science fundamentals explained throughout the Open Fashion ecosystem.

---

## 📚 Table of Contents
1. [Operating System & Process Management (`SIGINT` vs `SIGTERM`)](#1-operating-system--process-management-sigint-vs-sigterm)
2. [Node.js Architecture: Why Split `app.js` and `server.js`?](#2-nodejs-architecture-why-split-appjs-and-serverjs)
3. [Containerization: Why Docker over Local Host Installs?](#3-containerization-why-docker-over-local-host-installs)
4. [Logging Architecture: Winston Severity Levels & Production Observability](#4-logging-architecture-winston-severity-levels--production-observability)
5. [State Management Mental Models: BLoC vs Riverpod 2.0](#5-state-management-mental-models-bloc-vs-riverpod-20)
6. [Error Handling: Express 5 Unhandled Rejections & Central Error Pipeline](#6-error-handling-express-5-unhandled-rejections--central-error-pipeline)
7. [Validation: Authoritative Zod Schemas at System Boundaries](#7-validation-authoritative-zod-schemas-at-system-boundaries)
8. [Redis Key-Value Lifecycle & Namespace Patterns](#8-redis-key-value-lifecycle--namespace-patterns)
9. [Cryptography in Production: Bcrypt vs SHA-256 Token Hashing](#9-cryptography-in-production-bcrypt-vs-sha-256-token-hashing)
10. [Flutter Riverpod: `AsyncNotifier` Mental Model & State Flow](#10-flutter-riverpod-asyncnotifier-mental-model--state-flow)

---

## 1. Operating System & Process Management (`SIGINT` vs `SIGTERM`)

### What are POSIX Signals?
Standard operating system communication signals sent by OS kernels, terminals, Docker, or Kubernetes to running processes.

| Signal | Full Name | Triggered By | Meaning & Purpose |
|---|---|---|---|
| **`SIGINT`** | Signal Interrupt | Developer pressing `Ctrl + C` in terminal | Polite human request to halt the process. |
| **`SIGTERM`** | Signal Terminate | Docker (`docker stop`), Kubernetes, Cloud host | Polite automated request to shut down during redeployments. |
| **`SIGKILL`** | Signal Kill (`kill -9`) | Operating System (force kill) | Violent immediate termination (pulling the power plug). Bypasses all cleanup handlers. |

### Why Graceful Shutdown Matters:
```javascript
process.on('SIGTERM', () => {
  // 1. Stop receiving new HTTP traffic
  server.close(() => {
    // 2. Allow active checkouts/queries to finish
    // 3. Close database pools & Redis sockets
    // 4. Exit with code 0 (clean success)
  });
});
```

---

## 2. Node.js Architecture: Why Split `app.js` and `server.js`?

| Responsibility | `app.js` | `server.js` |
|---|---|---|
| **Primary Role** | Application / Domain Pipeline | Infrastructure & Port Binding |
| **What it Contains** | Express routing, Helmet, CORS, body parsers, Zod validation, error handlers. | `http.createServer(app)`, Socket.io WebSocket binding, `server.listen(5000)`, OS signal listeners. |
| **Automated Testing Impact** | **Importable into Supertest** (`supertest(app)`). Runs HTTP tests in-memory with zero port binding. | Runs when starting the real server process. |
| **Why not combine?** | Combining causes `"EADDRINUSE: address already in use"` port conflicts during parallel integration tests. |

---

## 3. Containerization: Why Docker over Local Host Installs?

- **Zero Host Pollution**: You don't install PostgreSQL or Redis on your Windows system. Everything runs inside lightweight, isolated Linux containers (~30MB RAM).
- **Automated Database Bootstrapping**: `docker-compose.yml` reads environment variables (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`) and creates the database automatically on startup.
- **Data Persistence**: Docker Volumes (`postgres_data`) keep your data on your hard drive even if containers are destroyed or updated.
- **Port Tunneling**: `ports: - "5432:5432"` forwards localhost traffic on your machine directly to the containerized database.

---

## 4. Logging Architecture: Winston Severity Levels & Production Observability

### RFC5424 Severity Hierarchy (Lower Number = Higher Urgency)

| Severity | Level | Description | Example in Open Fashion |
|:---:|---|---|---|
| **0** | `error` | Critical failures requiring immediate attention | Unhandled 500 exceptions, DB connection down, payment failure |
| **1** | `warn` | Recoverable or expected client failures | 4xx validation errors, rate limit exceeded, invalid token |
| **2** | `info` | Normal operational milestones | Server booted on port 5000, DB migrated, seed completed |
| **3** | `http` | HTTP access logs via Morgan stream | `GET /api/v1/products 200 - 12.4 ms` |
| **5** | `debug` | Verbose diagnostic data (Dev only) | Cache hit/miss details, Zod schema payload traces |

### Automatic Environment Switching:
- **Development**: Colorized, human-readable terminal output.
- **Production**: Machine-readable structured JSON format for automated log ingestors (Datadog, Grafana Loki, AWS CloudWatch).

---

## 5. State Management Mental Models: BLoC vs Riverpod 2.0

| Feature | Flutter BLoC | Flutter Riverpod 2.0 |
|---|---|---|
| **Mental Model** | Event Stream MVI (Events in ➔ States out) | Declarative Reactive Caching & Dependency Injection |
| **Reading State** | `BlocBuilder` / `BlocProvider.of(context)` | `ref.watch(provider)` / `ConsumerWidget` |
| **Triggering Actions** | `bloc.add(MyEvent())` | `ref.read(myProvider.notifier).myMethod()` |
| **Async Data** | Manual `LoadingState`, `SuccessState`, `ErrorState` | Built-in `AsyncValue` (`.when(data, loading, error)`) |
| **Cache Invalidation** | Manual event dispatching | `ref.invalidate(myProvider)` |
| **Context Dependency** | Bound to `BuildContext` widget tree | Global, independent of `BuildContext` |

---

## 6. Error Handling: Express 5 Unhandled Rejections & Central Error Pipeline

### The Express 5 Revolution:
- In Express 4, async errors required `try/catch` wrappers on every single route.
- In Express 5, **unhandled Promise rejections are caught natively by the router** and forwarded directly to the 4-parameter `errorHandler(err, req, res, next)` middleware.

### Unified JSON Error Envelope:
All client applications (Android + Flutter Admin) expect this exact schema:
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request validation failed",
    "details": [
      { "field": "email", "message": "Invalid email address" }
    ]
  }
}
```

---

## 7. Validation: Authoritative Zod Schemas at System Boundaries

- **Rule**: Never trust client data. Validate at the very edge of the server before payloads touch controllers or database services.
- **Fail-Fast Boot**: `env.js` parses environment variables with Zod upon server launch. If any required secret or database URL is missing, the application halts with exit code 1 immediately.

---

## 8. Redis Key-Value Lifecycle & Namespace Patterns

In Open Fashion, Redis is structured with clear key namespaces and explicit Time-To-Live (TTL) expiration:

| Purpose | Key Pattern | Data Stored | TTL | Strategy |
|---|---|---|---|---|
| **Refresh Token Whitelist** | `refresh_token:<userId>` | Active refresh token string | 7 days (`EX 604800`) | Replaced on every token refresh (Rotation) |
| **Password Reset (URL Token)** | `pwd_reset:token:<sha256(rawToken)>` | User ID | 15 mins (`EX 900`) | Deleted immediately on successful reset |
| **Password Reset (6-Digit OTP)** | `pwd_reset:otp:<email>` | JSON `{"otp": "123456", "userId": "..."}` | 15 mins (`EX 900`) | Deleted immediately on successful reset |
| **Email Verification (URL Token)** | `verify_email:token:<sha256(rawToken)>` | User ID | 24 hours (`EX 86400`) | Deleted on verification; flags DB `isEmailVerified: true` |
| **Email Verification (6-Digit OTP)** | `verify_email:otp:<email>` | JSON `{"otp": "654321", "userId": "..."}` | 15 mins (`EX 900`) | Deleted on verification |

---

## 9. Cryptography in Production: Bcrypt vs SHA-256 Token Hashing

| Dimension | Bcrypt (`bcryptjs`) | SHA-256 (`crypto.createHash('sha256')`) |
|---|---|---|
| **Speed** | Intentionally **Slow & Computationally Expensive** (Salt rounds = 12, ~100ms) | **Extremely Fast** (Microseconds) |
| **Purpose in Open Fashion** | User Passwords (`User.passwordHash`) | Short-lived Password Reset & Email Verification tokens |
| **Why not Bcrypt for Reset Tokens?** | Bcrypt is too slow for temporary high-frequency lookup keys in Redis. Reset tokens already possess 256 bits of high entropy (`crypto.randomBytes(32)`), making brute-force mathematically impossible. |
| **Why hash tokens in Redis at all?** | If Redis is dumped or compromised, attackers cannot read plain-text reset links. They only see un-invertible SHA-256 hashes. |

---

## 10. Flutter Riverpod: `AsyncNotifier` Mental Model & State Flow

### Anatomy of Riverpod Auth Architecture:
```
[User Action] (e.g. click Login)
       │
       ▼
[ref.read(authControllerProvider.notifier).login(email, pass)]
       │
       ▼
1. state = state.copyWith(status: AuthStatus.authenticating, errorMessage: null);
       │
       ▼
2. final user = await authRepository.login(...);
       │
       ▼
3. state = state.copyWith(status: AuthStatus.authenticated, user: user);
       │
       ▼
[UI automatically rebuilds via ref.watch(authControllerProvider)]
```

### Why `copyWith`?
State in Riverpod / MVI is **immutable**. We never mutate fields directly (`state.status = ...` is illegal). Instead, `copyWith` instantiates a fresh state object with selectively overridden values, notifying all listening widgets safely.

---

## 11. Modern Android Photo Picker vs Legacy Storage Permissions

| Dimension | Legacy Storage (`READ_MEDIA_IMAGES`) | Modern Photo Picker (`PickVisualMedia`) |
|---|---|---|
| **Manifest Permission** | Requires `<uses-permission android:name="..." />` | **Zero permissions** required in `AndroidManifest.xml` |
| **User Prompt** | Scary system dialog: *"Allow access to ALL photos?"* | Native, private system sheet isolated in OS process |
| **Play Store Audit** | Strict audit / potential rejection | 100% compliant with Google Play privacy policies |
| **Security Scope** | Broad access to user's entire media library | App only receives a temporary read grant for the **single selected URI** |

---

## 12. Scoped Storage & ContentResolver Streaming

### Why can't Android apps use raw file paths (`/sdcard/...`)?
Android Scoped Storage (API 30+) prevents apps from accessing global filesystem paths.
- Photo pickers return a **`content://` URI** (e.g. `content://media/external/images/media/42`).
- To read the image bytes, the app must ask the **`ContentResolver`** to open an input stream:
  ```kotlin
  val bytes = context.contentResolver.openInputStream(uri)?.use { stream ->
      stream.readBytes()
  }
  ```
- **Kotlin's `.use { ... }`**: Ensures the binary file stream is closed deterministically (equivalent to `try-finally`), preventing memory leaks and orphaned file handles.

---

## 13. Centralized UI Design System Component Architecture (ADR-007)

### Why avoid ad-hoc Material/Compose styling in screens?
1. **Brand Cohesion**: Centralizing `LuxuryButton`, `LuxuryTextField`, `LuxuryBadge`, and `LuxuryCard` enforces consistent luxury typography tracking, 2dp architectural corners, and 60/30/10 color rules.
2. **Boilerplate Reduction**: Replaces 30 lines of `OutlinedTextFieldDefaults` and `ButtonColors` on every screen with a clean, single-line composable/widget.
3. **Global Upgrades**: Changing a global token (e.g. button corner radius or focus gold border) takes effect across the entire app with a single file modification.
