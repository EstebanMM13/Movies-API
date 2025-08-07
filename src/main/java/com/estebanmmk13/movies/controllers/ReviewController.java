package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.error.dto.ReviewUpdateDTO;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.models.modelsDTO.ReviewResponseDto;
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
    public ResponseEntity<List<ReviewResponseDto>> findReviewsByMovie(@PathVariable Long movieId) {
        List<Review> reviews = reviewService.findReviewsByMovieId(movieId);
        List<ReviewResponseDto> response = reviews.stream()
                .map(review -> new ReviewResponseDto(
                        review.getId(),
                        review.getComment(),
                        review.getUser().getId()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> findReviewById(@PathVariable Long id){
        Review review = reviewService.findReviewById(id);
        ReviewResponseDto dto = new ReviewResponseDto(
                review.getId(),
                review.getComment(),
                review.getUser().getId()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable Long movieId,
                                               @RequestBody ReviewResponseDto dtoReview) {
        Review review = reviewService.createReview(dtoReview.getUserId(), movieId, dtoReview.getComment());
        ReviewResponseDto dtoCreated = new ReviewResponseDto(
                review.getId(),
                review.getComment(),
                review.getUser().getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dtoCreated);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long id,
                                               @RequestBody @Valid ReviewUpdateDTO dto) {
        Review updatedReview = reviewService.updateReview(id, dto.getUserId(), dto.getComment());
        ReviewResponseDto dtoUpdated = new ReviewResponseDto(
                updatedReview.getId(),
                updatedReview.getComment(),
                updatedReview.getUser().getId()
        );
        return ResponseEntity.ok(dtoUpdated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long reviewId,
                                             @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}
