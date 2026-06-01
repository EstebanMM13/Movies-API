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
- 📊 Structured logging with correlation ID
- 🐳 Docker containerization
- ⚙️ CI/CD with GitHub Actions
- ☁️ Docker Hub integration

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
- Docker & Docker Compose
- GitHub Actions (CI/CD)
- SLF4J & Logback (structured logging)
  
---

## 📘 API Documentation

Swagger UI available at: http://localhost:8085/swagger-ui/index.html

Controllers are grouped by domain:
- Authentication
- Users
- Movies
- Genres
- Reviews

> **Note:** If you are running Docker in a virtual machine or remote server, replace `localhost` with the VM's IP address (e.g., `http://192.168.1.100:8085/swagger-ui/index.html`).

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
## 🐳 Run with Docker (recommended)
The easiest way to run the entire stack (API + MySQL).

Prerequisites:

Docker and Docker Compose installed

Steps:

Clone the repository:
git clone https://github.com/EstebanMM13/Movies-API.git
cd Movies-API

Start the containers:
docker compose up -d

Wait 30 seconds for MySQL to start, then access:
http://localhost:8085/swagger-ui/index.html

To view logs:
docker compose logs -f

To stop the containers:
docker compose down

Preloaded test data:

The database comes preloaded with:

Genres: Action, Adventure, Comedy, Drama, Sci-Fi, Terror, Romance, Animation

Movies: Inception, The Matrix, Interstellar, The Dark Knight, Pulp Fiction

Test users: user / 123456 (ROLE_USER), admin / 123456 (ROLE_ADMIN)

Environment variables (optional):

You can customize the Swagger URL in docker-compose.yml:
SWAGGER_URL: http://localhost:8085 (Change to your IP/domain)

---
## ▶ Run locally without Docker
Install MySQL locally and create a database named movies_dev.

Configure application-dev.yml with your MySQL credentials (username, password).

Run the application using Maven:
mvn spring-boot:run

Access: http://localhost:8085/swagger-ui/index.html

To run tests:
mvn test

---
## ⚙️ CI/CD Pipeline
This project uses GitHub Actions for Continuous Integration and Continuous Delivery.

What happens on every push to master?

CI: Compiles the code and runs all unit/integration tests (using H2 in-memory database).
CD: If tests pass, builds a Docker image and pushes it to Docker Hub (estebanmm13/movies-api:latest).

Pipeline configuration:

Workflow file: .github/workflows/ci-cd.yml

Secrets required: DOCKER_USERNAME, DOCKER_PASSWORD

Runs on: ubuntu-latest

Status badge (optional):
https://github.com/EstebanMM13/Movies-API/actions/workflows/ci-cd.yml/badge.svg

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
- Refresh tokens
- Advanced filtering (Specifications / Criteria API)

---

## 📚 Interactive Documentation

You can explore the automatically generated documentation with [DeepWiki](https://deepwiki.com/EstebanMM13/Movies-API).

---

## 👨‍💻 Author

Esteban Martínez  
Backend Developer | Java & Spring Boot
