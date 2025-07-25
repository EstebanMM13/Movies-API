package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.services.MovieService;
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
import java.util.Optional;

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

    @MockitoBean()
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
    public void shouldReturnListOfMovies() throws Exception {
        List<Movie> movies = List.of(movie);
        Mockito.when(movieService.getAllMovies()).thenReturn(movies);

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Peli jiji"));
    }

    @Test
    public void findMovieById() throws Exception {
        Mockito.when(movieService.getMovieById(1L)).thenReturn(Optional.ofNullable(movie));
        mockMvc.perform(get("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(movie.getTitle()));
    }

    @Test
    public void findMovieByIdNotFound() throws Exception {
        Mockito.when(movieService.getMovieById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/movies/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindMovieByTitleIgnoreCase() throws Exception {
        Mockito.when(movieService.findByTitleIgnoreCase("peli jiji")).thenReturn(Optional.of(movie));

        mockMvc.perform(get("/api/movies/findByTitle/peli jiji"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Peli jiji"));
    }

    @Test
    public void shouldReturnNotFoundWhenMovieTitleDoesNotExist() throws Exception {
        Mockito.when(movieService.findByTitleIgnoreCase("no existe")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/movies/findByTitle/no existe"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteMovieSuccessfully() throws Exception {
        Mockito.when(movieService.deleteMovie(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent()); // Código 204
    }

    @Test
    public void shouldUpdateMovieSuccessfully() throws Exception {
        Movie updatedMovie = movie;
        updatedMovie.setDescription("Nueva desc");
        Mockito.when(movieService.updateMovie(eq(1L), any(Movie.class))).thenReturn(Optional.of(updatedMovie));

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
    public void shouldReturnNotFoundWhenUpdatingNonexistentMovie() throws Exception {
        Mockito.when(movieService.updateMovie(eq(99L), any(Movie.class))).thenReturn(Optional.empty());

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
    public void shouldVoteMovieSuccessfully() throws Exception {
        Movie votedMovie = movie;
        votedMovie.setRating(5.0);
        votedMovie.setVotes(1);
        Mockito.when(movieService.voteMovie(1L, 5.0)).thenReturn(Optional.of(votedMovie));

        mockMvc.perform(put("/api/movies/1/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5.0))
                .andExpect(jsonPath("$.votes").value(1));
    }

    @Test
    public void shouldReturnNotFoundWhenVotingNonexistentMovie() throws Exception {
        Mockito.when(movieService.voteMovie(99L, 5.0)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/movies/99/5.0"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void createMovie() throws Exception {

        Movie postMovie = Movie.builder()
                .title("Peli jiji")
                .description("Esta es la descripcion jiji")
                .movieYear(2000)
                .votes(0)
                .rating(0)
                .build();

        Mockito.when(movieService.createMovie(postMovie)).thenReturn(postMovie);
        mockMvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "        \"title\" : \"Peli jiji\",\n" +
                                "        \"description\": \"Esta es la descripcion jiji\",\n" +
                                "        \"movieYear\": 2000,\n" +
                                "        \"votes\": 0,\n" +
                                "        \"rating\": 0\n" +
                                "    }"))
                .andExpect(status().isCreated());
    }


}