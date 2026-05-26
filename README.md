# 📚 Library Management System

A full-featured **Library Management System** built with **Spring Boot** and **PostgreSQL**, featuring a clean **HTML + Bootstrap** frontend — no React or Angular required.

---

## 🌐 Live Preview

> Run locally at: `http://localhost:8080`

![Dashboard](https://img.shields.io/badge/Status-Complete-brightgreen)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple)

---

## ✨ Features

- 📖 **Book Management** — Add, edit, delete, search books with copy tracking
- 👥 **Member Management** — Register members, activate/deactivate accounts
- 🔄 **Borrow & Return** — Issue books with smart search dropdowns, return with one click
- ⏰ **Overdue Detection** — Automatic daily scheduler marks overdue records at midnight
- 📊 **Dashboard** — Live stats for books, members, active borrows and overdue count
- 📋 **Swagger UI** — Full API documentation and testing interface
- 🔒 **Input Validation** — Bean validation on all API inputs
- ⚠️ **Global Exception Handling** — Clean error responses across all endpoints

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Database | PostgreSQL 18 |
| ORM | Hibernate / Spring Data JPA |
| Frontend | HTML5, Bootstrap 5.3, Vanilla JS |
| API Docs | Swagger UI (SpringDoc OpenAPI 3) |
| Build Tool | Maven |
| Version Control | Git + GitHub |

---

## 📁 Project Structure


library-management/
├── src/
│   ├── main/
│   │   ├── java/com/vipin/library_management/
│   │   │   ├── config/
│   │   │   │   ├── OpenApiConfig.java        # Swagger configuration
│   │   │   │   └── WebConfig.java            # CORS configuration
│   │   │   ├── controller/
│   │   │   │   ├── BookController.java       # Book REST APIs
│   │   │   │   ├── MemberController.java     # Member REST APIs
│   │   │   │   └── BorrowController.java     # Borrow/Return REST APIs
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java          # Generic API response wrapper
│   │   │   │   ├── BookRequestDTO.java
│   │   │   │   ├── BookResponseDTO.java
│   │   │   │   ├── MemberRequestDTO.java
│   │   │   │   ├── MemberResponseDTO.java
│   │   │   │   ├── BorrowRequestDTO.java
│   │   │   │   └── BorrowResponseDTO.java
│   │   │   ├── entity/
│   │   │   │   ├── Book.java                 # Book entity
│   │   │   │   ├── Member.java               # Member entity
│   │   │   │   ├── BorrowRecord.java         # Borrow record entity
│   │   │   │   └── BorrowStatus.java         # Enum: BORROWED, RETURNED, OVERDUE
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── LibraryException.java
│   │   │   ├── repository/
│   │   │   │   ├── BookRepository.java
│   │   │   │   ├── MemberRepository.java
│   │   │   │   └── BorrowRecordRepository.java
│   │   │   ├── scheduler/
│   │   │   │   └── OverdueScheduler.java     # Runs daily at midnight
│   │   │   └── service/
│   │   │       ├── BookService.java
│   │   │       ├── MemberService.java
│   │   │       └── BorrowService.java
│   │   └── resources/
│   │       ├── static/                       # Frontend files
│   │       │   ├── index.html                # Dashboard
│   │       │   ├── books.html                # Books management
│   │       │   ├── members.html              # Members management
│   │       │   ├── borrow.html               # Borrow & Return
│   │       │   ├── css/style.css
│   │       │   └── js/
│   │       │       ├── api.js                # Shared fetch helper
│   │       │       ├── dashboard.js
│   │       │       ├── books.js
│   │       │       ├── members.js
│   │       │       └── borrow.js
│   │       └── application.properties.example
└── pom.xml

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- PostgreSQL 14+
- Maven 3.8+

### 1. Clone the repository

git clone git@github.com:Vipin200701/library-management.git
cd library-management


### 2. Create the database

CREATE DATABASE library_db;


### 3. Configure application.properties

cp src/main/resources/application.properties.example src/main/resources/application.properties


Edit `application.properties` with your credentials:

spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=your_username
spring.datasource.password=your_password


### 4. Run the application

./mvnw spring-boot:run


### 5. Open in browser

| URL | Description |
|-----|-------------|
| http://localhost:8080 | Frontend Dashboard |
| http://localhost:8080/books.html | Books Management |
| http://localhost:8080/members.html | Members Management |
| http://localhost:8080/borrow.html | Borrow & Return |
| http://localhost:8080/swagger-ui.html | Swagger API Docs |

---

## 📡 API Endpoints

### Books — `/api/books`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/books` | Get all books |
| `GET` | `/api/books/{id}` | Get book by ID |
| `GET` | `/api/books/search?keyword=` | Search by title or author |
| `POST` | `/api/books` | Add a new book |
| `PUT` | `/api/books/{id}` | Update a book |
| `DELETE` | `/api/books/{id}` | Delete a book |

### Members — `/api/members`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/members` | Get all members |
| `GET` | `/api/members/{id}` | Get member by ID |
| `GET` | `/api/members/search?name=` | Search by name |
| `GET` | `/api/members/active` | Get active members |
| `POST` | `/api/members` | Register a member |
| `PUT` | `/api/members/{id}` | Update member |
| `PATCH` | `/api/members/{id}/deactivate` | Deactivate member |
| `PATCH` | `/api/members/{id}/reactivate` | Reactivate member |
| `DELETE` | `/api/members/{id}` | Delete member |

### Borrow & Return — `/api/borrow`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/borrow/issue` | Issue a book to member |
| `PATCH` | `/api/borrow/return/{id}` | Return a borrowed book |
| `GET` | `/api/borrow/active` | All active borrows |
| `GET` | `/api/borrow/overdue` | All overdue borrows |
| `GET` | `/api/borrow/{id}` | Get single borrow record |
| `GET` | `/api/borrow/member/{id}/history` | Member borrow history |
| `GET` | `/api/borrow/book/{id}/history` | Book borrow history |
| `PATCH` | `/api/borrow/mark-overdue` | Manually mark overdue |

---

## 🗄️ Database Schema


books
├── id (PK)
├── title
├── author
├── isbn (unique)
├── genre
├── published_year
├── total_copies
├── available_copies
├── created_at
└── updated_at

members
├── id (PK)
├── name
├── email (unique)
├── phone
├── membership_date
├── is_active
├── created_at
└── updated_at

borrow_records
├── id (PK)
├── book_id (FK → books)
├── member_id (FK → members)
├── borrow_date
├── due_date
├── return_date
├── status (BORROWED / RETURNED / OVERDUE)
└── created_at


---

## ⚙️ Business Rules

| Rule | Behaviour |
|------|-----------|
| Inactive member borrows | ❌ Blocked — 400 Bad Request |
| No copies available | ❌ Blocked — 400 Bad Request |
| Member borrows same book twice | ❌ Blocked — 400 Bad Request |
| Book returned twice | ❌ Blocked — 400 Bad Request |
| Due date not provided | ✅ Auto set to 14 days from today |
| Book issued | ✅ Available copies decrease by 1 |
| Book returned | ✅ Available copies increase by 1 |
| Past due date | ✅ Auto marked OVERDUE every midnight |

---

## 🔧 Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | App runs on this port |
| `ddl-auto` | `update` | Hibernate auto-creates tables |
| `show-sql` | `true` | Prints SQL in console |
| `open-in-view` | `false` | Prevents lazy loading issues |
| Scheduler cron | `0 0 0 * * *` | Runs overdue check at midnight |

---

## 👨‍💻 Author

**Vipin Kumar**
- GitHub: [@Vipin200701](https://github.com/Vipin200701)

---

## 📝 License

This project is open source and available under the [MIT License](LICENSE).
