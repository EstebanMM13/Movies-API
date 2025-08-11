package com.estebanmmk13.movies.config;

import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.modelsRest.ReviewRest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {

    private final ModelMapper modelMapper;

    public ReviewMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        // Configuración para mapear user.id y movie.id
        this.modelMapper.typeMap(Review.class, ReviewRest.class).addMappings(mapper -> {
            mapper.map(review -> review.getUser().getId(), ReviewRest::setUserId);
            mapper.map(review -> review.getMovie().getId(), ReviewRest::setMovieId);
        });
    }

    public ReviewRest toRest(Review review) {
        return modelMapper.map(review, ReviewRest.class);
    }

    public List<ReviewRest> toRestList(List<Review> reviews) {
        return reviews.stream()
                .map(this::toRest)
                .toList();
    }

    public Review toEntity(ReviewRest dto, User user, Movie movie) {
        return Review.builder()
                .id(dto.getId())
                .comment(dto.getComment())
                .user(user)
                .movie(movie)
                .createdAt(dto.getCreatedAt())
                .build();
    }

}

