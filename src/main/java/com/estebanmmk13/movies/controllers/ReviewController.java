package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.config.ReviewMapper;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.modelsRest.ReviewRest;
import com.estebanmmk13.movies.services.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/{movieId}/reviews")
@CrossOrigin
@Tag(name = "Reviews", description = "Operations related to movie reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewMapper reviewMapper;

    @Operation(
            summary = "Get reviews by movie",
            description = "Retrieve a paginated list of reviews for a specific movie"
    )
    @GetMapping
    public ResponseEntity<Page<ReviewRest>> findReviewsByMovie(
            @Parameter(description = "ID of the movie") @PathVariable Long movieId,
            @Parameter(description = "Pagination information") Pageable pageable) {

        Page<Review> reviewsPage = reviewService.findReviewsByMovieId(movieId, pageable);
        Page<ReviewRest> reviewsRestPage = reviewsPage.map(reviewMapper::toRest);
        return ResponseEntity.ok(reviewsRestPage);
    }

    @Operation(summary = "Get review by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReviewRest> findReviewById(
            @Parameter(description = "ID of the review to retrieve") @PathVariable Long id) {

        Review review = reviewService.findReviewById(id);
        return ResponseEntity.ok(reviewMapper.toRest(review));
    }

    @Operation(
            summary = "Create a review for a movie",
            description = "Creates a new review for the specified movie"
    )
    @PostMapping
    public ResponseEntity<ReviewRest> createReview(
            @Parameter(description = "ID of the movie") @PathVariable Long movieId,
            @Parameter(description = "Review data (userId and comment)")
            @RequestBody ReviewRest dtoReview) {

        Review review = reviewService.createReview(
                dtoReview.getUserId(),
                movieId,
                dtoReview.getComment()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toRest(review));
    }

    @Operation(
            summary = "Update an existing review",
            description = "Updates the comment of an existing review"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewRest> updateReview(
            @Parameter(description = "ID of the review to update") @PathVariable Long id,
            @Parameter(description = "Updated review data (userId and comment)")
            @Valid @RequestBody ReviewRest dto) {

        Review updatedReview = reviewService.updateReview(
                id,
                dto.getUserId(),
                dto.getComment()
        );
        return ResponseEntity.ok(reviewMapper.toRest(updatedReview));
    }

    @Operation(
            summary = "Delete a review",
            description = "Deletes a review by ID (requires userId as request parameter)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID of the review to delete") @PathVariable("id") Long reviewId,
            @Parameter(description = "ID of the user who owns the review") @RequestParam Long userId) {

        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}