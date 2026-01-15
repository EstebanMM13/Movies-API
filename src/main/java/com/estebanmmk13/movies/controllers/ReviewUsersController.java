package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.config.ReviewMapper;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.modelsRest.ReviewRest;
import com.estebanmmk13.movies.services.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/{userId}")
@CrossOrigin
public class ReviewUsersController{

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewMapper reviewMapper;

    @GetMapping
    public ResponseEntity<Page<ReviewRest>> findReviewsByUser(@PathVariable Long userId, Pageable pageable) {
        Page<Review> reviewsPage = reviewService.findReviewsByUserId(userId, pageable);
        Page<ReviewRest> reviewsRestPage = reviewsPage.map(reviewMapper::toRest);
        return ResponseEntity.ok(reviewsRestPage);
    }
    
}
