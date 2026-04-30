package com.estebanmmk13.movies.dtoModels.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing JWT token")
public class AuthResponse {
    @Schema(description = "JWT token (Bearer)", example = "eyJhbGciOiJIUzI1NiIs...")
    private String token;
}
