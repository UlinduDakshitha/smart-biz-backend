# SmartBiz Backend

> Spring Boot REST API — AI-Powered Business Management Suite for SMEs

---

## Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Architecture (MVC)](#architecture-mvc)
5. [Database Schema](#database-schema)
6. [Prerequisites](#prerequisites)
7. [Configuration](#configuration)
8. [Running the Application](#running-the-application)
9. [Data Seeder](#data-seeder)
10. [Authentication & Security](#authentication--security)
11. [Forgot Password — OTP Flow](#forgot-password--otp-flow)
12. [API Reference](#api-reference)
13. [Email Setup (Gmail SMTP)](#email-setup-gmail-smtp)
14. [Production Deployment](#production-deployment)
15. [Environment Variables Reference](#environment-variables-reference)

---

## Overview

SmartBiz Backend is a Spring Boot REST API that powers the SmartBiz business management platform. It provides:

- **Unified Authentication** — Single login endpoint for both Admin and Business users; the backend detects the role automatically and returns the appropriate JWT token.
- **Business Management** — Full CRUD for Customers, Suppliers, Products, Invoices, and Expenses, all scoped to the authenticated business.
- **AI Integration** — OpenAI GPT-3.5-turbo integration for generating business insights, professional emails, invoice summaries, and social media posts.
- **Forgot Password with OTP** — Secure 6-digit OTP sent via Gmail SMTP, stored in Caffeine in-memory cache (5-minute TTL).
- **Admin Panel API** — Dedicated admin-only endpoints for managing businesses, subscriptions, and AI usage logs.

---

## Technology Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Spring Boot | 3.2.3 | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.x | ORM and repository layer |
| Spring Mail | 3.x | OTP email delivery |
| Spring Cache + Caffeine | 3.1.8 | In-memory OTP storage |
| MySQL | 8.0+ | Relational database |
| JWT (jjwt) | 0.11.5 | Stateless token authentication |
| Lombok | Latest | Boilerplate reduction |
| ModelMapper | 3.1.1 | DTO-Entity mapping |
| OkHttp | 4.12.0 | OpenAI HTTP client |
| Maven | 3.8+ | Build tool |

---

## Project Structure

```
backend/
├── pom.xml
└── src/
    └── main/
        ├── java/com/smartbiz/
        │   ├── SmartBizApplication.java          ← Entry point (@EnableCaching)
        │   │
        │   ├── config/
        │   │   ├── AppConfig.java                ← ModelMapper bean
        │   │   └── SecurityConfig.java           ← JWT filter, CORS, route security
        │   │
        │   ├── controller/                        ← MVC Controllers (REST layer)
        │   │   ├── AuthController.java           ← Login, Register, OTP endpoints
        │   │   ├── AdminController.java          ← Admin-only endpoints
        │   │   ├── DashboardController.java
        │   │   ├── CustomerController.java
        │   │   ├── SupplierController.java
        │   │   ├── ProductController.java
        │   │   ├── InvoiceController.java
        │   │   ├── ExpenseController.java
        │   │   └── AiController.java
        │   │
        │   ├── service/impl/                      ← Business logic layer
        │   │   ├── AuthService.java              ← Unified login + OTP flow
        │   │   ├── OtpService.java               ← OTP generate/verify/clear
        │   │   ├── EmailService.java             ← HTML OTP email via SMTP
        │   │   ├── BusinessContextService.java   ← Gets current logged-in business
        │   │   ├── DashboardService.java
        │   │   ├── CustomerService.java
        │   │   ├── SupplierService.java
        │   │   ├── ProductService.java
        │   │   ├── InvoiceService.java
        │   │   ├── ExpenseService.java
        │   │   ├── OpenAiService.java            ← GPT-3.5 calls
        │   │   ├── AiUsageService.java
        │   │   └── AdminService.java
        │   │
        │   ├── repository/                        ← JPA Repositories (data layer)
        │   │   ├── AdminRepository.java
        │   │   ├── BusinessRepository.java
        │   │   ├── UserRepository.java
        │   │   ├── CustomerRepository.java
        │   │   ├── SupplierRepository.java
        │   │   ├── ProductRepository.java
        │   │   ├── InvoiceRepository.java
        │   │   ├── InvoiceItemRepository.java
        │   │   ├── ExpenseRepository.java
        │   │   ├── AiUsageRepository.java
        │   │   └── SubscriptionRepository.java
        │   │
        │   ├── entity/                            ← JPA Entities (DB tables)
        │   │   ├── Admin.java
        │   │   ├── Business.java
        │   │   ├── User.java
        │   │   ├── Customer.java
        │   │   ├── Supplier.java
        │   │   ├── Product.java
        │   │   ├── Invoice.java
        │   │   ├── InvoiceItem.java
        │   │   ├── Expense.java
        │   │   ├── AiUsage.java
        │   │   └── Subscription.java
        │   │
        │   ├── dto/
        │   │   ├── request/
        │   │   │   ├── LoginRequest.java
        │   │   │   ├── BusinessRegisterRequest.java
        │   │   │   ├── InvoiceRequest.java
        │   │   │   ├── AiRequest.java
        │   │   │   ├── ForgotPasswordRequest.java
        │   │   │   ├── VerifyOtpRequest.java
        │   │   │   └── ResetPasswordRequest.java
        │   │   └── response/
        │   │       ├── ApiResponse.java          ← Generic wrapper {success, message, data}
        │   │       ├── AuthResponse.java
        │   │       └── DashboardResponse.java
        │   │
        │   ├── security/
        │   │   ├── JwtUtils.java                 ← Token generate/validate/parse
        │   │   └── JwtAuthFilter.java            ← OncePerRequestFilter
        │   │
        │   ├── exception/
        │   │   └── GlobalExceptionHandler.java   ← @RestControllerAdvice
        │   │
        │   └── seeder/
        │       └── DataSeeder.java               ← CommandLineRunner: seeds admin + subscriptions
        │
        └── resources/
            └── application.properties
```

---

## Architecture (MVC)

SmartBiz Backend strictly follows the **MVC (Model-View-Controller)** pattern adapted for a REST API:

```
HTTP Request
    │
    ▼
[ JwtAuthFilter ]          ← Validates Bearer token on every request
    │
    ▼
[ Controller Layer ]       ← Handles HTTP, validates input, returns ApiResponse<T>
    │
    ▼
[ Service Layer ]          ← All business logic, data transformation, orchestration
    │
    ▼
[ Repository Layer ]       ← Spring Data JPA, talks to MySQL
    │
    ▼
[ Entity / DB ]            ← JPA-mapped MySQL tables
```

**Key Design Decisions:**

- All endpoints return `ApiResponse<T>` — a uniform wrapper with `success`, `message`, and `data` fields.
- `BusinessContextService` extracts the currently authenticated business from the `SecurityContextHolder`, so every service method is automatically scoped to the right business without passing IDs manually.
- The `GlobalExceptionHandler` catches all `RuntimeException` and validation errors and returns structured JSON errors — no raw stack traces ever reach the client.

---

## Database Schema

```
admins                        subscriptions
┌────────────────┐            ┌────────────────────┐
│ admin_id  (PK) │            │ subscription_id(PK)│
│ name           │            │ plan_name          │
│ email (UNIQUE) │            │ price              │
│ password       │            │ duration_days      │
└────────────────┘            └────────────────────┘
                                       │
                                       │ FK
                                       ▼
businesses                    ┌────────────────────┐
┌────────────────────┐ ◄──────│ business_id   (PK) │
│ business_id   (PK) │        │ name               │
│ name               │        │ email (UNIQUE)      │
│ email (UNIQUE)     │        │ password           │
│ password           │        │ phone              │
│ phone              │        │ address            │
│ address            │        │ subscription_id(FK)│
│ subscription_id(FK)│        └────────────────────┘
└────────────────────┘
         │
    ┌────┴──────────────────────────────────┐
    │              │          │             │
    ▼              ▼          ▼             ▼
customers      products   expenses      ai_usages
suppliers    invoices → invoice_items
```

**Tables:**

| Table | Description |
|---|---|
| `admins` | Platform administrators |
| `subscriptions` | Plan definitions (Free, Starter, etc.) |
| `businesses` | Business owner accounts |
| `users` | Staff users under a business |
| `customers` | Business's customers |
| `suppliers` | Product suppliers |
| `products` | Product catalogue with stock |
| `invoices` | Sales invoices |
| `invoice_items` | Line items on each invoice |
| `expenses` | Business expense records |
| `ai_usages` | Log of every AI request made |

---

## Prerequisites

Before running the backend, ensure you have:

- **Java 17** or later — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/)
- A **Gmail account** with an App Password for OTP emails (see [Email Setup](#email-setup-gmail-smtp))
- An **OpenAI API key** — [Get one here](https://platform.openai.com/api-keys)

---

## Configuration

All configuration lives in `src/main/resources/application.properties`.

### Step 1 — Copy and edit properties

```properties
# ── Server ───────────────────────────────────────────────────────────────
server.port=8080
spring.application.name=smartbiz-backend

# ── Database ─────────────────────────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/smartbiz_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD          ← CHANGE THIS

# ── JPA ──────────────────────────────────────────────────────────────────
spring.jpa.hibernate.ddl-auto=update                   ← Creates/updates tables automatically
spring.jpa.show-sql=true                               ← Set false in production
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# ── JWT ──────────────────────────────────────────────────────────────────
jwt.secret=SmartBizSecretKey2024SuperSecureLongKeyForJWTTokenGeneration
jwt.expiration=86400000                                ← 24 hours in milliseconds

# ── OpenAI ───────────────────────────────────────────────────────────────
openai.api.key=YOUR_OPENAI_API_KEY                     ← CHANGE THIS
openai.api.url=https://api.openai.com/v1/chat/completions
openai.model=gpt-3.5-turbo

# ── CORS ─────────────────────────────────────────────────────────────────
cors.allowed-origins=http://localhost:3000,http://localhost:5173

# ── Email (Gmail SMTP) ───────────────────────────────────────────────────
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_gmail@gmail.com              ← CHANGE THIS
spring.mail.password=your_gmail_app_password           ← CHANGE THIS (App Password)
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# ── OTP Cache (Caffeine, 5-minute expiry) ────────────────────────────────
spring.cache.type=caffeine
spring.cache.cache-names=otpCache
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=5m
```

### Step 2 — Create MySQL database

```sql
CREATE DATABASE smartbiz_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> The database tables are created automatically by Hibernate on first startup (`ddl-auto=update`). You do **not** need to run any SQL scripts manually.

---

## Running the Application

### Development

```bash
# Navigate to backend directory
cd smartbiz/backend

# Install dependencies and compile
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at: **http://localhost:8080**

### Run compiled JAR

```bash
# Build the JAR
mvn clean package -DskipTests

# Run the JAR
java -jar target/smartbiz-backend-1.0.0.jar
```

### Verify it's running

```bash
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@smartbiz.com","password":"admin@123"}'
```

Expected response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJ...",
    "email": "admin@smartbiz.com",
    "name": "Super Admin",
    "role": "ADMIN",
    "id": 1
  }
}
```

---

## Data Seeder

The `DataSeeder` class implements `CommandLineRunner` and runs automatically on every application startup. It only inserts data if none exists yet (idempotent).

### What it seeds

**Admin Accounts:**

| Name | Email | Raw Password | Encrypted in DB |
|---|---|---|---|
| Super Admin | admin@smartbiz.com | `admin@123` | BCrypt hash |
| Support Admin | support@smartbiz.com | `support@456` | BCrypt hash |

**Subscription Plans:**

| Plan | Price | Duration |
|---|---|---|
| Free | $0.00 | 30 days |
| Starter | $9.99 | 30 days |
| Professional | $29.99 | 30 days |
| Enterprise | $99.99 | 365 days |

### Startup log output

```
========================================
SEEDING ADMIN ACCOUNTS
Both admins can login at the SAME login page as business users.
The system auto-detects the role (ADMIN vs BUSINESS).
========================================
Admin 1 Created:
  Name     : Super Admin
  Email    : admin@smartbiz.com
  Password (RAW)      : admin@123
  Password (ENCRYPTED): $2a$10$...
Admin 2 Created:
  Name     : Support Admin
  Email    : support@smartbiz.com
  Password (RAW)      : support@456
  Password (ENCRYPTED): $2a$10$...
========================================
```

> ⚠️ **Important**: Raw passwords are printed to the console **only once** on first startup for reference. Change them immediately in production by updating the database or modifying the seeder.

---

## Authentication & Security

### Unified Login

SmartBiz uses a **single login endpoint** for all user types:

```
POST /api/auth/login
```

The backend resolves the account in this order:
1. Checks the `admins` table — if email matches and password is correct → returns `role: "ADMIN"`
2. Checks the `businesses` table — if email matches and password is correct → returns `role: "BUSINESS"`
3. If neither matches → throws `"Invalid email or password"`

The frontend reads the `role` field in the response and redirects accordingly:
- `ADMIN` → `/admin` dashboard
- `BUSINESS` → `/dashboard`

### JWT Token

- Algorithm: `HS256`
- Expiry: 24 hours (configurable via `jwt.expiration`)
- Claims: `sub` (email), `role`
- Must be sent as `Authorization: Bearer <token>` on all protected requests

### Route Security

| Pattern | Access |
|---|---|
| `POST /api/auth/**` | Public (no token needed) |
| `POST /api/admin/auth/**` | Public |
| `GET/POST /api/admin/**` | Requires `ROLE_ADMIN` |
| All other `/api/**` | Requires valid JWT (any role) |

### Password Encryption

All passwords are hashed using **BCrypt** (`BCryptPasswordEncoder`). Plain-text passwords are never stored.

---

## Forgot Password — OTP Flow

The forgot password system works in three steps, all using the same `/api/auth` prefix (public, no auth required).

### Step 1 — Request OTP

```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**What happens internally:**
1. Looks up the email in `admins` table, then `businesses` table.
2. If found: generates a cryptographically secure 6-digit OTP using `SecureRandom`.
3. Stores the OTP in the Caffeine cache keyed by lowercase email, with a **5-minute TTL**.
4. Sends a styled HTML email to the user via Gmail SMTP.
5. Always returns `200 OK` regardless of whether the email exists (prevents email enumeration).

**Response:**
```json
{
  "success": true,
  "message": "If your email is registered, an OTP has been sent. Please check your inbox.",
  "data": null
}
```

### Step 2 — Verify OTP

```http
POST /api/auth/verify-otp
Content-Type: application/json

{
  "email": "user@example.com",
  "otp": "482910"
}
```

**What happens internally:**
- Retrieves OTP from Caffeine cache.
- Compares with submitted value.
- Returns `400` if OTP is wrong or has expired.
- Returns `200` if correct (OTP remains in cache for Step 3).

**Success response:**
```json
{
  "success": true,
  "message": "OTP verified successfully.",
  "data": null
}
```

### Step 3 — Reset Password

```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "email": "user@example.com",
  "otp": "482910",
  "newPassword": "mynewpassword123"
}
```

**What happens internally:**
1. Re-verifies OTP (double verification for security).
2. BCrypt-encodes the new password.
3. Updates the password in either `admins` or `businesses` table.
4. Clears the OTP from cache (`cache.evict(email)`) — OTP can only be used once.

**Success response:**
```json
{
  "success": true,
  "message": "Password reset successfully. Please login.",
  "data": null
}
```

### OTP Security Properties

| Property | Detail |
|---|---|
| Generation | `SecureRandom` — cryptographically secure |
| Length | 6 digits (`000000`–`999999`) |
| Storage | Caffeine in-memory cache (never persisted to DB) |
| Expiry | 5 minutes (configurable in `application.properties`) |
| Single-use | Cleared immediately after successful password reset |
| Email enumeration | Always returns `200` even if email not found |

---

## API Reference

### Response Format

Every endpoint returns a consistent wrapper:

```json
{
  "success": true | false,
  "message": "Human-readable message",
  "data": { ... } | [ ... ] | null
}
```

Error responses use HTTP `400`, `403`, or `500` with `"success": false`.

---

### Auth Endpoints — `POST /api/auth/...` (Public)

#### `POST /api/auth/login` — Unified Login

```json
// Request
{ "email": "user@example.com", "password": "secret123" }

// Response (Business)
{
  "data": {
    "token": "eyJ...",
    "email": "user@example.com",
    "name": "Acme Store",
    "role": "BUSINESS",
    "id": 5
  }
}

// Response (Admin)
{
  "data": {
    "token": "eyJ...",
    "email": "admin@smartbiz.com",
    "name": "Super Admin",
    "role": "ADMIN",
    "id": 1
  }
}
```

#### `POST /api/auth/register` — Register Business

```json
// Request
{
  "name": "Acme Store",
  "email": "acme@example.com",
  "password": "secure123",
  "phone": "+1 234 567 8900",
  "address": "123 Main St",
  "subscriptionId": 1
}
```

#### `POST /api/auth/forgot-password`

```json
{ "email": "user@example.com" }
```

#### `POST /api/auth/verify-otp`

```json
{ "email": "user@example.com", "otp": "123456" }
```

#### `POST /api/auth/reset-password`

```json
{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "newSecurePass!"
}
```

---

### Dashboard — `GET /api/dashboard` (Auth Required)

Returns a full summary for the current business:

```json
{
  "data": {
    "totalSalesThisMonth": 12450.00,
    "totalExpensesThisMonth": 3200.00,
    "netProfitThisMonth": 9250.00,
    "totalInvoices": 42,
    "totalCustomers": 18,
    "totalProducts": 35,
    "lowStockCount": 3,
    "topSellingProducts": [...],
    "recentInvoices": [...]
  }
}
```

---

### Customers — `/api/customers`

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/customers` | List all customers for current business |
| `GET` | `/api/customers/{id}` | Get single customer |
| `POST` | `/api/customers` | Create customer |
| `PUT` | `/api/customers/{id}` | Update customer |
| `DELETE` | `/api/customers/{id}` | Delete customer |

**Create/Update body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1 555 0100",
  "address": "456 Oak Avenue"
}
```

---

### Suppliers — `/api/suppliers`

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/suppliers` | List all suppliers |
| `GET` | `/api/suppliers/{id}` | Get single supplier |
| `POST` | `/api/suppliers` | Create supplier |
| `PUT` | `/api/suppliers/{id}` | Update supplier |
| `DELETE` | `/api/suppliers/{id}` | Delete supplier |

---

### Products — `/api/products`

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/products` | List all products |
| `GET` | `/api/products/{id}` | Get single product |
| `GET` | `/api/products/low-stock` | Products with stock ≤ 5 units |
| `POST` | `/api/products` | Create product |
| `PUT` | `/api/products/{id}` | Update product |
| `DELETE` | `/api/products/{id}` | Delete product |

**Create/Update body:**
```json
{
  "name": "Wireless Mouse",
  "price": 29.99,
  "stockQty": 150,
  "supplier": { "supplierId": 3 }
}
```

---

### Invoices — `/api/invoices`

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/invoices` | List all invoices |
| `GET` | `/api/invoices/{id}` | Get invoice with items |
| `POST` | `/api/invoices` | Create invoice (deducts stock) |
| `DELETE` | `/api/invoices/{id}` | Delete invoice |

**Create body:**
```json
{
  "customerId": 2,
  "items": [
    { "productId": 5, "quantity": 3 },
    { "productId": 8, "quantity": 1 }
  ]
}
```

> Invoice creation automatically deducts stock quantities. If stock is insufficient for any item, the entire transaction is rolled back (`@Transactional`).

---

### Expenses — `/api/expenses`

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/expenses` | List all expenses |
| `GET` | `/api/expenses/{id}` | Get single expense |
| `POST` | `/api/expenses` | Create expense |
| `PUT` | `/api/expenses/{id}` | Update expense |
| `DELETE` | `/api/expenses/{id}` | Delete expense |

**Create/Update body:**
```json
{
  "description": "Office supplies purchase",
  "amount": 145.50,
  "date": "2024-03-15"
}
```

---

### AI Assistant — `/api/ai`

#### `POST /api/ai/generate` — Generate AI Content

```json
// Request
{
  "feature": "INSIGHTS",
  "prompt": "What strategies can I use to increase sales this month?"
}
```

**Available features:**

| Feature | Description |
|---|---|
| `INSIGHTS` | Business analysis and strategy suggestions |
| `EMAIL` | Professional email drafting |
| `INVOICE_SUMMARY` | Plain-language invoice explanation |
| `SOCIAL_MEDIA` | Marketing post generation |

```json
// Response
{
  "data": {
    "response": "Based on your business context, here are 5 actionable strategies...",
    "feature": "INSIGHTS"
  }
}
```

Every AI request is automatically logged to the `ai_usages` table.

#### `GET /api/ai/history` — Usage History

Returns all AI requests made by the current business.

---

### Admin Endpoints — `/api/admin/**` (ADMIN role required)

#### `POST /api/admin/auth/login` — Admin Login (alternate endpoint)
Same as unified `/api/auth/login` but admin-only. Kept for compatibility.

#### Businesses

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/admin/businesses` | List all registered businesses |
| `DELETE` | `/api/admin/businesses/{id}` | Delete a business |

#### Subscriptions

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/admin/subscriptions` | List all plans |
| `POST` | `/api/admin/subscriptions` | Create new plan |
| `PUT` | `/api/admin/subscriptions/{id}` | Update plan |
| `DELETE` | `/api/admin/subscriptions/{id}` | Delete plan |

**Create/Update body:**
```json
{
  "planName": "Enterprise Plus",
  "price": 149.99,
  "durationDays": 365
}
```

#### Stats & AI Logs

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/admin/stats` | Platform-wide statistics |
| `GET` | `/api/admin/ai-usage` | All AI usage across all businesses |

---

## Email Setup (Gmail SMTP)

### Step 1 — Enable 2-Factor Authentication on your Google account

Go to [myaccount.google.com/security](https://myaccount.google.com/security) and enable 2FA.

### Step 2 — Create an App Password

1. Go to [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
2. Select app: **Mail** / device: **Other** → name it `SmartBiz`
3. Copy the generated 16-character password

### Step 3 — Update application.properties

```properties
spring.mail.username=your_actual_gmail@gmail.com
spring.mail.password=abcd efgh ijkl mnop     ← The 16-char App Password (spaces OK)
```

### What the OTP email looks like

The email is fully styled HTML with:
- SmartBiz branded header (indigo gradient)
- Large, prominent OTP code display
- 5-minute expiry notice
- Security warning note
- Footer with copyright

---

## Production Deployment

### AWS EC2 Deployment

```bash
# 1. Install Java 17
sudo apt update
sudo apt install openjdk-17-jdk -y

# 2. Install MySQL
sudo apt install mysql-server -y
sudo mysql_secure_installation

# 3. Create production database
mysql -u root -p
> CREATE DATABASE smartbiz_db;
> CREATE USER 'smartbiz'@'localhost' IDENTIFIED BY 'strong_password';
> GRANT ALL ON smartbiz_db.* TO 'smartbiz'@'localhost';

# 4. Build JAR (locally or on server)
mvn clean package -DskipTests

# 5. Upload JAR to EC2
scp target/smartbiz-backend-1.0.0.jar ec2-user@your-ec2-ip:/home/ec2-user/

# 6. Run with environment variables
java -jar smartbiz-backend-1.0.0.jar \
  --spring.datasource.password=strong_password \
  --jwt.secret=your_very_long_production_secret_key \
  --openai.api.key=sk-... \
  --spring.mail.username=your@gmail.com \
  --spring.mail.password=app_password \
  --spring.jpa.show-sql=false
```

### Systemd Service (run as daemon)

```ini
# /etc/systemd/system/smartbiz.service
[Unit]
Description=SmartBiz Backend
After=network.target mysql.service

[Service]
User=ec2-user
ExecStart=/usr/bin/java -jar /home/ec2-user/smartbiz-backend-1.0.0.jar
EnvironmentFile=/home/ec2-user/.smartbiz-env
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# /home/ec2-user/.smartbiz-env
SPRING_DATASOURCE_PASSWORD=strong_password
JWT_SECRET=your_production_secret
OPENAI_API_KEY=sk-...
SPRING_MAIL_USERNAME=you@gmail.com
SPRING_MAIL_PASSWORD=app_password
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable smartbiz
sudo systemctl start smartbiz
sudo journalctl -u smartbiz -f
```

### Nginx Reverse Proxy

```nginx
server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Production Checklist

- [ ] Change `spring.jpa.hibernate.ddl-auto` from `update` to `validate` after first run
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Set `logging.level.org.springframework.security=WARN`
- [ ] Use a strong, random `jwt.secret` (min 64 characters)
- [ ] Change default admin passwords after first login
- [ ] Configure CORS to your actual frontend domain only
- [ ] Enable HTTPS (SSL certificate via Let's Encrypt)
- [ ] Use environment variables or AWS Secrets Manager for all secrets

---

## Environment Variables Reference

All properties can be overridden via environment variables using Spring's relaxed binding:

| Property | Env Variable | Required | Default |
|---|---|---|---|
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | ✅ | — |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | ✅ | `root` |
| `jwt.secret` | `JWT_SECRET` | ✅ | — |
| `jwt.expiration` | `JWT_EXPIRATION` | ❌ | `86400000` (24h) |
| `openai.api.key` | `OPENAI_API_KEY` | ✅ | — |
| `spring.mail.username` | `SPRING_MAIL_USERNAME` | ✅ | — |
| `spring.mail.password` | `SPRING_MAIL_PASSWORD` | ✅ | — |
| `server.port` | `SERVER_PORT` | ❌ | `8080` |

---

*SmartBiz Backend — Built with Spring Boot 3.2 · Java 17 · MySQL 8*
