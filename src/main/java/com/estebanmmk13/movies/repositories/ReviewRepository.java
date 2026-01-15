package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    Page<Review> findReviewsByMovieId(Long movieId, Pageable pageable);
    Page<Review> findReviewsByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

}
