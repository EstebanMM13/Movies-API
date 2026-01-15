package com.estebanmmk13.movies.services.review;


import com.estebanmmk13.movies.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    List<Review> findAllReviews();

    Review findReviewById(Long id);

    Review createReview(Long userId, Long movieId, String comment);

    Review updateReview(Long id, Long userId, String comment);

    void deleteReview(Long reviewId,Long userId);

    Page<Review> findReviewsByMovieId(Long movieId, Pageable pageable);

    Page<Review> findReviewsByUserId(Long userId, Pageable pageable);

}
