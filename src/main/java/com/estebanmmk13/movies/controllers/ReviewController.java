package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.config.ReviewMapper;
import com.estebanmmk13.movies.modelsRest.ReviewRest;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.services.review.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies/{movieId}/reviews")
@CrossOrigin
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewMapper reviewMapper;

    @GetMapping
    public ResponseEntity<List<ReviewRest>> findReviewsByMovie(@PathVariable Long movieId) {
        List<Review> reviews = reviewService.findReviewsByMovieId(movieId);
        return ResponseEntity.ok(reviewMapper.toRestList(reviews));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewRest> findReviewById(@PathVariable Long id){
        Review review = reviewService.findReviewById(id);
        return ResponseEntity.ok(reviewMapper.toRest(review));
    }

    @PostMapping
    public ResponseEntity<ReviewRest> createReview(@PathVariable Long movieId,
                                               @RequestBody ReviewRest dtoReview) {
        Review review = reviewService.createReview(dtoReview.getUserId(), movieId, dtoReview.getComment());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toRest(review));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewRest> updateReview(@PathVariable Long id,
                                               @RequestBody @Valid ReviewRest dto) {
        Review updatedReview = reviewService.updateReview(id, dto.getUserId(), dto.getComment());
        return ResponseEntity.ok(reviewMapper.toRest(updatedReview));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long reviewId,
                                             @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}
