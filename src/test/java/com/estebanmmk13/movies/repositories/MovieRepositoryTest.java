package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.models.Movie;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Movie testMovie;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        testMovie = Movie.builder()
                .title("Peli jiji")
                .description("Esta es la descripcion jiji")
                .movieYear(2000)
                .votes(0)
                .rating(0.0)
                .build();

        testEntityManager.persist(testMovie);
        testEntityManager.flush();
    }

    @Test
    @DisplayName("Should find movie by ID successfully")
    void shouldFindByIdSuccessfully() {
        // Given
        Movie movie = Movie.builder()
                .title("Peli ID")
                .description("Desc")
                .movieYear(2005)
                .votes(1)
                .rating(4.5)
                .build();

        Movie saved = testEntityManager.persist(movie);
        testEntityManager.flush();

        // When
        Optional<Movie> found = movieRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Peli ID");
    }

    @Test
    @DisplayName("Should find movie by title containing (case sensitive)")
    void findMovieByTitleContaining_Found() {
        // When
        Page<Movie> result = movieRepository.findMovieByTitleContaining("jiji", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Peli jiji");
    }

    @Test
    @DisplayName("Should return empty page when title not found")
    void findMovieByTitleContaining_NotFound() {
        // When
        Page<Movie> result = movieRepository.findMovieByTitleContaining("asfdasdf", pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Should save movie successfully")
    void shouldSaveMovieSuccessfully() {
        // Given
        Movie newMovie = Movie.builder()
                .title("Nueva peli")
                .description("Otra descripcion")
                .movieYear(2022)
                .votes(0)
                .rating(0.0)
                .build();

        // When
        Movie savedMovie = movieRepository.save(newMovie);

        // Then
        assertThat(savedMovie.getId()).isNotNull();
        assertThat(savedMovie.getTitle()).isEqualTo("Nueva peli");

        // Verify it was actually saved
        Optional<Movie> found = movieRepository.findById(savedMovie.getId());
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Should delete movie successfully")
    void shouldDeleteMovieSuccessfully() {
        // Given
        Movie movie = Movie.builder()
                .title("Borrar esta")
                .description("Delete desc")
                .movieYear(2010)
                .votes(0)
                .rating(0.0)
                .build();

        Movie saved = testEntityManager.persist(movie);
        testEntityManager.flush();

        // When
        movieRepository.deleteById(saved.getId());

        // Then
        Optional<Movie> deleted = movieRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should find all movies by genre name (case insensitive)")
    void shouldFindAllMoviesByGenreName() {
        // Given
        Genre action = Genre.builder().name("Action").build();
        Genre comedy = Genre.builder().name("Comedy").build();

        testEntityManager.persist(action);
        testEntityManager.persist(comedy);
        testEntityManager.flush();

        Movie movie1 = Movie.builder()
                .title("Action Movie 1")
                .description("Explosions and stuff")
                .movieYear(2010)
                .votes(0)
                .rating(0.0)
                .genres(List.of(action))
                .build();

        Movie movie2 = Movie.builder()
                .title("Action Comedy Movie")
                .description("Funny and explosive")
                .movieYear(2015)
                .votes(0)
                .rating(0.0)
                .genres(List.of(action, comedy))
                .build();

        Movie movie3 = Movie.builder()
                .title("Romantic Movie")
                .description("No action here")
                .movieYear(2018)
                .votes(0)
                .rating(0.0)
                .genres(List.of())
                .build();

        testEntityManager.persist(movie1);
        testEntityManager.persist(movie2);
        testEntityManager.persist(movie3);
        testEntityManager.flush();

        // When
        Page<Movie> actionMovies = movieRepository.findAllByGenreName("Action", pageable);

        // Then
        assertThat(actionMovies.getContent()).hasSize(2);
        assertThat(actionMovies.getContent())
                .extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Action Movie 1", "Action Comedy Movie");
    }

    @Test
    @DisplayName("Should return empty page when genre not found")
    void shouldReturnEmptyWhenGenreNotFound() {
        // Given
        Genre action = Genre.builder().name("Action").build();
        testEntityManager.persist(action);
        testEntityManager.flush();

        Movie movie = Movie.builder()
                .title("Action Movie")
                .description("Desc")
                .movieYear(2010)
                .votes(0)
                .rating(0.0)
                .genres(List.of(action))
                .build();
        testEntityManager.persist(movie);
        testEntityManager.flush();

        // When
        Page<Movie> result = movieRepository.findAllByGenreName("NonExistentGenre", pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Should return paginated results correctly")
    void shouldReturnPaginatedResults() {
        // Given
        for (int i = 0; i < 15; i++) {
            Movie movie = Movie.builder()
                    .title("Movie " + i)
                    .description("Description " + i)
                    .movieYear(2000 + i)
                    .votes(0)
                    .rating(0.0)
                    .build();
            testEntityManager.persist(movie);
        }
        testEntityManager.flush();

        Pageable firstPage = PageRequest.of(0, 10);
        Pageable secondPage = PageRequest.of(1, 10);

        // When
        Page<Movie> page1 = movieRepository.findAll(firstPage);
        Page<Movie> page2 = movieRepository.findAll(secondPage);

        // Then
        assertThat(page1.getContent()).hasSize(10);
        assertThat(page2.getContent()).hasSize(6); // 16 total - 10 = 6
        assertThat(page1.getTotalElements()).isEqualTo(16); // 15 + 1 from setUp
        assertThat(page1.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should update movie successfully")
    void shouldUpdateMovie() {
        // Given
        Movie savedMovie = testEntityManager.persist(testMovie);
        testEntityManager.flush();

        // When
        savedMovie.setTitle("Título Actualizado");
        savedMovie.setDescription("Descripción actualizada");
        Movie updatedMovie = movieRepository.save(savedMovie);

        // Then
        Movie found = movieRepository.findById(updatedMovie.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo("Título Actualizado");
        assertThat(found.getDescription()).isEqualTo("Descripción actualizada");
    }
}