package com.estebanmmk13.movies.dtoModels.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class MovieRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Min(value = 1888, message = "Year must be greater than 1888")
    private int movieYear;

    private String imageUrl;

    private List<Long> genreIds;   // IDs de géneros existentes para asociar
}