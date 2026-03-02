package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.config.ReviewMapper;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.modelsRest.ReviewRest;
import com.estebanmmk13.movies.services.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/{userId}")
@CrossOrigin
@Tag(name = "Reviews", description = "Operations related to user reviews")
public class ReviewUsersController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewMapper reviewMapper;

    @Operation(
            summary = "Get reviews by user",
            description = "Retrieve a paginated list of all reviews made by a specific user"
    )
    @GetMapping
    public ResponseEntity<Page<ReviewRest>> findReviewsByUser(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "Pagination information") Pageable pageable) {

        Page<Review> reviewsPage = reviewService.findReviewsByUserId(userId, pageable);
        Page<ReviewRest> reviewsRestPage = reviewsPage.map(reviewMapper::toRest);
        return ResponseEntity.ok(reviewsRestPage);
    }
}