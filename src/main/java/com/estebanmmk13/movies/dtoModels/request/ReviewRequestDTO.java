package com.estebanmmk13.movies.dtoModels.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Data required to create or update a review")
public class ReviewRequestDTO {
    @NotBlank(message = "Comment cannot be blank")
    @Schema(description = "Review comment", example = "This movie is amazing!", required = true)
    private String comment;
}