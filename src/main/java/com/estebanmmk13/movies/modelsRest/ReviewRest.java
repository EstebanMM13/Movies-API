package com.estebanmmk13.movies.modelsRest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRest {
    private Long id;
    private String comment;
    private Long userId;
    private Long movieId;

    @JsonFormat(pattern = "dd-MM-yyyy 'at' HH:mm")
    private LocalDateTime createdAt;
}

