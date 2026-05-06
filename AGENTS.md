# AGENTS.md - Movies API

## Instrucciones para asistentes de IA (Cursor, Copilot, Claude, etc.)

Eres un asistente de IA que ayuda a desarrollar y mantener esta API. Sigue estas reglas estrictamente.

---

## 📚 Stack del proyecto

- **Lenguaje**: Java 17 (o superior)
- **Framework**: Spring Boot 3.5.3
- **Seguridad**: Spring Security + JWT
- **Persistencia**: Spring Data JPA + Hibernate
- **Base de datos**: MySQL (dev/qa/prod) / H2 (test)
- **Migración**: `ddl-auto: update` solo en desarrollo (en producción `validate`)
- **Logging**: SLF4J + Logback, con `@Slf4j` y correlationId
- **Documentación**: Swagger/OpenAPI (springdoc-openapi)
- **Build**: Maven

---

## 🧱 Estructura del proyecto
src/main/java/com/estebanmmk13/movies/
├── config/ # Configuraciones (Security, JWT, filtros, OpenAPI)
├── controllers/ # Controladores REST (solo orquestación)
├── services/ # Interfaces de servicios
├── services/impl/ # Implementaciones con lógica de negocio
├── repositories/ # Repositorios JPA
├── models/ # Entidades JPA
├── dtoModels/ # DTOs (Request y Response)
├── mapper/ # Mappers (conversión entidad ↔ DTO)
├── error/ # Excepciones personalizadas
└── security/ # Clases de seguridad (JwtService, JwtFilter, etc.)


---

## 🎯 Convenciones de código

- **Nombres de clases**: PascalCase (`MovieController`, `UserServiceImpl`)
- **Nombres de métodos y variables**: camelCase (`findMovieById`, `userRepository`)
- **Nombres de paquetes**: minúsculas y sin guiones
- **DTOs**: Siempre separar Request (`*RequestDTO`) de Response (`*ResponseDTO`)
- **Entidades JPA**: Usar Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- **Logging**: Usar `@Slf4j` y niveles adecuados (INFO para acciones importantes, DEBUG para detalles, WARN para anomalías, ERROR para fallos)

---

## 🚫 Reglas para agentes IA

1. **No generar código que exponga la entidad User directamente** → Siempre usar `UserResponseDTO` (sin password) y `UserRequestDTO`.
2. **No usar `System.out.println`** → Siempre usar `log.info(...)` o el nivel correspondiente.
3. **No hardcodear credenciales** → Usar variables de entorno o `application.yml` con `${...}`.
4. **No modificar el esquema de la base de datos en producción** → Usar `ddl-auto: validate`.
5. **No cambiar la estructura del proyecto sin consultar**.
6. **Siempre incluir el correlationId en los logs** (ya configurado en `CorrelationIdFilter` y patrón de log).

---

## 🧪 Testing

- **Framework**: JUnit 5 + Mockito + Spring Boot Test
- **Perfil de test**: `test` (usa H2 en memoria)
- **Ejecutar tests**: `mvn test`
- **Cobertura deseada**: Al menos un test por cada servicio público (create, update, delete, find)

---

## 📦 Comandos útiles

| Comando | Descripción |
|---------|-------------|
| `mvn clean compile` | Compilar el proyecto |
| `mvn spring-boot:run` | Ejecutar la aplicación (perfil `dev` por defecto) |
| `mvn test` | Ejecutar todos los tests |
| `mvn test -Dtest=MovieServiceTest` | Ejecutar un test específico |
| `java -jar target/movies.jar --spring.profiles.active=prod` | Ejecutar en producción |

---

## 🔐 JWT y seguridad

- El `JwtFilter` valida los tokens en cada petición.
- El `JwtService` genera y valida tokens con clave secreta (leída de `jwt.secret`).
- Los endpoints públicos: `/api/auth/**` y `/swagger-ui/**`, `/v3/api-docs/**`.
- **Protegido**: el resto de endpoints requieren token (JWT) en cabecera `Authorization: Bearer <token>`.

---

## 📡 Logging y correlationId

- Cada petición tiene un `correlationId` (cabecera `X-Correlation-Id`).
- Se imprime en cada línea de log: `[%X{correlationId}]`.
- Niveles por entorno:
    - `dev`: DEBUG para `com.estebanmmk13.movies`, WARN/ERROR para librerías.
    - `qa`: INFO para `com.estebanmmk13.movies`, WARN/ERROR para librerías.
    - `prod`: WARN/ERROR general.

---

## 🗃️ Base de datos

- **Dev**: MySQL en `localhost:3306/movies_dev` (usuario `root`, contraseña `1234`).
- **QA**: MySQL en `localhost:3306/movies_qa`.
- **Prod**: MySQL en `localhost:3306/movies_prod` (contraseña desde variable `DB_PASSWORD`).
- **Test**: H2 en memoria (modo MySQL).

---

## ✨ Mejoras pendientes (futuro)

- Frontend básico (React)
- Despliegue con Docker + CI/CD (GitHub Actions)
- Integración con TMDB para buscar películas
- Sistema de recomendaciones
- Role-based authorization (ADMIN / USER)

---

## 📝 Notas para la IA

- Si te pido que generes código, sigue estas convenciones.
- Si no estás seguro de algo, pregunta antes de escribir.
- No modifiques archivos de configuración sin confirmación (`application.yml`, `pom.xml`).
- Para añadir una nueva entidad, sigue el patrón de `Movie`: entidad → DTOs → mapper → servicio → controlador → tests.