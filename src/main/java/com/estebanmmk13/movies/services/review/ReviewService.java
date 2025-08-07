package com.estebanmmk13.movies.services.review;


import com.estebanmmk13.movies.models.Review;

import java.util.List;

public interface ReviewService {

    List<Review> findAllReviews();

    Review findReviewById(Long id);

    Review createReview(Long userId, Long movieId, String comment);

    Review updateReview(Long id, Long userId, String comment);

    void deleteReview(Long reviewId,Long userId);

    List<Review> findReviewsByMovieId(Long movieId);

    List<Review> findReviewsByUserId(Long userId);

}
