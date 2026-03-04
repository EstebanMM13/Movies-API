package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.config.ReviewMapper;
import com.estebanmmk13.movies.error.dto.ResponseError;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.modelsRest.ReviewRest;
import com.estebanmmk13.movies.services.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Review User REST Controller
 * Handles retrieval of reviews made by a specific user.
 *
 * Endpoint: /api/reviews/{userId}
 *
 * @author Esteban
 */
@RestController
@RequestMapping("/api/reviews/{userId}")
@CrossOrigin
@Tag(name = "User Reviews", description = "User review retrieval endpoints")
public class ReviewUsersController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewUsersController(ReviewService reviewService,
                                 ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    // GET REVIEWS BY USER
    @Operation(
            summary = "Get reviews by user",
            description = "Retrieve a paginated list of all reviews made by a specific user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ReviewRest.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ReviewRest>> findReviewsByUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Pagination information")
            Pageable pageable) {

        Page<Review> reviewsPage = reviewService.findReviewsByUserId(userId, pageable);
        Page<ReviewRest> reviewsRestPage = reviewsPage.map(reviewMapper::toRest);

        return ResponseEntity.ok(reviewsRestPage);
    }
}