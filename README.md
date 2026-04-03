# 💼 Expense Tracker Backend (Spring Boot)

A **production-style RESTful backend API** built using Java Spring Boot that enables small businesses to **record, categorize, filter, and analyze expenses**.

Inspired by real-world tools like **Zoho Books** and **QuickBooks**.

---

## 🚀 Features

* Expense management (add, update, delete)
* Category-based organization
* Date & category filtering
* Monthly and category-wise reports
* JWT-based authentication
* Clean layered architecture

---

## 🏗️ Architecture

```
com.expensetracker
├── controller     → Handles HTTP requests & responses
├── service        → Business logic & validation
├── repository     → Database access (JPA/Hibernate)
├── entity         → Database models
├── dto            → Request/response objects
├── config         → Security & JWT configuration
└── exception      → Global error handling
```

---

## 🛠️ Tech Stack

| Layer      | Technology            |
| ---------- | --------------------- |
| Language   | Java 17               |
| Framework  | Spring Boot 3         |
| Database   | MySQL                 |
| ORM        | JPA / Hibernate       |
| Security   | Spring Security + JWT |
| Build Tool | Maven                 |
| Testing    | JUnit                 |

---

## 🗄️ Database Schema

**Users**

* id, name, email, password, created_at

**Categories**

* id, name, user_id, created_at

**Expenses**

* id, amount, description, date
* category_id, user_id
* created_at, updated_at

---

## ⚙️ Getting Started

### 1. Create Database

```sql
CREATE DATABASE expense_tracker_db;
```

---

### 2. Configure Application

Edit `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

---

### 3. Run Application

```bash
mvn spring-boot:run
```

Application runs at:

```
http://localhost:8080/api
```

---

## 🔐 Authentication

Uses JWT (Bearer Token)

### Register

```
POST /api/auth/register
```

### Login

```
POST /api/auth/login
```

Use token in headers:

```
Authorization: Bearer <token>
```

---

## 📡 API Endpoints

### 🔑 Auth

* POST `/auth/register`
* POST `/auth/login`

---

### 📁 Categories

* POST `/categories`
* GET `/categories`
* PUT `/categories/{id}`
* DELETE `/categories/{id}`

---

### 💸 Expenses

* POST `/expenses`
* GET `/expenses`
* PUT `/expenses/{id}`
* DELETE `/expenses/{id}`

**Filters:**

* GET `/expenses/filter?startDate=&endDate=`
* GET `/expenses/category/{id}`

---

### 📊 Reports

* GET `/reports/monthly?month=&year=`
* GET `/reports/category`
* GET `/reports/summary`

---

## 📌 Business Rules

* Users can access only their own data
* Each expense must belong to a valid category
* Amount must be greater than 0
* Categories with expenses cannot be deleted
* Passwords are securely hashed (BCrypt)

---

## 🧪 Running Tests

```bash
mvn test
```

---

## 📁 Project Structure

```
expense-tracker/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── config/
├── exception/
├── resources/
└── pom.xml

---

## 👤 Author

Bhavana M
Aruna shivani R
Dharshini G

---
