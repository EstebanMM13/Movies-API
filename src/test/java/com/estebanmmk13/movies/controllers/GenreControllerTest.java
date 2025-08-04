package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.error.notFound.GenreNotFoundException;
import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.services.genre.GenreService;
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

@WebMvcTest(GenreController.class)
@ActiveProfiles("test")
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = Genre.builder()
                .id(1L)
                .name("Acción")
                .build();
    }

    @Test
    void findAllGenres() throws Exception {
        Mockito.when(genreService.findAllGenres()).thenReturn(List.of(genre));

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Acción"));
    }

    @Test
    void findGenreById() throws Exception {
        Mockito.when(genreService.findGenreById(1L)).thenReturn(genre);

        mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acción"));
    }

    @Test
    void findGenreByIdNotFound() throws Exception {
        Mockito.when(genreService.findGenreById(99L)).thenThrow(new GenreNotFoundException("No encontrado"));

        mockMvc.perform(get("/api/genres/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findGenreByName() throws Exception {
        Mockito.when(genreService.findGenreByNameIgnoreCase("acción")).thenReturn(genre);

        mockMvc.perform(get("/api/genres/name/acción"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acción"));
    }

    @Test
    void findGenreByNameNotFound() throws Exception {
        Mockito.when(genreService.findGenreByNameIgnoreCase("fantasía"))
                .thenThrow(new GenreNotFoundException("No encontrado"));

        mockMvc.perform(get("/api/genres/name/fantasía"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createGenre() throws Exception {
        Mockito.when(genreService.createGenre(any(Genre.class))).thenReturn(genre);

        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Acción"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Acción"));
    }

    @Test
    void updateGenre() throws Exception {
        Genre updated = Genre.builder().id(1L).name("Aventura").build();

        Mockito.when(genreService.updateGenre(eq(1L), any(Genre.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/genres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Aventura"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aventura"));
    }

    @Test
    void updateGenreNotFound() throws Exception {
        Mockito.when(genreService.updateGenre(eq(99L), any(Genre.class)))
                .thenThrow(new GenreNotFoundException("No encontrado"));

        mockMvc.perform(patch("/api/genres/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Inexistente"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGenre() throws Exception {
        Mockito.doNothing().when(genreService).deleteGenre(1L);

        mockMvc.perform(delete("/api/genres/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteGenreNotFound() throws Exception {
        Mockito.doThrow(new GenreNotFoundException("No encontrado")).when(genreService).deleteGenre(99L);

        mockMvc.perform(delete("/api/genres/99"))
                .andExpect(status().isNotFound());
    }
}

