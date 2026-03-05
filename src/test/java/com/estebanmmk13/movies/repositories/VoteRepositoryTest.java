package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.models.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    private Vote vote;

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
        testEntityManager.flush();
    }

    @Test
    @DisplayName("Debería devolver true si existe un voto del usuario para la película")
    void existsByUserAndMovie_WhenVoteExists_ShouldReturnTrue() {
        // Given
        Vote vote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(5.0)
                .votedAt(LocalDateTime.now())
                .build();

        testEntityManager.persist(vote);
        testEntityManager.flush();

        // When
        boolean exists = voteRepository.existsByUserAndMovie(user, movie);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debería devolver false si no existe un voto del usuario para la película")
    void existsByUserAndMovie_WhenVoteDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = voteRepository.existsByUserAndMovie(user, movie);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debería distinguir entre diferentes combinaciones usuario-película")
    void existsByUserAndMovie_ShouldDistinguishDifferentCombinations() {
        // Given
        Movie otraPeli = Movie.builder()
                .title("Otra peli")
                .description("Otra desc")
                .movieYear(2021)
                .votes(0)
                .rating(0)
                .build();

        User otroUser = User.builder()
                .username("usuario2")
                .email("usuario2@test.com")
                .password("123456")
                .build();

        testEntityManager.persist(otraPeli);
        testEntityManager.persist(otroUser);

        Vote vote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(5.0)
                .votedAt(LocalDateTime.now())
                .build();

        testEntityManager.persist(vote);
        testEntityManager.flush();

        // When & Then
        assertThat(voteRepository.existsByUserAndMovie(user, movie)).isTrue();
        assertThat(voteRepository.existsByUserAndMovie(user, otraPeli)).isFalse();
        assertThat(voteRepository.existsByUserAndMovie(otroUser, movie)).isFalse();
        assertThat(voteRepository.existsByUserAndMovie(otroUser, otraPeli)).isFalse();
    }

    @Test
    @DisplayName("Debería guardar un voto correctamente")
    void save_ShouldPersistVote() {
        // Given
        Vote newVote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(4.0)
                .votedAt(LocalDateTime.now())
                .build();

        // When
        Vote saved = voteRepository.save(newVote);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRating()).isEqualTo(4.0);
        assertThat(saved.getMovie().getId()).isEqualTo(movie.getId());
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());

        // Verificar en BD
        Vote found = testEntityManager.find(Vote.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getRating()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Debería encontrar un voto por ID")
    void findById_ShouldReturnVote_WhenExists() {
        // Given
        Vote vote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(4.5)
                .votedAt(LocalDateTime.now())
                .build();

        Vote saved = testEntityManager.persist(vote);
        testEntityManager.flush();

        // When
        Optional<Vote> found = voteRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRating()).isEqualTo(4.5);
        assertThat(found.get().getMovie().getTitle()).isEqualTo("Peli test");
        assertThat(found.get().getUser().getUsername()).isEqualTo("usuario1");
    }

    @Test
    @DisplayName("Debería devolver vacío al buscar voto por ID inexistente")
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // When
        Optional<Vote> found = voteRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debería eliminar un voto por ID")
    void deleteById_ShouldRemoveVote() {
        // Given
        Vote vote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(3.0)
                .votedAt(LocalDateTime.now())
                .build();

        Vote saved = testEntityManager.persist(vote);
        testEntityManager.flush();

        // When
        voteRepository.deleteById(saved.getId());
        testEntityManager.flush();

        // Then
        Optional<Vote> deleted = voteRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();

        // Verificar que el usuario y la película siguen existiendo
        assertThat(testEntityManager.find(User.class, user.getId())).isNotNull();
        assertThat(testEntityManager.find(Movie.class, movie.getId())).isNotNull();
    }

    @Test
    @DisplayName("No debería afectar a otros votos al eliminar uno")
    void deleteById_ShouldNotAffectOtherVotes() {
        // Given
        Vote vote1 = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(3.0)
                .votedAt(LocalDateTime.now())
                .build();

        User otroUser = User.builder()
                .username("usuario3")
                .email("usuario3@test.com")
                .password("123456")
                .build();
        testEntityManager.persist(otroUser);

        Vote vote2 = Vote.builder()
                .movie(movie)
                .user(otroUser)
                .rating(4.0)
                .votedAt(LocalDateTime.now())
                .build();

        testEntityManager.persist(vote1);
        Vote savedVote2 = testEntityManager.persist(vote2);
        testEntityManager.flush();

        // When
        voteRepository.deleteById(vote1.getId());
        testEntityManager.flush();

        // Then
        Optional<Vote> deleted = voteRepository.findById(vote1.getId());
        assertThat(deleted).isEmpty();

        Optional<Vote> stillExists = voteRepository.findById(savedVote2.getId());
        assertThat(stillExists).isPresent();
        assertThat(stillExists.get().getRating()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Debería encontrar todos los votos de una película")
    void shouldFindAllVotesForMovie() {
        // Given
        User user2 = User.builder()
                .username("usuario2")
                .email("user2@test.com")
                .password("123456")
                .build();
        testEntityManager.persist(user2);

        Vote vote1 = Vote.builder().movie(movie).user(user).rating(5.0).votedAt(LocalDateTime.now()).build();
        Vote vote2 = Vote.builder().movie(movie).user(user2).rating(4.0).votedAt(LocalDateTime.now()).build();

        testEntityManager.persist(vote1);
        testEntityManager.persist(vote2);
        testEntityManager.flush();

        // When - usando JPQL personalizado (si existiera)
        // Por ahora, como no tenemos método, buscamos todos y filtramos
        java.util.List<Vote> allVotes = voteRepository.findAll();
        long votesForMovie = allVotes.stream()
                .filter(v -> v.getMovie().getId().equals(movie.getId()))
                .count();

        // Then
        assertThat(votesForMovie).isEqualTo(2);
    }

    @Test
    @DisplayName("Debería lanzar excepción al intentar votar dos veces la misma película")
    void shouldEnforceUniqueVotePerUserAndMovie() {
        // Given
        Vote vote1 = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(5.0)
                .votedAt(LocalDateTime.now())
                .build();

        voteRepository.save(vote1);
        testEntityManager.flush();

        // When - intentar guardar otro voto del mismo usuario para la misma película
        Vote vote2 = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(3.0)
                .votedAt(LocalDateTime.now())
                .build();

        // Then - debería lanzar DataIntegrityViolationException
        org.springframework.dao.DataIntegrityViolationException exception =
                assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
                    voteRepository.save(vote2);
                    testEntityManager.flush();
                });

        // Verificar que el mensaje contiene información sobre la violación de unicidad
        // 🔹 CORREGIDO: Usamos los textos que aparecen en el mensaje real de H2
        assertThat(exception.getMessage())
                .contains("Violación de indice de Unicidad")
                .contains("UK_USER_MOVIE_VOTE"); // El nombre de la constraint en mayúsculas
    }

    @Test
    @DisplayName("Debería permitir votos de diferentes usuarios para la misma película")
    void shouldAllowDifferentUsersForSameMovie() {
        // Given
        User otroUser = User.builder()
                .username("usuario2")
                .email("user2@test.com")
                .password("123456")
                .build();
        testEntityManager.persist(otroUser);
        testEntityManager.flush();

        Vote vote1 = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(5.0)
                .votedAt(LocalDateTime.now())
                .build();

        Vote vote2 = Vote.builder()
                .movie(movie)
                .user(otroUser)
                .rating(4.0)
                .votedAt(LocalDateTime.now())
                .build();

        // When
        voteRepository.save(vote1);
        voteRepository.save(vote2);
        testEntityManager.flush();

        // Then
        long count = voteRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Debería permitir votos del mismo usuario para diferentes películas")
    void shouldAllowSameUserForDifferentMovies() {
        // Given
        Movie otraPeli = Movie.builder()
                .title("Otra peli")
                .description("Otra desc")
                .movieYear(2021)
                .votes(0)
                .rating(0)
                .build();
        testEntityManager.persist(otraPeli);
        testEntityManager.flush();

        Vote vote1 = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(5.0)
                .votedAt(LocalDateTime.now())
                .build();

        Vote vote2 = Vote.builder()
                .movie(otraPeli)
                .user(user)
                .rating(4.0)
                .votedAt(LocalDateTime.now())
                .build();

        // When
        voteRepository.save(vote1);
        voteRepository.save(vote2);
        testEntityManager.flush();

        // Then
        long count = voteRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Debería actualizar un voto existente")
    void shouldUpdateVote() {
        // Given
        Vote vote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(3.0)
                .votedAt(LocalDateTime.now())
                .build();

        Vote saved = testEntityManager.persist(vote);
        testEntityManager.flush();

        // When
        saved.setRating(5.0);
        Vote updated = voteRepository.save(saved);
        testEntityManager.flush();

        // Then
        Vote found = testEntityManager.find(Vote.class, updated.getId());
        assertThat(found.getRating()).isEqualTo(5.0);
    }
}