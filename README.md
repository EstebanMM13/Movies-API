# Movies API 🎬

¡Bienvenido a Movies API!

## Descripción 📝

Movies API es una aplicación backend desarrollada con Spring Boot para gestionar una base de datos de películas. Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre películas, así como funcionalidades adicionales como votar películas y gestionar valoraciones. El proyecto está diseñado con buenas prácticas, separación de capas (controller, service, repository) y un enfoque claro en la lógica de negocio.

## Tecnologías utilizadas ⚙️

- Java 17+
- Spring Boot (Web, Data JPA, Validation)
- Hibernate / JPA para persistencia
- Base de datos H2 (para pruebas) y MySQL (producción)
- Lombok para reducción de boilerplate
- JUnit y Mockito para testing
- Maven como sistema de construcción

## Características principales 🚀

- Gestión completa de películas con campos como título, descripción, año, imagen, votos y valoración.
- Sistema de votación que registra votos y calcula valoraciones medias.
- Validación de datos de entrada para garantizar integridad.
- Manejo de excepciones personalizado y respuestas REST coherentes.
- Tests unitarios y de integración para asegurar calidad.

# Futuro desarrollo 🔮
Integración de un frontend desarrollado con Angular para consumir esta API y ofrecer una experiencia de usuario completa y moderna.
