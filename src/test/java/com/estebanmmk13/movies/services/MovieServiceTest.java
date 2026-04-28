package com.estebanmmk13.movies.services;


import com.estebanmmk13.movies.dtoModels.GenreDTO;
import com.estebanmmk13.movies.dtoModels.MovieRequestDTO;
import com.estebanmmk13.movies.dtoModels.MovieResponseDTO;
import com.estebanmmk13.movies.error.DuplicateVoteException;
import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.mapper.MovieMapper;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.models.Vote;
import com.estebanmmk13.movies.repositories.GenreRepository;
import com.estebanmmk13.movies.repositories.MovieRepository;
import com.estebanmmk13.movies.repositories.UserRepository;
import com.estebanmmk13.movies.repositories.VoteRepository;
import com.estebanmmk13.movies.services.movie.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie;
    private User user;
    private Pageable pageable;
    private Page<Movie> moviePage;
    private MovieRequestDTO movieRequestDTO;
    private MovieResponseDTO movieResponseDTO;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("El retorno del rey")
                .description("Una descripción épica")
                .movieYear(2003)
                .votes(10)
                .rating(4.5)
                .genres(new ArrayList<>())
                .build();

        user = User.builder()
                .id(1L)
                .username("Esteban")
                .build();

        pageable = PageRequest.of(0, 10);
        moviePage = new PageImpl<>(List.of(movie), pageable, 1);

        movieRequestDTO = new MovieRequestDTO();
        movieRequestDTO.setTitle("El retorno del rey");
        movieRequestDTO.setDescription("Una descripción épica");
        movieRequestDTO.setMovieYear(2003);
        movieRequestDTO.setImageUrl("http://imagen.com");
        movieRequestDTO.setGenreIds(List.of(1L, 2L));

        movieResponseDTO = new MovieResponseDTO(
                1L, "El retorno del rey", "Una descripción épica", 2003,
                10, 4.5, "http://imagen.com",
                List.of(new GenreDTO(1L, "Fantasía"))
        );
    }

    @Test
    @DisplayName("Debería devolver todas las películas paginadas como DTOs")
    void findAllMovies_ShouldReturnPageOfMovieResponseDTO() {
        // Given
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(moviePage);
        when(movieMapper.toResponseDTO(any(Movie.class))).thenReturn(movieResponseDTO);

        // When
        Page<MovieResponseDTO> result = movieService.findAllMovies(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("El retorno del rey");
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(movieRepository).findAll(pageable);
        verify(movieMapper, times(1)).toResponseDTO(movie);
    }

    @Test
    @DisplayName("Debería devolver una película por ID cuando existe (como DTO)")
    void findMovieById_WhenExists_ShouldReturnMovieResponseDTO() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieMapper.toResponseDTO(movie)).thenReturn(movieResponseDTO);

        // When
        MovieResponseDTO result = movieService.findMovieById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("El retorno del rey");
        verify(movieRepository).findById(1L);
        verify(movieMapper).toResponseDTO(movie);
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
        verify(movieMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debería crear una nueva película a partir de DTO")
    void createMovie_ShouldMapDTOToEntity_SaveAndReturnDTO() {
        // Given
        when(movieMapper.toEntity(movieRequestDTO)).thenReturn(movie);
        when(genreRepository.findAllById(movieRequestDTO.getGenreIds())).thenReturn(new ArrayList<>());
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toResponseDTO(movie)).thenReturn(movieResponseDTO);

        // When
        MovieResponseDTO result = movieService.createMovie(movieRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("El retorno del rey");
        verify(movieMapper).toEntity(movieRequestDTO);
        verify(genreRepository).findAllById(movieRequestDTO.getGenreIds());
        verify(movieRepository).save(movie);
        verify(movieMapper).toResponseDTO(movie);
    }

    @Test
    @DisplayName("Debería actualizar una película existente desde DTO")
    void updateMovie_WhenExists_ShouldUpdateAndReturnDTO() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(genreRepository.findAllById(movieRequestDTO.getGenreIds())).thenReturn(new ArrayList<>());
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toResponseDTO(movie)).thenReturn(movieResponseDTO);

        // When
        MovieResponseDTO result = movieService.updateMovie(1L, movieRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("El retorno del rey");
        verify(movieRepository).findById(1L);
        verify(movieMapper).updateEntity(movie, movieRequestDTO);
        verify(genreRepository).findAllById(movieRequestDTO.getGenreIds());
        verify(movieRepository).save(movie);
        verify(movieMapper).toResponseDTO(movie);
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar una película inexistente")
    void updateMovie_WhenNotExists_ShouldThrowException() {
        // Given
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.updateMovie(99L, movieRequestDTO))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("99");

        verify(movieRepository).findById(99L);
        verify(movieMapper, never()).updateEntity(any(), any());
        verify(movieRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar una película existente")
    void deleteMovie_WhenExists_ShouldDelete() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // When
        movieService.deleteMovie(1L);

        // Then
        verify(movieRepository).findById(1L);
        verify(movieRepository).delete(movie);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar una película inexistente")
    void deleteMovie_WhenNotExists_ShouldThrowException() {
        // Given
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.deleteMovie(99L))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("99");

        verify(movieRepository).findById(99L);
        verify(movieRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería votar una película correctamente y devolver DTO actualizado")
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

        // 🔁 Mapper dinámico: construye el DTO a partir del Movie actualizado
        when(movieMapper.toResponseDTO(any(Movie.class))).thenAnswer(invocation -> {
            Movie m = invocation.getArgument(0);
            return new MovieResponseDTO(
                    m.getId(),
                    m.getTitle(),
                    m.getDescription(),
                    m.getMovieYear(),
                    m.getVotes(),
                    m.getRating(),
                    m.getImageUrl(),
                    m.getGenres().stream()
                            .map(g -> new GenreDTO(g.getId(), g.getName()))
                            .collect(Collectors.toList())
            );
        });

        // When
        MovieResponseDTO result = movieService.voteMovie(1L, 1L, newRating);

        // Then
        double expectedRating = (initialAverage * initialVotes + newRating) / (initialVotes + 1);

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isCloseTo(expectedRating, within(1e-9));
        assertThat(result.getVotes()).isEqualTo(initialVotes + 1);

        verify(voteRepository).existsByUserAndMovie(user, movie);
        verify(voteRepository).save(any(Vote.class));
        verify(movieRepository).save(movie);
        verify(movieMapper).toResponseDTO(movie);
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
    @DisplayName("Debería encontrar películas por título (búsqueda parcial) y devolver DTOs")
    void findMovieByTitleContaining_ShouldReturnPageOfDTOs() {
        // Given
        String searchTerm = "retorno";
        when(movieRepository.findMovieByTitleContaining(eq(searchTerm), any(Pageable.class)))
                .thenReturn(moviePage);
        when(movieMapper.toResponseDTO(any(Movie.class))).thenReturn(movieResponseDTO);

        // When
        Page<MovieResponseDTO> result = movieService.findMovieByTitleContaining(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains(searchTerm);
        verify(movieRepository).findMovieByTitleContaining(searchTerm, pageable);
        verify(movieMapper, times(1)).toResponseDTO(movie);
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
        Page<MovieResponseDTO> result = movieService.findMovieByTitleContaining(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(movieMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debería encontrar películas por género y devolver DTOs")
    void findAllMoviesByGenre_ShouldReturnPageOfDTOs() {
        // Given
        String genreName = "Fantasía";
        when(movieRepository.findAllByGenreName(eq(genreName), any(Pageable.class)))
                .thenReturn(moviePage);
        when(movieMapper.toResponseDTO(any(Movie.class))).thenReturn(movieResponseDTO);

        // When
        Page<MovieResponseDTO> result = movieService.findAllMoviesByGenre(genreName, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(movieRepository).findAllByGenreName(genreName, pageable);
        verify(movieMapper, times(1)).toResponseDTO(movie);
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
        Page<MovieResponseDTO> result = movieService.findAllMoviesByGenre(genreName, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(movieMapper, never()).toResponseDTO(any());
    }
}