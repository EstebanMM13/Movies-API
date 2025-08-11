package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MovieRepositoryTest {

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    TestEntityManager testEntityManager;

    //Se ejecuta antes de las pruebas unitarias
    @BeforeEach
    void setUp() {
        Movie movie = Movie.builder()
                .title("Peli jiji")
                .description("Esta es la descripcion jiji")
                .movieYear(2000)
                .votes(0)
                .rating(0)
                .build();

        testEntityManager.persist(movie);
    }

    @Test
    public void shouldFindByIdSuccessfully() {
        Movie movie = Movie.builder()
                .title("Peli ID")
                .description("Desc")
                .movieYear(2005)
                .votes(1)
                .rating(4.5)
                .build();

        Movie saved = testEntityManager.persist(movie);

        Optional<Movie> found = movieRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Peli ID", found.get().getTitle());
    }

    @Test
    public void findMovieByTitleIgnoreCaseFound(){
        List<Movie> optionalMovie = movieRepository.findMovieByTitleIgnoreCaseContaining("Peli JIji");
        assertEquals("Peli jiji", optionalMovie.getFirst().getTitle());
    }

    @Test
    public void findMovieByTitleIgnoreCaseNotFound(){
        List<Movie> movies = movieRepository.findMovieByTitleIgnoreCaseContaining("asfdasdf");
        assertTrue(movies.isEmpty());
    }


    @Test
    public void shouldSaveMovieSuccessfully() {
        Movie newMovie = Movie.builder()
                .title("Nueva peli")
                .description("Otra descripcion")
                .movieYear(2022)
                .votes(0)
                .rating(0)
                .build();

        Movie savedMovie = movieRepository.save(newMovie);

        assertNotNull(savedMovie.getId()); // El ID debe generarse
        assertEquals("Nueva peli", savedMovie.getTitle());
    }

    @Test
    public void shouldDeleteMovieSuccessfully() {
        Movie movie = Movie.builder()
                .title("Borrar esta")
                .description("Delete desc")
                .movieYear(2010)
                .votes(0)
                .rating(0)
                .build();

        Movie saved = testEntityManager.persist(movie);
        movieRepository.deleteById(saved.getId());

        Optional<Movie> deleted = movieRepository.findById(saved.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void shouldFindAllMoviesByGenreNameIgnoreCase() {

        // Creamos el género y lo persistimos
        Genre action = Genre.builder().name("Action").build();
        Genre comedy = Genre.builder().name("Comedy").build();
        testEntityManager.persist(action);
        testEntityManager.persist(comedy);

        // Creamos películas con géneros asociados
        Movie movie1 = Movie.builder()
                .title("Action Movie 1")
                .description("Explosions and stuff")
                .movieYear(2010)
                .votes(0)
                .rating(0)
                .genres(List.of(action))
                .build();

        Movie movie2 = Movie.builder()
                .title("Action Comedy Movie")
                .description("Funny and explosive")
                .movieYear(2015)
                .votes(0)
                .rating(0)
                .genres(List.of(action, comedy))
                .build();

        Movie movie3 = Movie.builder()
                .title("Romantic Movie")
                .description("No action here")
                .movieYear(2018)
                .votes(0)
                .rating(0)
                .genres(List.of()) // Sin géneros
                .build();

        testEntityManager.persist(movie1);
        testEntityManager.persist(movie2);
        testEntityManager.persist(movie3);

        // Ejecutamos la búsqueda ignorando mayúsculas
        List<Movie> actionMovies = movieRepository.findAllByGenreNameIgnoreCase("aCtIoN");

        // Verificaciones
        assertEquals(2, actionMovies.size());
        assertTrue(actionMovies.stream().anyMatch(m -> m.getTitle().equals("Action Movie 1")));
        assertTrue(actionMovies.stream().anyMatch(m -> m.getTitle().equals("Action Comedy Movie")));
    }

}