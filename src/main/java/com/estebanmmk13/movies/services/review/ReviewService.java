package com.estebanmmk13.movies.services.review;


import com.estebanmmk13.movies.dtoModels.request.ReviewRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.ReviewResponseDTO;
import com.estebanmmk13.movies.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    List<ReviewResponseDTO> findAllReviews();

    ReviewResponseDTO findReviewById(Long id);

    ReviewResponseDTO createReview(Long userId, Long movieId, ReviewRequestDTO dto);

    ReviewResponseDTO updateReview(Long id, Long userId, ReviewRequestDTO dto);

    void deleteReview(Long reviewId,Long userId);

    Page<ReviewResponseDTO> findReviewsByMovieId(Long movieId, Pageable pageable);

    Page<ReviewResponseDTO> findReviewsByUserId(Long userId, Pageable pageable);

}
