package com.estebanmmk13.movies.services.review;

import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.ReviewNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.repositories.MovieRepository;
import com.estebanmmk13.movies.repositories.ReviewRepository;
import com.estebanmmk13.movies.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import static com.estebanmmk13.movies.error.notFound.ReviewNotFoundException.NOT_ACCES;
import static com.estebanmmk13.movies.error.notFound.ReviewNotFoundException.NOT_FOUND_BY_ID;

@Service
public class ReviewServiceImpl implements ReviewService{

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MovieRepository movieRepository;

    @Override
    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(String.format(NOT_FOUND_BY_ID,id)));
    }

    @Override
    public Review createReview(Long userId, Long movieId, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(NOT_FOUND_BY_ID,userId)));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(String.format(NOT_FOUND_BY_ID,movieId)));

        Review review = Review.builder()
                .user(user)
                .movie(movie)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long id, Long userId, String comment) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(String.format(NOT_FOUND_BY_ID, id)));

        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewNotFoundException(NOT_ACCES);
        }

        review.setComment(comment);
        return reviewRepository.save(review);
    }


    @Override
    public void deleteReview(Long reviewId,Long userId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(String.format(NOT_FOUND_BY_ID,reviewId)));

        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewNotFoundException(NOT_ACCES);
        }

        reviewRepository.deleteById(reviewId);
    }

    @Override
    public List<Review> findReviewsByMovieId(Long movieId) {
        return reviewRepository.findReviewsByMovieId(movieId);
    }

    @Override
    public List<Review> findReviewsByUserId(Long userId) {
        return reviewRepository.findReviewsByUserId(userId);
    }
}
