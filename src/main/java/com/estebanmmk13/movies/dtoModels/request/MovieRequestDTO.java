package com.estebanmmk13.movies.dtoModels.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Data required to create or update a movie")
public class MovieRequestDTO {

    @NotBlank(message = "Title is required")
    @Schema(description = "Movie title", example = "Inception", required = true)
    private String title;

    @Schema(description = "Movie description (optional)", example = "A thief who steals corporate secrets...")
    private String description;

    @Min(value = 1888, message = "Year must be greater than 1888")
    @Schema(description = "Release year", example = "2010")
    private int movieYear;

    @Schema(description = "URL of the poster image", example = "https://example.com/inception.jpg")
    private String imageUrl;

    @Schema(description = "List of genre IDs to associate", example = "[1, 2, 3]")
    private List<Long> genreIds;
}