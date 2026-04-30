package com.estebanmmk13.movies.dtoModels.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Data to create or update a user")
public class UserRequestDTO {
    @NotBlank
    @Schema(description = "Username", example = "john_doe", required = true)
    private String username;

    @NotBlank
    @Email
    @Schema(description = "Email address", example = "john@example.com", required = true)
    private String email;

    @NotBlank
    @Schema(description = "Password (min 6 characters)", example = "secret123", required = true)
    private String password;

    @Schema(description = "User role (optional, defaults to USER)", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role;
}
