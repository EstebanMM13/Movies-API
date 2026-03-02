package com.estebanmmk13.movies.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Movies API",
                version = "1.0",
                description = """
                        **API RESTful para la gestión de películas y usuarios.**
                        
                        ---
                        
                        ### ✨ Características principales:
                        
                        *   **Gestión de películas**: CRUD completo con información detallada (título, descripción, año, imagen, etc.).
                        *   **Sistema de votación**: Los usuarios pueden votar y reseñar películas, con cálculo automático de valoraciones medias.
                        *   **Autenticación segura**: Basada en tokens JWT para proteger endpoints sensibles.
                        *   **Validación de datos**: Entrada de datos validada para garantizar integridad.
                        *   **Manejo de errores**: Respuestas coherentes y descriptivas ante errores.
                        
                        ---
                        
                        ### 🔐 Cómo usar la autenticación:
                        
                        1.  Regístrate en `/auth/register` o haz login en `/auth/authenticate` para obtener un token JWT.
                        2.  Copia el token recibido (sin comillas).
                        3.  Haz clic en el botón **Authorize** (🔒) en la parte superior derecha.
                        4.  Introduce el token en el formato: `Bearer <tu-token-jwt>` (ejemplo: `Bearer eyJhbGciOiJIUzI1NiIs...`).
                        5.  ¡Listo! Ahora puedes probar los endpoints protegidos.
                        
                        ---
                        
                        ### 🚀 Próximamente:
                        
                        *   Paginación y filtros avanzados
                        *   Roles de usuario (USER, ADMIN)
                        *   Documentación interactiva mejorada
                        
                        ---
                        """,
                termsOfService = "https://github.com/EstebanMM13/Movies-API/blob/main/TERMS.md",
                contact = @Contact(
                        name = "Esteban Martínez",
                        email = "2001estebanmartinez@gmail.com",
                        url = "https://github.com/EstebanMM13"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        description = "🖥️ Servidor local de desarrollo",
                        url = "http://localhost:8085"
                ),
                @Server(
                        description = "☁️ Servidor de producción (próximamente)",
                        url = "https://api.moviesapp.com"
                )
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = """
                🔑 **Token JWT requerido para endpoints protegidos.**
                
                ### ¿Cómo obtenerlo?
                1.  Haz una petición `POST` a `/auth/authenticate` con email y contraseña válidos.
                2.  El servidor responderá con un token JWT.
                3.  Copia el token (sin comillas) y pégalo aquí con el formato: `Bearer <token>`
                
                ### Ejemplo:
                `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c`
                """,
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {}