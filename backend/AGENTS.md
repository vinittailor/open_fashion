# Open Fashion — Backend Engineering Guidelines & Learning Rules

## 1. Domain & Responsibilities
The `backend/` directory contains the core server application for the Open Fashion ecosystem. It is the single source of truth for business logic, database persistence, access control, media file registry, and real-time event broadcasting.

**Strict Boundary Rule**: Never place Kotlin, Android, Dart, or Flutter code inside this directory.

---

## 2. Technology Stack & Runtime
- **Runtime**: Node.js `v26.7.0` (Native ES Modules)
- **Module System**: ES Modules (`import`/`export`, `"type": "module"` in `package.json`)
- **HTTP Framework**: Express 5 (native async error routing)
- **Database & ORM**: PostgreSQL 16 (Port `5433`) with Prisma 7 (`@prisma/adapter-pg`)
- **In-Memory Cache & Broker**: Redis 7 (Port `6379`) with `ioredis`
- **File & Media Storage**: Multer 2.4.0 (MIME whitelist, crypto collision-free naming)
- **Real-Time Engine**: Socket.io
- **Request & Contract Validation**: Zod
- **Testing**: Vitest (19 tests passing)
- **Containerization**: Docker & Docker Compose

---

## 3. Core Architectural Layers & Request Lifecycle

Every API request follows a strict, unidirectional pipeline:

```
[HTTP Request]
       │
       ▼
1. Middleware Pipeline (Helmet, CORS, RateLimiter, RequestLogger)
       │
       ▼
2. Authentication / Authorization Middleware (JWT verification, Role checking)
       │
       ▼
3. Zod Validation Middleware (Strict payload parsing & error interception)
       │
       ▼
4. Controller Layer (Extracts params, invokes Service, formats HTTP status & body)
       │
       ▼
5. Service Layer (Pure business logic, orchestration, stock reservations, pricing)
       │
       ▼
6. Repository / Data Access Layer (Prisma queries, Redis caching, SQL transactions)
       │
       ▼
[Database / Cache]
       │
       ▼
[Response Envelope formatted & sent via Controller]
```

### Layer Rationale & Boundaries
1. **Controllers**: Should remain "skinny". No database queries or heavy calculations. Only handle HTTP-specific logic (headers, status codes, payload extraction).
2. **Services**: Contain 100% of domain rules. Independent of Express `req`/`res` objects, making them effortlessly unit-testable.
3. **Repositories / Prisma**: Encapsulate database queries, relational joins, indexing hints, and transactions.

---

## 4. Backend-Specific Learning & Engineering Rules

### 4.1. Asynchronous Code & Event Loop
- Always use `async`/`await` for promise-based operations. Never mix raw callbacks with promises.
- Express 5 automatically catches unhandled Promise rejections in route handlers, but all errors must be mapped to custom `AppError` subclasses.
- Understand Node.js event-loop phases: never block the event loop with synchronous CPU-intensive operations (e.g. use asynchronous cryptographic methods for password hashing).

### 4.2. Authoritative Zod Validation
- All incoming requests (`body`, `query`, `params`) must have a corresponding Zod schema.
- Validate data *at the boundary* before it touches any controller or service logic.

### 4.3. Database Transactions & Concurrency
- When multiple database operations must succeed or fail together (e.g. placing an order and decrementing stock), use Prisma's interactive transaction `$transaction(async (tx) => { ... })`.
- Understand optimistic vs. pessimistic locking for inventory race conditions.

### 4.4. Unified JSON Response & Error Envelopes
- **Success Format**:
  ```json
  {
    "success": true,
    "statusCode": 200,
    "data": { ... },
    "message": "Resource created successfully"
  }
  ```
- **Error Format**:
  ```json
  {
    "success": false,
    "statusCode": 422,
    "message": "Validation error",
    "errors": [ ... ]
  }
  ```

### 4.5. Security Best Practices
- Never log passwords, tokens, or sensitive user PII.
- Enforce strict CORS whitelists and security headers via Helmet.
- Use parameterized queries via Prisma to prevent SQL injection.
- Implement rate limiting on sensitive routes (auth, checkout).

---

## 5. Standard Backend Commands
- `npm run dev`: Start development server with live reload (`--watch`)
- `npm run start`: Start production server
- `npm test`: Run test suite with Vitest
- `npx prisma migrate dev`: Run migrations in development
- `npx prisma studio`: Open GUI database browser
- `docker compose up -d`: Launch PostgreSQL and Redis containers
