package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.services.movie.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = "USER")
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    private Movie movie;
    private Movie updatedMovie;
    private Pageable pageable;
    private Page<Movie> moviePage;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("Peli jiji")
                .description("Esta es la descripcion jiji")
                .movieYear(2000)
                .votes(0)
                .rating(0)
                .build();

        updatedMovie = Movie.builder()
                .id(1L)
                .title("Peli jiji actualizada")
                .description("Nueva descripcion")
                .movieYear(2001)
                .votes(5)
                .rating(4.5)
                .build();

        pageable = PageRequest.of(0, 10);
        moviePage = new PageImpl<>(List.of(movie), pageable, 1);
    }

    @Test
    @DisplayName("GET /api/movies - Should return paginated list of movies")
    void findAllMovies_ShouldReturnPageOfMovies() throws Exception {
        when(movieService.findAllMovies(any(Pageable.class))).thenReturn(moviePage);

        mockMvc.perform(get("/api/movies")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Peli jiji"))
                .andExpect(jsonPath("$.content[0].description").value("Esta es la descripcion jiji"))
                .andExpect(jsonPath("$.content[0].movieYear").value(2000));
    }

    @Test
    @DisplayName("GET /api/movies/{id} - Should return movie when exists")
    void findMovieById_WhenExists_ShouldReturnMovie() throws Exception {
        when(movieService.findMovieById(1L)).thenReturn(movie);

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Peli jiji"))
                .andExpect(jsonPath("$.description").value("Esta es la descripcion jiji"))
                .andExpect(jsonPath("$.movieYear").value(2000));

        verify(movieService).findMovieById(1L);
    }

    @Test
    @DisplayName("GET /api/movies/{id} - Should return 404 when movie not found")
    void findMovieById_WhenNotExists_ShouldReturn404() throws Exception {
        when(movieService.findMovieById(99L))
                .thenThrow(new MovieNotFoundException("Movie not found with id: 99"));

        mockMvc.perform(get("/api/movies/99"))
                .andExpect(status().isNotFound());

        verify(movieService).findMovieById(99L);
    }

    @Test
    @DisplayName("POST /api/movies - Should create movie and return 201")
    void createMovie_ShouldReturnCreated() throws Exception {
        when(movieService.createMovie(any(Movie.class))).thenReturn(movie);

        mockMvc.perform(post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Peli jiji",
                                    "description": "Esta es la descripcion jiji",
                                    "movieYear": 2000
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Peli jiji"))
                .andExpect(jsonPath("$.description").value("Esta es la descripcion jiji"));

        verify(movieService).createMovie(any(Movie.class));
    }

    @Test
    @DisplayName("POST /api/movies - Should return 400 when validation fails")
    void createMovie_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "",
                                    "movieYear": null
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).createMovie(any(Movie.class));
    }

    @Test
    @DisplayName("PATCH /api/movies/{id} - Should update movie and return 200")
    void updateMovie_ShouldReturnUpdatedMovie() throws Exception {
        when(movieService.updateMovie(eq(1L), any(Movie.class))).thenReturn(updatedMovie);

        mockMvc.perform(patch("/api/movies/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Peli jiji actualizada",
                                    "description": "Nueva descripcion",
                                    "movieYear": 2001
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Peli jiji actualizada"))
                .andExpect(jsonPath("$.description").value("Nueva descripcion"))
                .andExpect(jsonPath("$.movieYear").value(2001));

        verify(movieService).updateMovie(eq(1L), any(Movie.class));
    }

    @Test
    @DisplayName("PATCH /api/movies/{id} - Should return 404 when movie not found")
    void updateMovie_WhenNotExists_ShouldReturn404() throws Exception {
        when(movieService.updateMovie(eq(99L), any(Movie.class)))
                .thenThrow(new MovieNotFoundException("Movie not found with id: 99"));

        mockMvc.perform(patch("/api/movies/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Peli actualizada"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(movieService).updateMovie(eq(99L), any(Movie.class));
    }

    @Test
    @DisplayName("PATCH /api/movies/{id} - Should allow partial update")
    void updateMovie_WithPartialData_ShouldUpdateOnlyProvidedFields() throws Exception {
        Movie partiallyUpdated = Movie.builder()
                .id(1L)
                .title("Solo titulo actualizado")
                .description("Esta es la descripcion jiji")  // Se mantiene igual
                .movieYear(2000)  // Se mantiene igual
                .votes(0)
                .rating(0)
                .build();

        when(movieService.updateMovie(eq(1L), any(Movie.class))).thenReturn(partiallyUpdated);

        mockMvc.perform(patch("/api/movies/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Solo titulo actualizado"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Solo titulo actualizado"))
                .andExpect(jsonPath("$.description").value("Esta es la descripcion jiji"))
                .andExpect(jsonPath("$.movieYear").value(2000));
    }

    @Test
    @DisplayName("DELETE /api/movies/{id} - Should delete movie and return 204")
    void deleteMovie_ShouldReturnNoContent() throws Exception {
        doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/movies/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(movieService).deleteMovie(1L);
    }

    @Test
    @DisplayName("DELETE /api/movies/{id} - Should return 404 when movie not found")
    void deleteMovie_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new MovieNotFoundException("Movie not found with id: 99"))
                .when(movieService).deleteMovie(99L);

        mockMvc.perform(delete("/api/movies/99")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(movieService).deleteMovie(99L);
    }

    @Test
    @DisplayName("GET /api/movies/title - Should return movies by title")
    void findMovieByTitle_ShouldReturnPageOfMovies() throws Exception {
        when(movieService.findMovieByTitleContaining(eq("Peli"), any(Pageable.class)))
                .thenReturn(moviePage);

        mockMvc.perform(get("/api/movies/title")
                        .param("title", "Peli")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Peli jiji"));

        verify(movieService).findMovieByTitleContaining(eq("Peli"), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/movies/title - Should return 400 when title is missing")
    void findMovieByTitle_WithoutTitle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/movies/title")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).findMovieByTitleContaining(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /api/movies/{movieId}/vote/{userId}/{rating} - Should vote and return updated movie")
    void voteMovie_ShouldReturnUpdatedMovie() throws Exception {
        Movie votedMovie = Movie.builder()
                .id(1L)
                .title("Peli jiji")
                .description("Esta es la descripcion jiji")
                .movieYear(2000)
                .votes(1)
                .rating(5.0)
                .build();

        when(movieService.voteMovie(1L, 1L, 5.0)).thenReturn(votedMovie);

        mockMvc.perform(put("/api/movies/1/vote/1/5.0")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5.0))
                .andExpect(jsonPath("$.votes").value(1));

        verify(movieService).voteMovie(1L, 1L, 5.0);
    }

    @Test
    @DisplayName("PUT /api/movies/{movieId}/vote/{userId}/{rating} - Should return 404 when movie not found")
    void voteMovie_WhenMovieNotFound_ShouldReturn404() throws Exception {
        when(movieService.voteMovie(99L, 1L, 5.0))
                .thenThrow(new MovieNotFoundException("Movie not found with id: 99"));

        mockMvc.perform(put("/api/movies/99/vote/1/5.0")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(movieService).voteMovie(99L, 1L, 5.0);
    }

    @Test
    @DisplayName("PUT /api/movies/{movieId}/vote/{userId}/{rating} - Should return 400 when rating is invalid")
    void voteMovie_WithInvalidRating_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/api/movies/1/vote/1/11.0")  // Rating fuera de rango
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).voteMovie(anyLong(), anyLong(), anyDouble());
    }

    @Test
    @DisplayName("GET /api/movies/genre/{name} - Should return movies by genre")
    void findAllMoviesByGenre_ShouldReturnPageOfMovies() throws Exception {
        when(movieService.findAllMoviesByGenre(eq("Acción"), any(Pageable.class)))
                .thenReturn(moviePage);

        mockMvc.perform(get("/api/movies/genre/Acción")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Peli jiji"));

        verify(movieService).findAllMoviesByGenre(eq("Acción"), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/movies/genre/{name} - Should return empty page when genre has no movies")
    void findAllMoviesByGenre_WithNoMovies_ShouldReturnEmptyPage() throws Exception {
        Page<Movie> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(movieService.findAllMoviesByGenre(eq("Inexistente"), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/movies/genre/Inexistente")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Should handle pagination parameters correctly in findAllMovies")
    void findAllMovies_ShouldHandlePaginationParameters() throws Exception {
        when(movieService.findAllMovies(any(Pageable.class))).thenReturn(moviePage);

        mockMvc.perform(get("/api/movies")
                        .param("page", "2")
                        .param("size", "5")
                        .param("sort", "title,desc"))
                .andExpect(status().isOk());

        verify(movieService).findAllMovies(argThat(p ->
                p.getPageNumber() == 2 &&
                        p.getPageSize() == 5 &&
                        p.getSort().toString().contains("title: DESC")
        ));
    }

    @Test
    @DisplayName("Should handle pagination parameters correctly in findByGenre")
    void findAllMoviesByGenre_ShouldHandlePaginationParameters() throws Exception {
        when(movieService.findAllMoviesByGenre(eq("Acción"), any(Pageable.class)))
                .thenReturn(moviePage);

        mockMvc.perform(get("/api/movies/genre/Acción")
                        .param("page", "1")
                        .param("size", "20")
                        .param("sort", "rating,desc"))
                .andExpect(status().isOk());

        verify(movieService).findAllMoviesByGenre(eq("Acción"), argThat(p ->
                p.getPageNumber() == 1 &&
                        p.getPageSize() == 20 &&
                        p.getSort().toString().contains("rating: DESC")
        ));
    }

    @Test
    @DisplayName("Should handle pagination parameters correctly in findByTitle")
    void findMovieByTitle_ShouldHandlePaginationParameters() throws Exception {
        when(movieService.findMovieByTitleContaining(eq("Peli"), any(Pageable.class)))
                .thenReturn(moviePage);

        mockMvc.perform(get("/api/movies/title")
                        .param("title", "Peli")
                        .param("page", "3")
                        .param("size", "15")
                        .param("sort", "movieYear,asc"))
                .andExpect(status().isOk());

        verify(movieService).findMovieByTitleContaining(eq("Peli"), argThat(p ->
                p.getPageNumber() == 3 &&
                        p.getPageSize() == 15 &&
                        p.getSort().toString().contains("movieYear: ASC")
        ));
    }

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void whenUserNotAuthenticated_ShouldReturn401() throws Exception {
        // Este test sobrescribe el @WithMockUser de la clase
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isUnauthorized());
    }
}