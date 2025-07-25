package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
        Optional<Movie> optionalMovie = movieRepository.findByTitleIgnoreCase("Peli JIji");
        assertEquals("Peli jiji", optionalMovie.get().getTitle());
    }

    @Test
    public void findMovieByTitleIgnoreCaseNotFound(){
        Optional<Movie> optionalMovie = movieRepository.findByTitleIgnoreCase("Peliafgsdf");
        assertTrue(optionalMovie.isEmpty());
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



}