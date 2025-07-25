package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.error.MovieNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.repositories.MovieRepository;
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
    void getAllMoviesShouldReturnList() {
        List<Movie> movies = List.of(movie);
        Mockito.when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.getAllMovies();
        assertEquals(1, result.size());
        assertEquals("El retorno del rey", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Debería devolver una película por ID")
    void getMovieByIdShouldReturnMovie() throws Exception {
        Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Optional<Movie> result = movieService.getMovieById(1L);
        assertTrue(result.isPresent());
        assertEquals(movie.getTitle(), result.get().getTitle());
    }

    @Test
    @DisplayName("Debería lanzar excepción si la película no existe al buscar por ID")
    void getMovieByIdShouldThrowIfNotFound() {
        Mockito.when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> {
            movieService.getMovieById(99L);
        });
    }

    @Test
    @DisplayName("Debería crear una nueva película")
    void createMovieShouldSave() {
        Mockito.when(movieRepository.save(movie)).thenReturn(movie);

        Movie result = movieService.createMovie(movie);
        assertEquals(movie.getTitle(), result.getTitle());
    }

    @Test
    @DisplayName("Debería actualizar una película existente")
    void updateMovieShouldUpdateIfExists() {
        Mockito.when(movieRepository.existsById(1L)).thenReturn(true);
        Mockito.when(movieRepository.save(movie)).thenReturn(movie);

        Optional<Movie> result = movieService.updateMovie(1L, movie);
        assertTrue(result.isPresent());
        assertEquals(movie.getTitle(), result.get().getTitle());
    }

    @Test
    @DisplayName("No debería actualizar una película si no existe")
    void updateMovieShouldReturnEmptyIfNotFound() {
        Mockito.when(movieRepository.existsById(1L)).thenReturn(false);

        Optional<Movie> result = movieService.updateMovie(1L, movie);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería eliminar una película si existe")
    void deleteMovieShouldDeleteIfExists() {
        Mockito.when(movieRepository.existsById(1L)).thenReturn(true);

        boolean result = movieService.deleteMovie(1L);
        assertTrue(result);
        Mockito.verify(movieRepository).deleteById(1L);
    }

    @Test
    @DisplayName("No debería eliminar una película si no existe")
    void deleteMovieShouldReturnFalseIfNotExists() {
        Mockito.when(movieRepository.existsById(1L)).thenReturn(false);

        boolean result = movieService.deleteMovie(1L);
        assertFalse(result);
    }

    @Test
    @DisplayName("Debería votar una película y actualizar rating y votos")
    void voteMovieShouldUpdateRating() {
        Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.save(Mockito.any(Movie.class))).thenAnswer(inv -> inv.getArgument(0));

        double newVote = 5.0;
        Optional<Movie> result = movieService.voteMovie(1L, newVote);

        assertTrue(result.isPresent());
        Movie voted = result.get();
        assertEquals(11, voted.getVotes());
        assertEquals(((10 * 4.5) + newVote) / 11, voted.getRating());
    }

    @Test
    @DisplayName("No debería votar si la película no existe")
    void voteMovieShouldReturnEmptyIfMovieNotFound() {
        Mockito.when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Movie> result = movieService.voteMovie(99L, 4.0);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería encontrar una película por título ignorando mayúsculas")
    void findByTitleIgnoreCaseShouldReturnMovie() {
        Mockito.when(movieRepository.findByTitleIgnoreCase("el retorno del rey")).thenReturn(Optional.of(movie));

        Optional<Movie> result = movieService.findByTitleIgnoreCase("el retorno del rey");
        assertTrue(result.isPresent());
        assertEquals(movie.getTitle(), result.get().getTitle());
    }
}
