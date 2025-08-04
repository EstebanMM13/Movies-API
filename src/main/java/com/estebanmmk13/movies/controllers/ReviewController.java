package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.error.dto.ReviewUpdateDTO;
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

    @GetMapping
    public ResponseEntity<List<Review>> findReviewsByMovie(@PathVariable Long movieId) {
        List<Review> reviews = reviewService.findReviewsByMovieId(movieId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> findReviewById(@PathVariable Long id){
        Review review = reviewService.findReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@PathVariable Long movieId,
                                               @RequestParam Long userId,
                                               @RequestBody String comment) {
        Review review = reviewService.createReview(userId, movieId, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id,
                                               @RequestBody @Valid ReviewUpdateDTO dto) {
        Review updatedReview = reviewService.updateReview(id, dto.getUserId(), dto.getComment());
        return ResponseEntity.ok(updatedReview);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long reviewId,
                                             @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }





}
