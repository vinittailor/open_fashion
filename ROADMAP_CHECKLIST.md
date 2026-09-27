# Open Fashion — Micro-Slice Feature Master Roadmap

> **Core Philosophy (Micro-Feature Vertical Slices)**:
> We build each **individual capability** end-to-end:
> `[1. Backend Endpoint & Logic]` ➔ `[2. Flutter Admin Visual UI]` ➔ `[3. Android Mobile Compose UI]`
> This guarantees zero cognitive overload, immediate visual feedback, and rock-solid full-stack architecture mastery.

> **Status Legend**:
> - `[ ]` Not Started
> - `[-]` In Progress
> - `[x]` Completed

---

## 🏗️ Phase 0: Workspace & Infrastructure Foundations (100% Completed)

### 0.1 Workspace Scaffolding & Design Systems
- [x] **0.1.1**: Root Agent Protocol, Guidelines & ADRs (`/AGENTS.md`, `/ARCHITECTURE.md`, `/DECISIONS.md`)
- [x] **0.1.2**: Backend Scaffolding (`package.json`, Express 5, Zod, Logger, Error Handler, Server)
- [x] **0.1.3**: Flutter Admin Scaffolding (`pubspec.yaml`, Riverpod, Breakpoints, Color & Typography Tokens)
- [x] **0.1.4**: Android Mobile Scaffolding (`libs.versions.toml`, Gradle, M3 Theme Tokens, Clean Architecture skeleton)
- [x] **0.1.5**: Multi-Container Docker Infrastructure (`docker-compose.yml` - PostgreSQL 16 on port 5433 + Redis 7 on port 6379)

### 0.2 Database & Cache Client Infrastructure
- [x] **0.2.1**: Enterprise Relational Schema (`backend/prisma/schema.prisma` - 11 models + enums)
- [x] **0.2.2**: Prisma 7 Configuration (`backend/prisma.config.js`)
- [x] **0.2.3**: Database Migration Applied (`init_ecommerce_schema` via Docker PostgreSQL)
- [x] **0.2.4**: Prisma Client Singleton & Lifecycle Manager (`backend/src/config/prisma.js` with `@prisma/adapter-pg`)
- [x] **0.2.5**: Redis Client Singleton & Event Manager (`backend/src/config/redis.js` with `ioredis`)

---

## 🔐 Feature Slice 1: Authentication & User Accounts (Next Phase)

### 1.1 Micro-Slice: User Registration ➔ [COMPLETED]
- [x] **1.1.1 [Backend]**: Password Hashing utility (Bcrypt) + Registration Zod Schema + POST `/api/v1/auth/register`
- [x] **1.1.2 [Flutter Admin]**: Admin User Registration / Invitation Form
- [x] **1.1.3 [Android Mobile]**: Customer Luxury Registration Screen (Compose M3 + MVI ViewModel)

### 1.2 Micro-Slice: User Login & JWT Session Management ➔ 📍 STARTING HERE
- [ ] **1.2.1 [Backend]**: JWT Signer/Verifier utility + Redis Refresh Token Whitelist + POST `/api/v1/auth/login` & POST `/api/v1/auth/refresh`
- [ ] **1.2.2 [Flutter Admin]**: Responsive Admin Login Screen (Web & Mobile Layouts) + Riverpod AuthState + Secure Storage
- [ ] **1.2.3 [Android Mobile]**: Customer Login Screen (Compose M3) + Encrypted Token DataStore + Auto-Login Flow

### 1.3 Micro-Slice: Protected Routes & User Profile (`/me`)
- [ ] **1.3.1 [Backend]**: JWT Auth Guard Middleware + Role-Based Access Guard (`ADMIN`/`CUSTOMER`) + GET & PATCH `/api/v1/users/me`
- [ ] **1.3.2 [Flutter Admin]**: Admin Header Profile Chip, Role Badge & Logout Action
- [ ] **1.3.3 [Android Mobile]**: Customer Profile Screen (Account info, Edit Profile, Logout bottom sheet)

### 1.4 Micro-Slice: Password Reset & Email Verification
- [ ] **1.4.1 [Backend]**: SHA-256 Token Generator + Forgot Password + Reset Password + Verify Email endpoints
- [ ] **1.4.2 [Flutter Admin]**: Admin Forgot Password Screen & Reset Link Confirmation
- [ ] **1.4.3 [Android Mobile]**: Forgot Password Screen + OTP/Email Verification Screen

### 1.5 Micro-Slice: Admin User Management
- [ ] **1.5.1 [Backend]**: Admin Users List API with Pagination, Role Filter & Soft Delete (`/api/v1/admin/users`)
- [ ] **1.5.2 [Flutter Admin]**: Interactive User Management Data Table (View registered customers, toggle active/ban status)

---

## 📁 Feature Slice 2: Media & File Storage Registry

### 2.1 Micro-Slice: Single File Upload
- [ ] **2.1.1 [Backend]**: Multer file parser + MIME validation + File Model Registry (`/api/v1/files/upload`)
- [ ] **2.1.2 [Flutter Admin]**: File Upload Widget & Progress Indicator
- [ ] **2.1.3 [Android Mobile]**: Avatar Picker & Upload integration

### 2.2 Micro-Slice: Product Media Gallery
- [ ] **2.2.1 [Backend]**: Multi-file upload endpoint & ProductImage relation linking
- [ ] **2.2.2 [Flutter Admin]**: Drag-and-Drop Image Gallery & Primary Image Selector
- [ ] **2.2.3 [Android Mobile]**: Coil Image Caching & Luxury Shimmer Placeholders

---

## 👗 Feature Slice 3: Categories & Product Catalog Management

### 3.1 Micro-Slice: Hierarchical Categories
- [ ] **3.1.1 [Backend]**: Category CRUD API (Parent/Child relations, Slug auto-generation, Redis cache)
- [ ] **3.1.2 [Flutter Admin]**: Category Tree View & Category Create/Edit Modal
- [ ] **3.1.3 [Android Mobile]**: Category Filter Chips & Category Navigation Sheet

### 3.2 Micro-Slice: Product CRUD & Multi-SKU Variants
- [ ] **3.2.1 [Backend]**: Product & SKU Variant API (Color, Size, SKU, Stock, Price Adjustment) with Prisma interactive transaction
- [ ] **3.2.2 [Flutter Admin]**: Product Data Table with Stock Badges + Add/Edit Multi-Step Modal
- [ ] **3.2.3 [Android Mobile]**: Product Discovery Screen (Hero Carousel, Curated Grids, Offline Room Cache)

### 3.3 Micro-Slice: Product Detail & SKU Variant Selector
- [ ] **3.3.1 [Backend]**: Product by Slug API with full Variant matrix & Image gallery
- [ ] **3.3.2 [Android Mobile]**: Luxury Product Detail Composable (Image Pager, Size Pills, Color Dots, Dynamic Stock Pill)

### 3.4 Micro-Slice: Catalog Search, Filter & Pagination
- [ ] **3.4.1 [Backend]**: Full-text Search & Dynamic Filtering API (Price range, Category, Size, Color, Sort)
- [ ] **3.4.2 [Flutter Admin]**: Data Table Live Search & Filter Bar
- [ ] **3.4.3 [Android Mobile]**: Filter BottomSheet (Price Range Slider, Color Selector)

---

## 🛍️ Feature Slice 4: Wishlist & Shopping Cart

### 4.1 Micro-Slice: Wishlist Experience
- [ ] **4.1.1 [Backend]**: Wishlist Toggle API (Add, Remove, List with user relation)
- [ ] **4.1.2 [Android Mobile]**: Wishlist Screen with Staggered Grid & Quick Move-to-Cart

### 4.2 Micro-Slice: Shopping Cart & Quantity Controls
- [ ] **4.2.1 [Backend]**: Cart API (Add Variant, Quantity Steppers, Atomic Stock Check, Subtotal Calculation)
- [ ] **4.2.2 [Android Mobile]**: Luxury Shopping Cart Screen (Swipe-to-Delete, Quantity Controls, Promo Code, Subtotal Breakdown)
- [ ] **4.2.3 [Flutter Admin]**: Abandoned Cart & Stock Reservation Metrics

---

## 📦 Feature Slice 5: Orders, Checkout & Payments

### 5.1 Micro-Slice: Address Management
- [ ] **5.1.1 [Backend]**: Address CRUD API (Add, Edit, Delete, Set Default)
- [ ] **5.1.2 [Android Mobile]**: Address Selection & Add Address Bottom Sheet

### 5.2 Micro-Slice: Order Placement & Atomic Stock Decrement
- [ ] **5.2.1 [Backend]**: Checkout API with Prisma interactive `$transaction` (Snapshot generation, stock deduction, order number generator)
- [ ] **5.2.2 [Android Mobile]**: Multi-Step Checkout Flow (Address ➔ Payment Method ➔ Order Review ➔ Success Celebration)

### 5.3 Micro-Slice: Order State Machine & Fulfillment
- [ ] **5.3.1 [Backend]**: Order State Transition API (`PENDING` ➔ `PAID` ➔ `PROCESSING` ➔ `SHIPPED` ➔ `DELIVERED` ➔ `CANCELLED`)
- [ ] **5.3.2 [Flutter Admin]**: Orders Kanban Board, Interactive Data Table & PDF Invoice Generator
- [ ] **5.3.3 [Android Mobile]**: Order History & Live Delivery Timeline Tracking Screen

---

## ⭐ Feature Slice 6: Product Reviews & Ratings

### 6.1 Micro-Slice: Customer Reviews
- [ ] **6.1.1 [Backend]**: Review API (1-5 Stars, Comment, Verified Buyer enforcement, Rating average aggregation)
- [ ] **6.1.2 [Android Mobile]**: Review Breakdown (Rating Histogram + Write Review Modal)
- [ ] **6.1.3 [Flutter Admin]**: Review Moderation Data Table (Approve/Flag/Delete)

---

## ⚡ Feature Slice 7: Real-Time Engine & Live Admin Analytics

### 7.1 Micro-Slice: Live Order Feed & Alerts
- [ ] **7.1.1 [Backend]**: Socket.io Server Gateway with Authenticated Rooms (`admin_room`, `user_{id}`)
- [ ] **7.1.2 [Flutter Admin]**: Real-time Order Toast Notifications & Live Feed
- [ ] **7.1.3 [Android Mobile]**: Real-time Order Status Push Listener

### 7.2 Micro-Slice: Executive Analytics Dashboard
- [ ] **7.2.1 [Backend]**: Analytics Aggregator API (Revenue, Sales Trends, Top SKUs, Low Stock alerts)
- [ ] **7.2.2 [Flutter Admin]**: Luxury FlChart Dashboard (Revenue curves, Order volume, Inventory health)

---

## 🚢 Phase 8: Testing, CI/CD & Production Polish

- [ ] **8.1**: Backend Integration Test Suite (Supertest + Vitest)
- [ ] **8.2**: Flutter Widget & Riverpod Provider Tests
- [ ] **8.3**: Android Unit & Compose UI Tests
- [ ] **8.4**: Production Multi-Stage Dockerfile & GitHub Actions Workflow
- [ ] **8.5**: Complete Portfolio & Architecture Showcase Documentation

---

## 📌 Status Summary
- **Current Milestone**: `Feature Slice 1: Authentication & User Accounts`
- **Immediate Next Step**: `Micro-Slice 1.1.1 [Backend] — Password Utility & Registration Schema`
