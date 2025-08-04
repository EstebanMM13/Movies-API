package com.estebanmmk13.movies.error.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewUpdateDTO {
    @NotBlank
    private String comment;

    @NotNull
    private Long userId;
}

