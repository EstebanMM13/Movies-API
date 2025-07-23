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
    public void findMovieByTitleIgnoreCaseFound(){
        Optional<Movie> optionalMovie = movieRepository.findByTitleIgnoreCase("Peli jiji");
        assertEquals("Peli jiji", optionalMovie.get().getTitle());
    }

    @Test
    public void findMovieByTitleIgnoreCaseNotFound(){
        Optional<Movie> optionalMovie = movieRepository.findByTitleIgnoreCase("Peliafgsdf");
        assertEquals(Optional.empty(),optionalMovie);
    }


}