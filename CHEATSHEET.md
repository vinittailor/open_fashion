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
