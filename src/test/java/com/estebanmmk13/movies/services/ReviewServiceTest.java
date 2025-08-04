package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.ReviewNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.repositories.MovieRepository;
import com.estebanmmk13.movies.repositories.ReviewRepository;
import com.estebanmmk13.movies.repositories.UserRepository;
import com.estebanmmk13.movies.services.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private MovieRepository movieRepository;

    private User user;
    private Movie movie;
    private Review review;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("Esteban")
                .email("esteban@example.com")
                .password("1234")
                .build();

        movie = Movie.builder()
                .id(1L)
                .title("Peli épica")
                .description("Una peli potente")
                .movieYear(2020)
                .votes(0)
                .rating(0)
                .build();

        review = Review.builder()
                .id(1L)
                .user(user)
                .movie(movie)
                .comment("Muy buena")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debería crear una review correctamente")
    void createReview() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        Mockito.when(reviewRepository.save(Mockito.any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.createReview(1L, 1L, "Muy buena");

        assertEquals("Muy buena", result.getComment());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(movie.getId(), result.getMovie().getId());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe al crear review")
    void createReviewUserNotFound() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.createReview(99L, 1L, "Comentario"));
    }

    @Test
    @DisplayName("Debería lanzar excepción si la película no existe al crear review")
    void createReviewMovieNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> reviewService.createReview(1L, 99L, "Comentario"));
    }

    @Test
    @DisplayName("Debería devolver reviews por ID de película")
    void findReviewsByMovieId() {
        List<Review> reviews = List.of(review);
        Mockito.when(reviewRepository.findReviewsByMovieId(1L)).thenReturn(reviews);

        List<Review> result = reviewService.findReviewsByMovieId(1L);

        assertEquals(1, result.size());
        assertEquals("Muy buena", result.get(0).getComment());
    }

    @Test
    @DisplayName("Debería devolver reviews por ID de usuario")
    void findReviewsByUserId() {
        List<Review> reviews = List.of(review);
        Mockito.when(reviewRepository.findReviewsByUserId(1L)).thenReturn(reviews);

        List<Review> result = reviewService.findReviewsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("Muy buena", result.get(0).getComment());
    }

    @Test
    @DisplayName("Debería lanzar excepción si no se encuentra la review por ID")
    void findReviewByIdNotFound() {
        Mockito.when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.findReviewById(99L));
    }

    @Test
    @DisplayName("Debería eliminar una review si pertenece al usuario")
    void deleteReview() {
        Mockito.when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L, 1L);

        Mockito.verify(reviewRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción si la review no pertenece al usuario")
    void deleteReviewNotOwned() {
        Review anotherUserReview = Review.builder()
                .id(2L)
                .user(User.builder().id(2L).username("Otro").build())
                .movie(movie)
                .comment("Ajena")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(reviewRepository.findById(2L)).thenReturn(Optional.of(anotherUserReview));

        assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(2L, 1L));
    }

    @Test
    @DisplayName("Debería actualizar el comentario de una review si pertenece al usuario")
    void updateReview() {
        Mockito.when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        Mockito.when(reviewRepository.save(Mockito.any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String newComment = "Comentario actualizado";

        Review result = reviewService.updateReview(1L, 1L, newComment);

        assertEquals(newComment, result.getComment());
        assertEquals(1L, result.getUser().getId());
        assertEquals(1L, result.getId());
    }
}

