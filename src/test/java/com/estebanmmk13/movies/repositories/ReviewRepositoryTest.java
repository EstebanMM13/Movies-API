package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.models.User;
import org.junit.jupiter.api.BeforeEach;
import com.estebanmmk13.movies.models.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private User user1;
    private User user2;
    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    void setUp() {
        user1 = User.builder().username("User1").email("user1@example.com").password("1234").build();
        user2 = User.builder().username("User2").email("user2@example.com").password("1234").build();
        movie1 = Movie.builder().title("Movie1").description("Desc1").movieYear(2001).votes(0).rating(0).build();
        movie2 = Movie.builder().title("Movie2").description("Desc2").movieYear(2002).votes(0).rating(0).build();

        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(movie1);
        testEntityManager.persist(movie2);

        // Reviews
        Review r1 = Review.builder().user(user1).movie(movie1).comment("Buenísima").createdAt(LocalDateTime.now()).build();
        Review r2 = Review.builder().user(user1).movie(movie2).comment("Regular").createdAt(LocalDateTime.now()).build();
        Review r3 = Review.builder().user(user2).movie(movie1).comment("Genial").createdAt(LocalDateTime.now()).build();

        testEntityManager.persist(r1);
        testEntityManager.persist(r2);
        testEntityManager.persist(r3);
    }

    @Test
    void shouldFindReviewsByMovieId() {
        List<Review> reviews = reviewRepository.findReviewsByMovieId(movie1.getId());
        assertEquals(2, reviews.size());
        assertTrue(reviews.stream().allMatch(r -> r.getMovie().getId().equals(movie1.getId())));
    }

    @Test
    void shouldFindReviewsByUserId() {
        List<Review> reviews = reviewRepository.findReviewsByUserId(user1.getId());
        assertEquals(2, reviews.size());
        assertTrue(reviews.stream().allMatch(r -> r.getUser().getId().equals(user1.getId())));
    }

    @Test
    void shouldReturnEmptyListIfNoReviewsForMovie() {
        Movie movieNoReviews = Movie.builder().title("No Reviews").description("Nada").movieYear(2020).votes(0).rating(0).build();
        testEntityManager.persist(movieNoReviews);

        List<Review> reviews = reviewRepository.findReviewsByMovieId(movieNoReviews.getId());
        assertTrue(reviews.isEmpty());
    }

    @Test
    void shouldReturnEmptyListIfNoReviewsForUser() {
        User userNoReviews = User.builder().username("Sin Reviews").email("no@reviews.com").password("1234").build();
        testEntityManager.persist(userNoReviews);

        List<Review> reviews = reviewRepository.findReviewsByUserId(userNoReviews.getId());
        assertTrue(reviews.isEmpty());
    }

    @Test
    void shouldSaveReviewCorrectly() {
        Review newReview = Review.builder()
                .user(user1)
                .movie(movie1)
                .comment("Muy buena")
                .createdAt(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(newReview);

        assertNotNull(saved.getId());
        assertEquals("Muy buena", saved.getComment());
        assertEquals(user1.getId(), saved.getUser().getId());
        assertEquals(movie1.getId(), saved.getMovie().getId());
    }
}
