# 🎬 Movies API

A fully documented RESTful API built with Spring Boot for managing movies, users, genres, reviews and voting functionality.

This project follows clean architecture principles and implements authentication, pagination, validation, and comprehensive testing.

---

## 🚀 Features

- 🔐 JWT Authentication (Register & Login)
- 👤 User Management
- 🎬 Full Movie CRUD
- 🎭 Genre Management
- ⭐ Review system per movie
- 👍 Voting system with automatic rating calculation
- 📄 Pagination & sorting support (Spring Pageable)
- 📘 Swagger (OpenAPI 3) documentation
- ❌ Global exception handling (`@ControllerAdvice`)
- ✅ DTO layer with validation
- 🧪 Unit & integration tests (Mockito + H2)

---

## 🛠 Tech Stack

- Java 17
- Spring Boot (Web, Data JPA, Validation, Security)
- Hibernate / JPA
- MySQL (production profile)
- H2 (test profile)
- JWT (stateless authentication)
- Swagger / OpenAPI 3
- Maven
- JUnit & Mockito

---

## 📘 API Documentation

Swagger UI available at: http://localhost:8085/swagger-ui/index.html

Controllers are grouped by domain:
- Authentication
- Users
- Movies
- Genres
- Reviews

---

## 📄 Pagination Example

All list endpoints support pagination using Spring `Pageable`.

Example: GET /movies?page=0&size=5&sort=title,asc

Query parameters:
- `page` → Page number (0-based)
- `size` → Number of elements per page
- `sort` → Field and direction (e.g., title,asc)

---

## 🔐 Authentication

### Register
POST /auth/register
### Login
POST /auth/authenticate

Returns a JWT token that must be included in protected endpoints: 

Authorization: Bearer YOUR_TOKEN

---

## 🏗 Architecture

The project follows a layered architecture:
Controller → Service → Repository → Database
Additional structure:
- DTO layer for API responses
- Entity-to-DTO mapping
- Centralized exception handling
- Input validation using Jakarta Validation
- Environment-based configuration (`dev`, `qa`, `test`)

---

## 🧪 Testing

- Unit tests using Mockito
- Integration tests with H2 in-memory database
- Separate test profile configuration

Run tests with: mvn test
---

## ▶ Run the Project

1. Clone the repository
2. Configure `application.yml` if needed
3. Run: mvn spring-boot:run
4. Access: http://localhost:8085
---

## 🔮 Future Improvements

- Role-based authorization (ADMIN / USER)
- Docker containerization
- Refresh tokens
- CI/CD integration
- Advanced filtering (Specifications / Criteria API)

---

## 👨‍💻 Author

Esteban Martínez  
Backend Developer | Java & Spring Boot
