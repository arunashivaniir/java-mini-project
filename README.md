# 💼 Expense Tracker for Small Businesses

A production-grade RESTful backend API built with **Java Spring Boot** that enables small businesses to record, categorize, filter, and report on their daily expenses — inspired by tools like Zoho Books and QuickBooks.

---

## 🏗️ Architecture

```
com.expensetracker
├── controller/       → HTTP request handling, routing, response codes
├── service/          → Business logic, validation rules
├── repository/       → JPA/Hibernate database access
├── entity/           → JPA database models (User, Category, Expense)
├── dto/
│   ├── request/      → Incoming API payloads (validated)
│   └── response/     → Outgoing API payloads (no entity exposure)
├── config/           → Security, JWT, Spring configuration
└── exception/        → Global error handling
```

---

## 🛠️ Tech Stack

| Layer        | Technology                        |
|--------------|-----------------------------------|
| Language     | Java 17                           |
| Framework    | Spring Boot 3.2                   |
| Database     | MySQL 8.0                         |
| ORM          | Spring Data JPA / Hibernate       |
| Security     | Spring Security + JWT (JJWT)      |
| Build Tool   | Maven                             |
| Containers   | Docker + Docker Compose           |
| Testing      | JUnit 5 + Spring Boot Test        |

---

## 🗄️ Database Schema

```
users
  id (PK), name, email (UNIQUE), password, created_at

categories
  id (PK), name, user_id (FK → users), created_at

expenses
  id (PK), amount, description, date,
  category_id (FK → categories),
  user_id (FK → users),
  created_at, updated_at
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0 (or Docker)

### Option A — Run with Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/your-username/expense-tracker.git
cd expense-tracker

# Start MySQL + App
docker-compose up --build
```

App will be available at: `http://localhost:8080/api`

---

### Option B — Run Locally

**1. Create MySQL database**
```sql
CREATE DATABASE expense_tracker_db;
```

**2. Configure credentials**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker_db?...
spring.datasource.username=your_mysql_user
spring.datasource.password=your_mysql_password
```

**3. Build and run**
```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

App starts at: `http://localhost:8080/api`

---

## 🔐 Authentication

This API uses **JWT Bearer Token** authentication.

1. Register → `POST /api/auth/register`
2. Login → `POST /api/auth/login`
3. Copy the `token` from the response
4. Add header to all subsequent requests:
   ```
   Authorization: Bearer <your_token_here>
   ```

---

## 📡 API Reference

> **Base URL:** `http://localhost:8080/api`  
> All protected endpoints require `Authorization: Bearer <token>` header.

---

### 🔑 Auth Endpoints

#### Register
```http
POST /auth/register
Content-Type: application/json

{
  "name": "Acme Corp",
  "email": "admin@acme.com",
  "password": "secure123"
}
```
**Response 201:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "name": "Acme Corp",
    "email": "admin@acme.com"
  }
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@acme.com",
  "password": "secure123"
}
```

---

### 📁 Category Endpoints

#### Create Category
```http
POST /categories
Authorization: Bearer <token>

{ "name": "Office Supplies" }
```

#### Get All Categories
```http
GET /categories
Authorization: Bearer <token>
```

#### Update Category
```http
PUT /categories/{id}
Authorization: Bearer <token>

{ "name": "Office Equipment" }
```

#### Delete Category
```http
DELETE /categories/{id}
Authorization: Bearer <token>
```
> ⚠️ Returns 400 if expenses exist under this category.

---

### 💸 Expense Endpoints

#### Add Expense
```http
POST /expenses
Authorization: Bearer <token>

{
  "amount": 1500.00,
  "description": "Monthly office rent",
  "date": "2025-03-01",
  "categoryId": 2
}
```

#### Get All Expenses (Paginated)
```http
GET /expenses?page=0&size=10&sortBy=date&sortDir=desc
Authorization: Bearer <token>
```

#### Filter by Date Range
```http
GET /expenses/filter?startDate=2025-03-01&endDate=2025-03-31&page=0&size=10
Authorization: Bearer <token>
```

#### Filter by Category
```http
GET /expenses/category/{categoryId}?page=0&size=10
Authorization: Bearer <token>
```

#### Update Expense
```http
PUT /expenses/{id}
Authorization: Bearer <token>

{
  "amount": 1600.00,
  "description": "Updated rent",
  "date": "2025-03-01",
  "categoryId": 2
}
```

#### Delete Expense
```http
DELETE /expenses/{id}
Authorization: Bearer <token>
```

---

### 📊 Report Endpoints

#### Monthly Report
```http
GET /reports/monthly?month=3&year=2025
Authorization: Bearer <token>
```
**Response:**
```json
{
  "success": true,
  "data": {
    "month": 3,
    "year": 2025,
    "totalAmount": 4750.00,
    "totalTransactions": 8,
    "expenses": [ ... ]
  }
}
```

#### Category-Wise Report
```http
GET /reports/category
Authorization: Bearer <token>
```
**Response:**
```json
{
  "success": true,
  "data": [
    { "categoryId": 1, "categoryName": "Rent", "totalAmount": 1500.00, "transactionCount": 1 },
    { "categoryId": 2, "categoryName": "Food", "totalAmount": 850.00, "transactionCount": 12 }
  ]
}
```

#### Summary Report
```http
GET /reports/summary
Authorization: Bearer <token>
```
**Response:**
```json
{
  "success": true,
  "data": {
    "totalExpenses": 9250.50,
    "totalTransactions": 34,
    "averageTransactionAmount": 272.07,
    "categoryBreakdown": [ ... ]
  }
}
```

---

## ✅ HTTP Status Codes

| Code | Meaning                    |
|------|----------------------------|
| 200  | OK — success               |
| 201  | Created — resource created |
| 400  | Bad Request — validation   |
| 401  | Unauthorized — bad/no JWT  |
| 404  | Not Found — resource missing |
| 500  | Server Error               |

---

## 🔒 Business Rules

- Each user can only access **their own** data (enforced via JWT)
- Every expense **must** belong to a category owned by the same user
- Amount must be **> 0** and date is **required**
- Categories with existing expenses **cannot be deleted**
- Passwords are **BCrypt hashed** — never stored in plaintext

---

## 🧪 Running Tests

```bash
mvn test
```

Tests use an in-memory **H2 database** — no MySQL setup needed.

---

## 📁 Project Structure

```
expense-tracker/
├── src/
│   ├── main/
│   │   ├── java/com/expensetracker/
│   │   │   ├── ExpenseTrackerApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── ExpenseController.java
│   │   │   │   └── ReportController.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   ├── ExpenseService.java
│   │   │   │   ├── ReportService.java
│   │   │   │   └── SecurityContextService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   └── ExpenseRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Category.java
│   │   │   │   └── Expense.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── AuthRequest.java
│   │   │   │   │   ├── CategoryRequest.java
│   │   │   │   │   └── ExpenseRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── ApiResponse.java
│   │   │   │       ├── AuthResponse.java
│   │   │   │       ├── CategoryResponse.java
│   │   │   │       ├── ExpenseResponse.java
│   │   │   │       └── ReportResponse.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── BadRequestException.java
│   │   │       └── UnauthorizedException.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/expensetracker/
│       │   └── AuthServiceTest.java
│       └── resources/
│           └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🔮 Future Enhancements

- [ ] Budget limits per category with alerts
- [ ] Export reports to PDF/CSV
- [ ] Multi-currency support
- [ ] Role-based access (Admin / Accountant / Viewer)
- [ ] Email notifications for overspending
- [ ] Dashboard analytics endpoint
- [ ] Recurring expense templates

---

## 👤 Author

Built as a production-style backend system demonstrating clean architecture, JWT security, layered design, and real-world SaaS patterns in Spring Boot.
