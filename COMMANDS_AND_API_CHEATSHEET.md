# Open Fashion — Commands & API Cheatsheet

> **Purpose**: A centralized reference for all daily terminal commands (Docker, Backend, Android, Flutter Admin) and complete cURL request/response JSON models.

---

## 💻 1. Master Terminal Commands Reference

### 1.1 Docker & Infrastructure (`backend/`)
```bash
# Start PostgreSQL & Redis containers in background
docker compose up -d

# Check running container statuses & health
docker compose ps

# View live container logs
docker compose logs -f

# Stop containers (keeps database data saved in volume)
docker compose down

# Stop containers AND wipe all database data (fresh start)
docker compose down -v
```

### 1.2 Backend Commands (`backend/`)
```bash
# Start development server with auto-reload (watch mode)
npm run dev

# Start production server
npm start

# Run test suite with Vitest
npm test

# Prisma ORM Commands
npx prisma generate       # Generate Prisma client from schema.prisma
npx prisma migrate dev    # Create and apply new SQL migration
npx prisma studio         # Open GUI web interface to browse database
npx prisma db seed        # Run database seed script
```

### 1.3 Flutter Admin Commands (`admin/`)
```bash
# Fetch dependencies
flutter pub get

# Run Admin Dashboard on Chrome Web
flutter run -d chrome

# Run Admin on connected Mobile device
flutter run

# Run Flutter tests
flutter test

# Build production Web release
flutter build web
```

### 1.4 Android Mobile Commands (`mobile/`)
```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run UI instrumentation tests
./gradlew connectedCheck

# Clean build cache
./gradlew clean
```

---

## 📡 2. Complete API cURL & JSON Response Models

Base URL: `http://localhost:5000`

---

### 2.1 System Health Check

#### cURL:
```bash
curl -X GET http://localhost:5000/health
```

#### Response Model (200 OK):
```json
{
  "status": "healthy",
  "timestamp": "2026-09-18T17:27:18.875Z",
  "uptime": 9.87,
  "environment": "development",
  "version": "1.0.0"
}
```

---

### 2.2 Base API Welcome Route

#### cURL:
```bash
curl -X GET http://localhost:5000/api/v1
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "message": "Welcome to Open Fashion Enterprise API v1"
}
```

---

### 2.3 Undefined Route (Standard 404 Error Model)

#### cURL:
```bash
curl -X GET http://localhost:5000/api/v1/invalid-route
```

#### Response Model (404 Not Found):
```json
{
  "success": false,
  "error": {
    "code": "NOT_FOUND",
    "message": "Cannot find endpoint [GET] /api/v1/invalid-route on this server"
  }
}
```

---

### 2.4 User Registration (Upcoming Auth Module)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane@example.com",
    "password": "SecurePassword123!"
  }'
```

#### Response Model (201 Created):
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_9b1deb4d3b7d4e89",
      "name": "Jane Doe",
      "email": "jane@example.com",
      "role": "CUSTOMER",
      "createdAt": "2026-09-18T18:00:00.000Z"
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
  },
  "message": "User registered successfully"
}
```

#### Validation Error Model (422 Unprocessable Entity):
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request validation failed",
    "details": [
      {
        "field": "email",
        "message": "Invalid email format"
      },
      {
        "field": "password",
        "message": "Password must be at least 8 characters long"
      }
    ]
  }
}
```

---

### 2.5 User Login (Upcoming Auth Module)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "SecurePassword123!"
  }'
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_9b1deb4d3b7d4e89",
      "name": "Jane Doe",
      "email": "jane@example.com",
      "role": "CUSTOMER"
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
  },
  "message": "Logged in successfully"
}
```

---

### 2.6 Authenticated Request Example (Bearer Token)

#### cURL:
```bash
curl -X GET http://localhost:5000/api/v1/users/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### Invalid / Expired Token Error Model (401 Unauthorized):
```json
{
  "success": false,
  "error": {
    "code": "TOKEN_EXPIRED",
    "message": "Authentication token has expired"
  }
}
```
