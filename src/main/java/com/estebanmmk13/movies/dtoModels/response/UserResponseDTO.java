package com.estebanmmk13.movies.dtoModels.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information (safe, no password)")
public class UserResponseDTO {
    @Schema(description = "User ID", example = "42")
    private Long id;

    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Schema(description = "User role", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role;
}
