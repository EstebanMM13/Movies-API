package com.estebanmmk13.movies.dtoModels.response;

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
public class ReviewResponseDTO {
    private Long id;
    private String comment;

    @JsonFormat(pattern = "dd-MM-yyyy 'at' HH:mm")
    private LocalDateTime createdAt;

    private String username;
    private String movieTitle;
}

