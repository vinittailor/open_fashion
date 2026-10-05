# Open Fashion — Backend API & Real-Time Engine

> Enterprise-grade RESTful API and WebSocket engine built with Node.js (ES Modules), Express 5, PostgreSQL 16 (Prisma 7), Redis 7, and Docker.

---

## 🛠️ Tech Stack & Dependencies

- **Runtime**: Node.js `v26.7.0` (Native ES Modules)
- **Framework**: Express `5.x` (native asynchronous error handling)
- **Database**: PostgreSQL 16 (Port `5433` via Docker)
- **ORM**: Prisma `7.x` with `@prisma/adapter-pg`
- **Cache / Broker**: Redis 7 (Port `6379` via Docker) with `ioredis`
- **Validation**: Zod
- **File & Media Storage**: Multer 2.4.0 (MIME whitelist, crypto collision-free naming)
- **Authentication**: JWT (Access + Refresh Token rotation with Redis blacklist)
- **Logging**: Winston 3 logger with rotational file transports
- **Testing**: Vitest (19 tests passing)

---

## 🚀 Getting Started

### 1. Start Database & Redis (Docker)
```bash
docker compose up -d
```

### 2. Environment Variables
Create `.env` based on `.env.example`:
```env
PORT=5000
NODE_ENV=development
DATABASE_URL="postgresql://postgres:postgres123@localhost:5433/open_fashion_db?schema=public"
REDIS_URL="redis://localhost:6379"
JWT_ACCESS_SECRET="your-access-secret"
JWT_REFRESH_SECRET="your-refresh-secret"
```

### 3. Database Migration & Prisma Client
```bash
npx prisma migrate dev
```

### 4. Run Development Server
```bash
npm run dev
```

### 5. Run Test Suite
```bash
npm test
```

---

## 📡 API Route Architecture

- `POST /api/v1/auth/register` — Customer & Admin Registration
- `POST /api/v1/auth/login` — User Authentication (JWT Access + Refresh tokens)
- `POST /api/v1/auth/refresh` — Token rotation
- `POST /api/v1/auth/forgot-password` — Password reset request (SHA-256 token)
- `POST /api/v1/auth/reset-password` — Reset password with token
- `POST /api/v1/auth/verify-email` — Verify email address
- `GET /api/v1/users/me` — Authenticated User Profile
- `PATCH /api/v1/users/me` — Update User Profile
- `POST /api/v1/files/upload` — Single Media File Upload (Multer)
- `GET /api/v1/files/:id` — File Registry Metadata
- `DELETE /api/v1/files/:id` — Delete File Record & Physical Asset
- `GET /uploads/*` — Static asset serving with CORS headers
