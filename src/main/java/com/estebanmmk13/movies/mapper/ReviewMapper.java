package com.estebanmmk13.movies.mapper;

import com.estebanmmk13.movies.dtoModels.request.ReviewRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.ReviewResponseDTO;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.models.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReviewMapper {

    public ReviewResponseDTO toResponseDTO(Review review) {
        if (review == null) return null;
        return new ReviewResponseDTO(
                review.getId(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUser().getUsername(),
                review.getMovie().getTitle()
        );
    }

    public Review toEntity(ReviewRequestDTO dto, User user, Movie movie) {
        if (dto == null) return null;
        return Review.builder()
                .comment(dto.getComment())
                .user(user)
                .movie(movie)
                .createdAt(LocalDateTime.now())
                .build();
    }
}