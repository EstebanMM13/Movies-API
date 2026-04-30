package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.dtoModels.request.ReviewRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.ReviewResponseDTO;
import com.estebanmmk13.movies.error.dto.ResponseError;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.services.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/{movieId}/reviews")
@CrossOrigin
@Tag(name = "Reviews", description = "Movie review management endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // GET REVIEWS BY MOVIE
    @Operation(
            summary = "Find reviews by movie",
            description = "Retrieve a paginated list of reviews for a specific movie"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found",
                    content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewsByMovie(
            @Parameter(description = "Movie ID", required = true)
            @PathVariable Long movieId,
            @Parameter(description = "Pagination information")
            Pageable pageable) {

        return ResponseEntity.ok(reviewService.findReviewsByMovieId(movieId, pageable));
    }

    // GET REVIEW BY ID
    @Operation(summary = "Find review by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review found",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> findReviewById(
            @Parameter(description = "Review ID", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(reviewService.findReviewById(id));
    }

    // CREATE REVIEW
    @Operation(
            summary = "Create a review for a movie",
            description = "Creates a new review for the specified movie"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created successfully",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid review data",
                    content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "Movie or user not found",
                    content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewRequestDTO dto,
            @RequestParam Long userId) {   // ← ADDED
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(userId, movieId, dto));
    }

    // UPDATE REVIEW
    @Operation(
            summary = "Update an existing review",
            description = "Updates the comment of an existing review"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "400", description = "Invalid review data")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long movieId,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO dto,
            @RequestParam Long userId) {   // ← ADDED
        return ResponseEntity.ok(reviewService.updateReview(id, userId, dto));
    }


    // DELETE REVIEW
    @Operation(
            summary = "Delete a review",
            description = "Deletes a review by ID (requires userId as request parameter)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long movieId,
            @PathVariable Long id,
            @RequestParam Long userId) {   // ← ADDED
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }
}