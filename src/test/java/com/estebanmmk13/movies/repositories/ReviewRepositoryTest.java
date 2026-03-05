package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Pageable pageable;
    private User user1;
    private User user2;
    private Movie movie1;
    private Movie movie2;
    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        // Crear usuarios
        user1 = User.builder()
                .username("User1")
                .email("user1@example.com")
                .password("1234")
                .build();

        user2 = User.builder()
                .username("User2")
                .email("user2@example.com")
                .password("1234")
                .build();

        // Crear películas
        movie1 = Movie.builder()
                .title("Movie1")
                .description("Desc1")
                .movieYear(2001)
                .votes(0)
                .rating(0)
                .build();

        movie2 = Movie.builder()
                .title("Movie2")
                .description("Desc2")
                .movieYear(2002)
                .votes(0)
                .rating(0)
                .build();

        // Persistir usuarios y películas
        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(movie1);
        testEntityManager.persist(movie2);
        testEntityManager.flush();

        // Crear reviews
        review1 = Review.builder()
                .user(user1)
                .movie(movie1)
                .comment("Buenísima")
                .createdAt(LocalDateTime.now())
                .build();

        review2 = Review.builder()
                .user(user1)
                .movie(movie2)
                .comment("Regular")
                .createdAt(LocalDateTime.now())
                .build();

        review3 = Review.builder()
                .user(user2)
                .movie(movie1)
                .comment("Genial")
                .createdAt(LocalDateTime.now())
                .build();

        testEntityManager.persist(review1);
        testEntityManager.persist(review2);
        testEntityManager.persist(review3);
        testEntityManager.flush();
    }

    @Test
    @DisplayName("Should find reviews by movie ID with pagination")
    void shouldFindReviewsByMovieId() {
        // When
        Page<Review> result = reviewRepository.findReviewsByMovieId(movie1.getId(), pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Review::getComment)
                .containsExactlyInAnyOrder("Buenísima", "Genial");

        // Verificar que todas las reviews son de la película correcta
        assertThat(result.getContent())
                .allMatch(review -> review.getMovie().getId().equals(movie1.getId()));
    }

    @Test
    @DisplayName("Should find reviews by user ID with pagination")
    void shouldFindReviewsByUserId() {
        // When
        Page<Review> result = reviewRepository.findReviewsByUserId(user1.getId(), pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Review::getComment)
                .containsExactlyInAnyOrder("Buenísima", "Regular");

        // Verificar que todas las reviews son del usuario correcto
        assertThat(result.getContent())
                .allMatch(review -> review.getUser().getId().equals(user1.getId()));
    }

    @Test
    @DisplayName("Should return empty page if no reviews for movie")
    void shouldReturnEmptyPageIfNoReviewsForMovie() {
        // Given
        Movie movieNoReviews = Movie.builder()
                .title("No Reviews")
                .description("Nada")
                .movieYear(2020)
                .votes(0)
                .rating(0)
                .build();
        testEntityManager.persist(movieNoReviews);
        testEntityManager.flush();

        // When
        Page<Review> result = reviewRepository.findReviewsByMovieId(movieNoReviews.getId(), pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Should return empty page if no reviews for user")
    void shouldReturnEmptyPageIfNoReviewsForUser() {
        // Given
        User userNoReviews = User.builder()
                .username("Sin Reviews")
                .email("no@reviews.com")
                .password("1234")
                .build();
        testEntityManager.persist(userNoReviews);
        testEntityManager.flush();

        // When
        Page<Review> result = reviewRepository.findReviewsByUserId(userNoReviews.getId(), pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Should save review correctly")
    void shouldSaveReviewCorrectly() {
        // Given
        Review newReview = Review.builder()
                .user(user1)
                .movie(movie2)
                .comment("Muy buena")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        Review saved = reviewRepository.save(newReview);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getComment()).isEqualTo("Muy buena");
        assertThat(saved.getUser().getId()).isEqualTo(user1.getId());
        assertThat(saved.getMovie().getId()).isEqualTo(movie2.getId());

        // Verificar que se guardó en BD
        Review found = testEntityManager.find(Review.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getComment()).isEqualTo("Muy buena");
    }

    @Test
    @DisplayName("Should check if review exists by user and movie")
    void existsByUserIdAndMovieId_ShouldReturnCorrectBoolean() {
        // When - review existente
        boolean existsExisting = reviewRepository.existsByUserIdAndMovieId(
                user1.getId(), movie1.getId());

        // When - review no existente
        boolean existsNonExisting = reviewRepository.existsByUserIdAndMovieId(
                user2.getId(), movie2.getId());

        // Then
        assertThat(existsExisting).isTrue();
        assertThat(existsNonExisting).isFalse();
    }

    @Test
    @DisplayName("Should handle pagination correctly with multiple pages")
    void shouldHandlePagination() {
        // Given - contar reviews existentes de movie1
        long existingReviews = reviewRepository.findReviewsByMovieId(movie1.getId(), Pageable.unpaged())
                .getTotalElements();
        assertThat(existingReviews).isEqualTo(2); // review1 y review3

        // Crear exactamente 15 reviews nuevas
        int newReviewsCount = 15;
        for (int i = 0; i < newReviewsCount; i++) {
            Review review = Review.builder()
                    .user(user1)
                    .movie(movie1)
                    .comment("Review " + i)
                    .createdAt(LocalDateTime.now())
                    .build();
            testEntityManager.persist(review);
        }
        testEntityManager.flush();

        Pageable firstPage = PageRequest.of(0, 10);
        Pageable secondPage = PageRequest.of(1, 10);

        // When
        Page<Review> page1 = reviewRepository.findReviewsByMovieId(movie1.getId(), firstPage);
        Page<Review> page2 = reviewRepository.findReviewsByMovieId(movie1.getId(), secondPage);

        // Then
        int totalReviewsExpected = (int) existingReviews + newReviewsCount; // 2 + 15 = 17

        assertThat(page1.getContent()).hasSize(10);
        assertThat(page2.getContent()).hasSize(totalReviewsExpected - 10); // 17 - 10 = 7
        assertThat(page1.getTotalElements()).isEqualTo(totalReviewsExpected);
        assertThat(page1.getTotalPages()).isEqualTo(2); // 17/10 = 1.7 → 2 páginas
    }

    @Test
    @DisplayName("Should find reviews ordered by date")
    void shouldFindReviewsOrderedByDate() {
        // Given - crear reviews con diferentes fechas
        LocalDateTime now = LocalDateTime.now();

        Review older = Review.builder()
                .user(user1)
                .movie(movie1)
                .comment("Más antigua")
                .createdAt(now.minusDays(10))
                .build();

        Review newer = Review.builder()
                .user(user1)
                .movie(movie1)
                .comment("Más nueva")
                .createdAt(now)
                .build();

        testEntityManager.persist(older);
        testEntityManager.persist(newer);
        testEntityManager.flush();

        // When - usamos un Pageable con ordenamiento por fecha descendente
        Pageable sortedByDateDesc = PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by("createdAt").descending());

        Page<Review> result = reviewRepository.findReviewsByMovieId(movie1.getId(), sortedByDateDesc);

        // Then
        assertThat(result.getContent()).isNotEmpty();
        // La primera debería ser la más nueva
        assertThat(result.getContent().get(0).getComment()).isEqualTo("Más nueva");
    }

    @Test
    @DisplayName("Should delete review correctly")
    void shouldDeleteReview() {
        // Given
        Review reviewToDelete = Review.builder()
                .user(user1)
                .movie(movie1)
                .comment("Para borrar")
                .createdAt(LocalDateTime.now())
                .build();
        Review saved = testEntityManager.persist(reviewToDelete);
        testEntityManager.flush();

        // When
        reviewRepository.deleteById(saved.getId());
        testEntityManager.flush();

        // Then
        Review deleted = testEntityManager.find(Review.class, saved.getId());
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should update review correctly")
    void shouldUpdateReview() {
        // Given
        Review reviewToUpdate = review1; // La review "Buenísima"

        // When
        reviewToUpdate.setComment("Actualizada: Excelente película");
        Review updated = reviewRepository.save(reviewToUpdate);
        testEntityManager.flush();

        // Then
        Review found = testEntityManager.find(Review.class, updated.getId());
        assertThat(found.getComment()).isEqualTo("Actualizada: Excelente película");
    }
}