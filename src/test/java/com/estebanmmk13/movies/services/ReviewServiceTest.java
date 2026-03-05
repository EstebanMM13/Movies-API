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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private User otroUser;
    private Movie movie;
    private Review review;
    private Pageable pageable;
    private Page<Review> reviewPage;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("Esteban")
                .email("esteban@example.com")
                .password("1234")
                .build();

        otroUser = User.builder()
                .id(2L)
                .username("OtroUsuario")
                .email("otro@example.com")
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

        pageable = PageRequest.of(0, 10);
        reviewPage = new PageImpl<>(List.of(review), pageable, 1);
    }

    @Test
    @DisplayName("Debería crear una review correctamente")
    void createReview_ShouldCreateAndReturnReview() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(reviewRepository.existsByUserIdAndMovieId(1L, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Review result = reviewService.createReview(1L, 1L, "Muy buena");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getComment()).isEqualTo("Muy buena");
        assertThat(result.getUser().getId()).isEqualTo(user.getId());
        assertThat(result.getMovie().getId()).isEqualTo(movie.getId());

        verify(reviewRepository).save(argThat(review ->
                review.getComment().equals("Muy buena") &&
                        review.getUser().equals(user) &&
                        review.getMovie().equals(movie)
        ));
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario ya hizo una review de la película")
    void createReview_WhenDuplicate_ShouldThrowException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(reviewRepository.existsByUserIdAndMovieId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, "Muy buena"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already submitted a review");

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe al crear review")
    void createReview_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(99L, 1L, "Comentario"))
                .isInstanceOf(UserNotFoundException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si la película no existe al crear review")
    void createReview_WhenMovieNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(1L, 99L, "Comentario"))
                .isInstanceOf(MovieNotFoundException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería devolver todas las reviews")
    void findAllReviews_ShouldReturnListOfReviews() {
        // Given
        List<Review> reviews = List.of(review);
        when(reviewRepository.findAll()).thenReturn(reviews);

        // When
        List<Review> result = reviewService.findAllReviews();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getComment()).isEqualTo("Muy buena");
        verify(reviewRepository).findAll();
    }

    @Test
    @DisplayName("Debería devolver una review por ID cuando existe")
    void findReviewById_WhenExists_ShouldReturnReview() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // When
        Review result = reviewService.findReviewById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getComment()).isEqualTo("Muy buena");
        verify(reviewRepository).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción si no se encuentra la review por ID")
    void findReviewById_WhenNotExists_ShouldThrowException() {
        // Given
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.findReviewById(99L))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(reviewRepository).findById(99L);
    }

    @Test
    @DisplayName("Debería devolver reviews por ID de película paginadas")
    void findReviewsByMovieId_ShouldReturnPageOfReviews() {
        // Given
        when(reviewRepository.findReviewsByMovieId(eq(1L), any(Pageable.class)))
                .thenReturn(reviewPage);

        // When
        Page<Review> result = reviewService.findReviewsByMovieId(1L, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getComment()).isEqualTo("Muy buena");
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(reviewRepository).findReviewsByMovieId(1L, pageable);
    }

    @Test
    @DisplayName("Debería devolver página vacía cuando no hay reviews para la película")
    void findReviewsByMovieId_WhenNoReviews_ShouldReturnEmptyPage() {
        // Given
        Page<Review> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(reviewRepository.findReviewsByMovieId(eq(1L), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When
        Page<Review> result = reviewService.findReviewsByMovieId(1L, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Debería devolver reviews por ID de usuario paginadas")
    void findReviewsByUserId_ShouldReturnPageOfReviews() {
        // Given
        when(reviewRepository.findReviewsByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(reviewPage);

        // When
        Page<Review> result = reviewService.findReviewsByUserId(1L, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getComment()).isEqualTo("Muy buena");

        verify(reviewRepository).findReviewsByUserId(1L, pageable);
    }

    @Test
    @DisplayName("Debería actualizar el comentario de una review si pertenece al usuario")
    void updateReview_WhenOwnedByUser_ShouldUpdateComment() {
        // Given
        String newComment = "Comentario actualizado";

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Review result = reviewService.updateReview(1L, 1L, newComment);

        // Then
        assertThat(result.getComment()).isEqualTo(newComment);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUser().getId()).isEqualTo(1L);

        verify(reviewRepository).save(argThat(updatedReview ->
                updatedReview.getComment().equals(newComment)
        ));
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar review que no pertenece al usuario")
    void updateReview_WhenNotOwnedByUser_ShouldThrowException() {
        // Given
        String newComment = "Comentario actualizado";

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // When & Then
        assertThatThrownBy(() -> reviewService.updateReview(1L, 2L, newComment))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar review que no existe")
    void updateReview_WhenNotFound_ShouldThrowException() {
        // Given
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.updateReview(99L, 1L, "nuevo"))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar una review si pertenece al usuario")
    void deleteReview_WhenOwnedByUser_ShouldDelete() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // When
        reviewService.deleteReview(1L, 1L);

        // Then
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción si la review no pertenece al usuario")
    void deleteReview_WhenNotOwnedByUser_ShouldThrowException() {
        // Given
        Review anotherUserReview = Review.builder()
                .id(2L)
                .user(otroUser)
                .movie(movie)
                .comment("Ajena")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewRepository.findById(2L)).thenReturn(Optional.of(anotherUserReview));

        // When & Then
        assertThatThrownBy(() -> reviewService.deleteReview(2L, 1L))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar review que no existe")
    void deleteReview_WhenNotFound_ShouldThrowException() {
        // Given
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.deleteReview(99L, 1L))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debería manejar correctamente la paginación en búsquedas")
    void shouldHandlePaginationCorrectly() {
        // Given
        List<Review> manyReviews = List.of(
                review,
                Review.builder().id(2L).user(user).movie(movie).comment("Segunda").build(),
                Review.builder().id(3L).user(user).movie(movie).comment("Tercera").build()
        );
        Page<Review> pageWithThree = new PageImpl<>(manyReviews, pageable, 3);

        when(reviewRepository.findReviewsByMovieId(eq(1L), any(Pageable.class)))
                .thenReturn(pageWithThree);

        // When
        Page<Review> result = reviewService.findReviewsByMovieId(1L, pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debería respetar el ordenamiento en las búsquedas paginadas")
    void shouldRespectSortingInPagedQueries() {
        // Given
        Pageable sortedByDateDesc = PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by("createdAt").descending());

        when(reviewRepository.findReviewsByMovieId(eq(1L), any(Pageable.class)))
                .thenReturn(reviewPage);

        // When
        Page<Review> result = reviewService.findReviewsByMovieId(1L, sortedByDateDesc);

        // Then
        assertThat(result).isNotNull();
        verify(reviewRepository).findReviewsByMovieId(1L, sortedByDateDesc);
    }
}