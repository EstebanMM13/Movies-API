package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.models.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class VoteRepositoryTest {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Movie movie;
    private User user;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .title("Peli test")
                .description("Descripción test")
                .movieYear(2020)
                .votes(0)
                .rating(0)
                .build();

        user = User.builder()
                .username("usuario1")
                .email("usuario1@test.com")
                .password("123456")
                .build();

        testEntityManager.persist(movie);
        testEntityManager.persist(user);
    }

    @Test
    void existsByUserAndMovieReturnsTrueIfVoteExists() {
        Vote vote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(5.0)
                .votedAt(LocalDateTime.now())
                .build();

        testEntityManager.persist(vote);

        boolean exists = voteRepository.existsByUserAndMovie(user, movie);
        assertTrue(exists);
    }

    @Test
    void existsByUserAndMovieReturnsFalseIfVoteDoesNotExist() {
        boolean exists = voteRepository.existsByUserAndMovie(user, movie);
        assertFalse(exists);
    }

    @Test
    void saveAndFindById() {
        Vote vote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(4.0)
                .votedAt(LocalDateTime.now())
                .build();

        Vote saved = voteRepository.save(vote);
        assertNotNull(saved.getId());

        Optional<Vote> found = voteRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(4, found.get().getRating());
    }

    @Test
    void deleteByIdRemovesVote() {
        Vote vote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(3.0)
                .votedAt(LocalDateTime.now())
                .build();

        Vote saved = testEntityManager.persist(vote);

        voteRepository.deleteById(saved.getId());

        Optional<Vote> deleted = voteRepository.findById(saved.getId());
        assertTrue(deleted.isEmpty());
    }
}


