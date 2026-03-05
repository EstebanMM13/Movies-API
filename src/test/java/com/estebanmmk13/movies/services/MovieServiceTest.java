package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.error.DuplicateVoteException;
import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.models.Vote;
import com.estebanmmk13.movies.repositories.MovieRepository;
import com.estebanmmk13.movies.repositories.UserRepository;
import com.estebanmmk13.movies.repositories.VoteRepository;
import com.estebanmmk13.movies.services.movie.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @MockitoBean
    private MovieRepository movieRepository;

    @MockitoBean
    private VoteRepository voteRepository;

    @MockitoBean
    private UserRepository userRepository;

    private Movie movie;
    private User user;
    private Pageable pageable;
    private Page<Movie> moviePage;

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

        user = User.builder()
                .id(1L)
                .username("Esteban")
                .build();

        pageable = PageRequest.of(0, 10);
        moviePage = new PageImpl<>(List.of(movie), pageable, 1);

        // Configuración común de mocks
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Debería devolver todas las películas paginadas")
    void findAllMovies_ShouldReturnPageOfMovies() {
        // Given
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(moviePage);

        // When
        Page<Movie> result = movieService.findAllMovies(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("El retorno del rey");
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(movieRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Debería devolver una película por ID cuando existe")
    void findMovieById_WhenExists_ShouldReturnMovie() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // When
        Movie result = movieService.findMovieById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("El retorno del rey");
        verify(movieRepository).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la película no existe por ID")
    void findMovieById_WhenNotExists_ShouldThrowException() {
        // Given
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.findMovieById(99L))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("99");

        verify(movieRepository).findById(99L);
    }

    @Test
    @DisplayName("Debería crear una nueva película")
    void createMovie_ShouldSaveAndReturnMovie() {
        // Given
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        // When
        Movie result = movieService.createMovie(movie);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("El retorno del rey");
        verify(movieRepository).save(movie);
    }

    @Test
    @DisplayName("Debería actualizar una película existente")
    void updateMovie_WhenExists_ShouldUpdateAndReturn() {
        // Given
        Movie updatedDetails = Movie.builder()
                .title("El retorno del rey (Edición extendida)")
                .description("Nueva descripción")
                .movieYear(2004)
                .votes(10)
                .rating(4.5)
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Movie result = movieService.updateMovie(1L, updatedDetails);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("El retorno del rey (Edición extendida)");

        verify(movieRepository).findById(1L);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar una película inexistente")
    void updateMovie_WhenNotExists_ShouldThrowException() {
        // Given
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.updateMovie(99L, movie))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("99");

        verify(movieRepository).findById(99L);
        verify(movieRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar una película existente")
    void deleteMovie_WhenExists_ShouldDelete() {
        // Given
        when(movieRepository.existsById(1L)).thenReturn(true);

        // When
        movieService.deleteMovie(1L);

        // Then
        verify(movieRepository).existsById(1L);
        verify(movieRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar una película inexistente")
    void deleteMovie_WhenNotExists_ShouldThrowException() {
        // Given
        when(movieRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> movieService.deleteMovie(99L))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("99");

        verify(movieRepository).existsById(99L);
        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debería votar una película correctamente")
    void voteMovie_ShouldUpdateRatingAndVotesAndSaveVote() {
        // Given
        double newRating = 5.0;
        int initialVotes = movie.getVotes();
        double initialAverage = movie.getRating();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(voteRepository.existsByUserAndMovie(user, movie)).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Movie result = movieService.voteMovie(1L, 1L, newRating);

        // Then
        double expectedRating = (initialAverage * initialVotes + newRating) / (initialVotes + 1);

        assertThat(result.getVotes()).isEqualTo(initialVotes + 1);
        assertThat(result.getRating()).isEqualTo(expectedRating);

        verify(voteRepository).existsByUserAndMovie(user, movie);
        verify(voteRepository).save(any(Vote.class));

        // 🔹 CORREGIDO: Solo se llama 1 vez, no 2
        verify(movieRepository, times(1)).save(any(Movie.class));

        // También podemos verificar que se llamó con la película actualizada
        verify(movieRepository).save(argThat(savedMovie ->
                savedMovie.getVotes() == initialVotes + 1 &&
                        savedMovie.getRating() == expectedRating
        ));
    }

    @Test
    @DisplayName("Debería lanzar excepción al votar una película inexistente")
    void voteMovie_WhenMovieNotFound_ShouldThrowException() {
        // Given
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.voteMovie(99L, 1L, 4.0))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("99");

        verify(movieRepository).findById(99L);
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción al votar con usuario inexistente")
    void voteMovie_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.voteMovie(1L, 99L, 4.0))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");

        verify(userRepository).findById(99L);
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción al votar dos veces la misma película")
    void voteMovie_WhenDuplicateVote_ShouldThrowException() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(voteRepository.existsByUserAndMovie(user, movie)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> movieService.voteMovie(1L, 1L, 4.0))
                .isInstanceOf(DuplicateVoteException.class)
                .hasMessageContaining("already voted");

        verify(voteRepository).existsByUserAndMovie(user, movie);
        verify(voteRepository, never()).save(any());
        verify(movieRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería encontrar películas por título (búsqueda parcial)")
    void findMovieByTitleContaining_ShouldReturnPageOfMovies() {
        // Given
        String searchTerm = "retorno";
        when(movieRepository.findMovieByTitleContaining(eq(searchTerm), any(Pageable.class)))
                .thenReturn(moviePage);

        // When
        Page<Movie> result = movieService.findMovieByTitleContaining(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains(searchTerm);

        verify(movieRepository).findMovieByTitleContaining(searchTerm, pageable);
    }

    @Test
    @DisplayName("Debería devolver página vacía cuando no encuentra películas por título")
    void findMovieByTitleContaining_WhenNoMatches_ShouldReturnEmptyPage() {
        // Given
        String searchTerm = "xyz";
        Page<Movie> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(movieRepository.findMovieByTitleContaining(eq(searchTerm), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When
        Page<Movie> result = movieService.findMovieByTitleContaining(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Debería encontrar películas por género")
    void findAllMoviesByGenre_ShouldReturnPageOfMovies() {
        // Given
        String genreName = "Fantasía";
        when(movieRepository.findAllByGenreName(eq(genreName), any(Pageable.class)))
                .thenReturn(moviePage);

        // When
        Page<Movie> result = movieService.findAllMoviesByGenre(genreName, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(movieRepository).findAllByGenreName(genreName, pageable);
    }

    @Test
    @DisplayName("Debería devolver página vacía cuando no encuentra películas por género")
    void findAllMoviesByGenre_WhenNoMatches_ShouldReturnEmptyPage() {
        // Given
        String genreName = "NoExistente";
        Page<Movie> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(movieRepository.findAllByGenreName(eq(genreName), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When
        Page<Movie> result = movieService.findAllMoviesByGenre(genreName, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }
}

