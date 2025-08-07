package com.estebanmmk13.movies.models.modelsDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResponseDto {

    private Long id;

    @NotBlank
    private String comment;

    @NotNull
    private Long userId;
}