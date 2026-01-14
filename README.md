# Movies API 🎬

¡Bienvenido a Movies API!

## Descripción 📝

Movies API es una aplicación backend desarrollada con Spring Boot para gestionar una base de datos de películas. Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre películas, así como funcionalidades adicionales como votar películas y gestionar valoraciones. 
El proyecto está diseñado con buenas prácticas, separación de capas (controller, service, repository), autenticación con JWT, un enfoque claro en la lógica de negocio y manejo de errores.

## Tecnologías utilizadas ⚙️

- Java 17+
- Spring Boot (Web, Data JPA, Validation, Security)
- Hibernate / JPA para persistencia
- Base de datos H2 (para pruebas) y MySQL (producción)
- Lombok para reducción de boilerplate
- JUnit y Mockito para testing
- Maven como sistema de construcción

## Características principales 🚀

- Gestión completa de películas con campos como título, descripción, año, imagen, votos y valoración.
- Sistema de votación que registra votos y calcula valoraciones medias.
- Validación de datos de entrada para garantizar integridad.
- Autenticación basada en tokens JWT para proteger endpoints
- Manejo de excepciones personalizado y respuestas REST coherentes.
- Tests unitarios y de integración para asegurar calidad.

## Endpoints principales 🔗

- POST  /auth/register
- POST  /auth/authenticate
- GET  /movies
- GET /movies/title/{title}
- POST  /movies
- POST  /movies/{movieId}/reviews

## Ejecutar proyecto ▶️

Usa H2 por defecto para facilitar pruebas sin configuración adicional.

1. Clonar el repositorio
2. Configurar application.properties (si aplica)
3. Ejecutar con mvn spring-boot:run
4. Acceder a http://localhost:8080

## Futuro desarrollo 🔮

- Mejorar sistema de roles
- Añadir paginación y filtros
- Documentación con Swagger
- Integración de un frontend desarrollado con Angular para consumir esta API y ofrecer una experiencia de usuario completa y moderna.
