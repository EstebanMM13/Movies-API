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
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Movies API",
                version = "1.0",
                description = "... (tu descripción) ...",
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
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "...",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Value("${SWAGGER_URL:http://localhost:8085}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new io.swagger.v3.oas.models.servers.Server().url(serverUrl).description("Servidor actual"),
                        new io.swagger.v3.oas.models.servers.Server().url("http://localhost:8085").description("Servidor local")
                ))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Movies API")
                        .version("1.0")
                        .description("API para gestionar películas, reseñas y usuarios")
                );
    }
}