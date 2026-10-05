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

## 📡 2. Complete REST API Specifications & cURL Models

### 2.1 Health Check (`GET /health`)

#### cURL:
```bash
curl -X GET http://localhost:5000/health
```

#### Response Model (200 OK):
```json
{
  "status": "healthy",
  "timestamp": "2026-10-05T12:00:00.000Z",
  "uptime": 124.5,
  "services": {
    "database": "connected",
    "redis": "connected"
  }
}
```

---

### 2.2 Customer Registration (`POST /api/v1/auth/register`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane@example.com",
    "password": "Password123!",
    "phoneNumber": "+1234567890"
  }'
```

#### Response Model (201 Created):
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_94b1c8a1-2d3e-4f5a-8b9c-0d1e2f3a4b5c",
      "name": "Jane Doe",
      "email": "jane@example.com",
      "role": "CUSTOMER",
      "phoneNumber": "+1234567890",
      "isEmailVerified": false,
      "createdAt": "2026-10-05T12:00:00.000Z"
    }
  },
  "message": "User registered successfully"
}
```

---

### 2.3 Customer / Admin Login (`POST /api/v1/auth/login`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "Password123!"
  }'
```

#### Response Model (200 OK):
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_94b1c8a1-2d3e-4f5a-8b9c-0d1e2f3a4b5c",
      "name": "Jane Doe",
      "email": "jane@example.com",
      "role": "CUSTOMER"
    },
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900
  },
  "message": "Login successful"
}
```

---

### 2.4 Token Refresh (`POST /api/v1/auth/refresh`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN_HERE"
  }'
```

---

### 2.5 Single File Upload (`POST /api/v1/files/upload`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/files/upload \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

#### Response Model (201 Created):
```json
{
  "success": true,
  "statusCode": 201,
  "message": "File uploaded and registered successfully",
  "data": {
    "file": {
      "id": "c1f76023-e2ef-4573-8a3a-a1adcf6ceb92",
      "filename": "img-1728148920123-a1b2c3d4e5.jpg",
      "key": "avatars/img-1728148920123-a1b2c3d4e5.jpg",
      "url": "http://localhost:5000/uploads/avatars/img-1728148920123-a1b2c3d4e5.jpg",
      "mimeType": "image/jpeg",
      "sizeBytes": 245812,
      "provider": "LOCAL",
      "isPublic": true,
      "uploaderId": "usr_94b1c8a1-2d3e-4f5a-8b9c-0d1e2f3a4b5c",
      "createdAt": "2026-10-05T12:00:00.000Z"
    }
  }
}
```

---

### 2.6 Get File Metadata (`GET /api/v1/files/:id`)

#### cURL:
```bash
curl -X GET http://localhost:5000/api/v1/files/c1f76023-e2ef-4573-8a3a-a1adcf6ceb92
```

---

### 2.7 Delete File (`DELETE /api/v1/files/:id`)

#### cURL:
```bash
curl -X DELETE http://localhost:5000/api/v1/files/c1f76023-e2ef-4573-8a3a-a1adcf6ceb92 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

### 2.8 Forgot Password (`POST /api/v1/auth/forgot-password`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com"
  }'
```

---

### 2.9 Reset Password (`POST /api/v1/auth/reset-password`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_RESET_TOKEN",
    "newPassword": "NewPassword123!"
  }'
```

---

### 2.10 Verify Email (`POST /api/v1/auth/verify-email`)

#### cURL:
```bash
curl -X POST http://localhost:5000/api/v1/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_VERIFY_TOKEN"
  }'
```

---

### 2.11 Standard Error Envelopes

#### 400 Bad Request (Invalid input / Unsupported MIME):
```json
{
  "success": false,
  "statusCode": 400,
  "message": "Invalid file format. Supported formats: JPEG, PNG, WEBP, GIF, AVIF, HEIC, HEIF"
}
```

#### 401 Unauthorized (Expired or Missing Token):
```json
{
  "success": false,
  "statusCode": 401,
  "message": "Authentication token missing or expired"
}
```

#### 413 Payload Too Large (File > 5MB):
```json
{
  "success": false,
  "statusCode": 413,
  "message": "File size exceeds 5MB limit"
}
```
