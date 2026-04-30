package com.estebanmmk13.movies.services.review;

import com.estebanmmk13.movies.dtoModels.request.ReviewRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.ReviewResponseDTO;
import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.ReviewNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.mapper.ReviewMapper;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.repositories.MovieRepository;
import com.estebanmmk13.movies.repositories.ReviewRepository;
import com.estebanmmk13.movies.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.estebanmmk13.movies.error.notFound.ReviewNotFoundException.NOT_ACCES;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRepository userRepository,
                             MovieRepository movieRepository,
                             ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<ReviewResponseDTO> findAllReviews() {
        return reviewRepository.findAll()
                .stream().map(reviewMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDTO findReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(String.format(ReviewNotFoundException.NOT_FOUND_BY_ID, id)));
        return reviewMapper.toResponseDTO(review);
    }

    @Override
    public ReviewResponseDTO createReview(Long userId, Long movieId, ReviewRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(UserNotFoundException.NOT_FOUND_BY_ID, userId)));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(String.format(MovieNotFoundException.NOT_FOUND_BY_ID, movieId)));

        if (reviewRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new RuntimeException("User already submitted a review for this movie");
        }

        Review review = reviewMapper.toEntity(dto, user, movie);
        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponseDTO(saved);
    }

    @Override
    public ReviewResponseDTO updateReview(Long id, Long userId, ReviewRequestDTO dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(String.format(ReviewNotFoundException.NOT_FOUND_BY_ID, id)));

        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewNotFoundException(NOT_ACCES);
        }

        review.setComment(dto.getComment());
        Review updated = reviewRepository.save(review);
        return reviewMapper.toResponseDTO(updated);
    }


    @Override
    public void deleteReview(Long reviewId, Long userId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(String.format(ReviewNotFoundException.NOT_FOUND_BY_ID, reviewId)));

        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewNotFoundException(NOT_ACCES);
        }

        reviewRepository.deleteById(reviewId);
    }

    @Override
    public Page<ReviewResponseDTO> findReviewsByMovieId(Long movieId, Pageable pageable) {
        return reviewRepository.findReviewsByMovieId(movieId, pageable).map(reviewMapper::toResponseDTO);
    }

    @Override
    public Page<ReviewResponseDTO> findReviewsByUserId(Long userId, Pageable pageable) {
        return reviewRepository.findReviewsByUserId(userId, pageable).map(reviewMapper::toResponseDTO);
    }
}