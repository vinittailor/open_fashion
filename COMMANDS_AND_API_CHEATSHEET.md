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
# Forward port 5000 from Android emulator/device to host machine (avoids Windows firewall drops)
adb reverse tcp:5000 tcp:5000

# Build debug APK
./gradlew assembleDebug

# Compile Kotlin sources only (fast verification)
./gradlew compileDebugKotlin

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
  "timestamp": "2026-10-03T16:00:00.000Z",
  "uptime": 120.45,
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

### 2.3 User Registration (`POST /api/v1/auth/register`)

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
      "id": "cm...unique_id",
      "name": "Jane Doe",
      "email": "jane@example.com",
      "role": "CUSTOMER",
      "isEmailVerified": false,
      "createdAt": "2026-10-03T16:00:00.000Z"
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
  },
  "message": "User registered successfully"
}
```

---

### 2.4 User Login (`POST /api/v1/auth/login`)

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
      "id": "cm...unique_id",
      "name": "Jane Doe",
      "email": "jane@example.com",
      "role": "CUSTOMER",
      "isEmailVerified": false
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

### 2.5 Refresh JWT Session (`POST /api/v1/auth/refresh`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "data": {
    "tokens": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
  },
  "message": "Token refreshed successfully"
}
```

---

### 2.6 Get Profile (`GET /api/v1/users/me`)

#### cURL:
```bash
curl -X GET http://localhost:5000/api/v1/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "cm...unique_id",
    "name": "Jane Doe",
    "email": "jane@example.com",
    "role": "CUSTOMER",
    "isEmailVerified": false,
    "phone": null,
    "avatar": null,
    "createdAt": "2026-10-03T16:00:00.000Z"
  },
  "message": "User profile fetched successfully"
}
```

---

### 2.7 Update Profile (`PATCH /api/v1/users/me`)

#### cURL:
```bash
curl -X PATCH http://localhost:5000/api/v1/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Updated",
    "phone": "+1234567890"
  }'
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "cm...unique_id",
    "name": "Jane Updated",
    "email": "jane@example.com",
    "role": "CUSTOMER",
    "phone": "+1234567890"
  },
  "message": "Profile updated successfully"
}
```

---

### 2.8 Forgot Password Request (`POST /api/v1/auth/forgot-password`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com"
  }'
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "data": {
    "message": "If that email is registered, a password reset link has been sent.",
    "devToken": "8f3b... (development only)",
    "devOtp": "123456 (development only)"
  },
  "message": "Password reset email dispatched"
}
```

---

### 2.9 Reset Password with Token or OTP (`POST /api/v1/auth/reset-password`)

#### cURL (with Token):
```bash
curl -X POST http://localhost:5000/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_RESET_TOKEN",
    "password": "NewSecurePassword123!"
  }'
```

#### cURL (with OTP):
```bash
curl -X POST http://localhost:5000/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "otp": "123456",
    "password": "NewSecurePassword123!"
  }'
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "data": {
    "message": "Password has been successfully reset. Please log in with your new password."
  },
  "message": "Password reset completed"
}
```

---

### 2.10 Send Email Verification (`POST /api/v1/auth/send-verification`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/send-verification \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "data": {
    "message": "Verification email has been dispatched.",
    "devToken": "3a1c... (development only)",
    "devOtp": "654321 (development only)"
  },
  "message": "Verification email sent"
}
```

---

### 2.11 Verify Email (`POST /api/v1/auth/verify-email`)

#### cURL (with Token):
```bash
curl -X POST http://localhost:5000/api/v1/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_VERIFY_TOKEN"
  }'
```

#### cURL (with OTP):
```bash
curl -X POST http://localhost:5000/api/v1/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "otp": "654321"
  }'
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "data": {
    "message": "Email address verified successfully!"
  },
  "message": "Email verified"
}
```

---

### 2.12 Standard Error Envelopes

#### 401 Unauthorized (Expired or Missing Token):
```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Authentication token missing or expired"
  }
}
```

#### 403 Forbidden (Role Guard):
```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "You do not have permission to perform this action"
  }
}
```

#### 422 Unprocessable Entity (Zod Validation Failure):
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request validation failed",
    "details": [
      {
        "field": "password",
        "message": "Password must contain at least one uppercase letter and one special character"
      }
    ]
  }
}
```
