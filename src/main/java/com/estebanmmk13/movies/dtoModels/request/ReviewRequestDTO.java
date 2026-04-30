package com.estebanmmk13.movies.dtoModels.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequestDTO {
    @NotBlank(message = "Comment cannot be blank")
    private String comment;
}