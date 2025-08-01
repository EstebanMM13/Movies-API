package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.repositories.MovieRepository;
import com.estebanmmk13.movies.services.movie.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @MockitoBean
    private MovieRepository movieRepository;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("El retorno del rey")
                .description("Una descripción épica")
                .movieYear(2003)
                .votes(10)
                .rating(4.5)
                .build();
    }

    @Test
    @DisplayName("Debería devolver todas las películas")
    void findAll() {
        List<Movie> movies = List.of(movie);
        Mockito.when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.findAllMovies();
        assertEquals(1, result.size());
        assertEquals("El retorno del rey", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Debería devolver una película por ID")
    void findById() {
        Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Movie result = movieService.findMovieById(1L);

        assertEquals(movie.getTitle(), result.getTitle());
    }

    @Test
    @DisplayName("Debería lanzar excepción si la película no existe")
    void findByIdNotFound() {
        Mockito.when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.findMovieById(99L));
    }

    @Test
    @DisplayName("Debería crear una nueva película")
    void create() {
        Mockito.when(movieRepository.save(movie)).thenReturn(movie);

        Movie result = movieService.createMovie(movie);
        assertEquals(movie.getTitle(), result.getTitle());
    }

    @Test
    @DisplayName("Debería actualizar una película existente")
    void update() {
        Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.save(movie)).thenReturn(movie);

        Movie result = movieService.updateMovie(1L, movie);
        assertEquals(movie.getTitle(), result.getTitle());
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar una película inexistente")
    void updateNotFound() {
        Mockito.when(movieRepository.existsById(1L)).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(1L, movie));
    }

    @Test
    @DisplayName("Debería eliminar una película existente")
    void delete() {
        Mockito.when(movieRepository.existsById(1L)).thenReturn(true);

        movieService.deleteMovie(1L);

        Mockito.verify(movieRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar una película inexistente")
    void deleteNotFound() {
        Mockito.when(movieRepository.existsById(1L)).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(1L));
    }

    @Test
    @DisplayName("Debería votar una película y actualizar rating y votos")
    void vote() {
        Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.save(Mockito.any(Movie.class))).thenAnswer(inv -> inv.getArgument(0));

        double newVote = 5.0;
        Movie result = movieService.voteMovie(1L, newVote);

        assertEquals(11, result.getVotes());
        assertEquals(((10 * 4.5) + newVote) / 11, result.getRating());
    }

    @Test
    @DisplayName("Debería lanzar excepción al votar una película inexistente")
    void voteNotFound() {
        Mockito.when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.voteMovie(99L, 4.0));
    }

    @Test
    @DisplayName("Debería encontrar una película por título ignorando mayúsculas")
    void findByTitleIgnoreCase() {
        Mockito.when(movieRepository.findMovieByTitleIgnoreCase("el retorno del rey")).thenReturn(Optional.of(movie));

        Movie result = movieService.findMovieByTitleIgnoreCase("el retorno del rey");

        assertEquals(movie.getTitle(), result.getTitle());
    }
}

