package com.estebanmmk13.movies.dtoModels.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Genre information")
public class GenreResponseDTO {
    @Schema(description = "Genre ID", example = "1")
    private Long id;

    @Schema(description = "Genre name", example = "Action")
    private String name;
}
