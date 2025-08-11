package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.services.movie.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@ActiveProfiles("test")
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    private Movie movie;

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
    }

    @Test
    void findAll() throws Exception {
        Mockito.when(movieService.findAllMovies()).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Peli jiji"));
    }

    @Test
    void findById() throws Exception {
        Mockito.when(movieService.findMovieById(1L)).thenReturn(movie);

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Peli jiji"));
    }

    @Test
    void findByIdNotFound() throws Exception {
        Mockito.when(movieService.findMovieById(99L)).thenThrow(new MovieNotFoundException("No encontrada"));

        mockMvc.perform(get("/api/movies/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByTitle() throws Exception {
        Mockito.when(movieService.findMovieByTitleIgnoreCaseContaining("peli jiji"))
                .thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies/title/peli jiji"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Peli jiji"));
    }

    @Test
    void findByTitleNotFound() throws Exception {
        Mockito.when(movieService.findMovieByTitleIgnoreCaseContaining("no existe")).thenThrow(new MovieNotFoundException("No encontrada"));

        mockMvc.perform(get("/api/movies/title/no existe"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovie() throws Exception {
        Mockito.doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMovieNotFound() throws Exception {
        Mockito.doThrow(new MovieNotFoundException("No encontrada")).when(movieService).deleteMovie(99L);

        mockMvc.perform(delete("/api/movies/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMovie() throws Exception {
        Movie updated = movie;
        updated.setDescription("Nueva desc");

        Mockito.when(movieService.updateMovie(eq(1L), any(Movie.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Peli jiji",
                                    "description": "Nueva desc",
                                    "movieYear": 2000,
                                    "votes": 0,
                                    "rating": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Nueva desc"));
    }

    @Test
    void updateMovieNotFound() throws Exception {
        Mockito.when(movieService.updateMovie(eq(99L), any(Movie.class)))
                .thenThrow(new MovieNotFoundException("No encontrada"));

        mockMvc.perform(patch("/api/movies/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Desconocida",
                                    "description": "No existe",
                                    "movieYear": 2001
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void voteMovie() throws Exception {
        Movie voted = movie;
        voted.setVotes(1);
        voted.setRating(5.0);

        Mockito.when(movieService.voteMovie(1L, 1L,5.0)).thenReturn(voted);

        mockMvc.perform(put("/api/movies/1/vote/1/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5.0))
                .andExpect(jsonPath("$.votes").value(1));
    }

    @Test
    void voteMovieNotFound() throws Exception {
        Mockito.when(movieService.voteMovie(99L, 99L,5.0))
                .thenThrow(new MovieNotFoundException("No encontrada"));

        mockMvc.perform(put("/api/movies/99/vote/99/5.0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMovie() throws Exception {
        Mockito.when(movieService.createMovie(any(Movie.class))).thenReturn(movie);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Peli jiji",
                                    "description": "Esta es la descripcion jiji",
                                    "movieYear": 2000,
                                    "votes": 0,
                                    "rating": 0
                                }
                                """))
                .andExpect(status().isCreated());
    }

}