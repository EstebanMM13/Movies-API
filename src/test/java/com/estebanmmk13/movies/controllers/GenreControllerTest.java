package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.error.notFound.GenreNotFoundException;
import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.services.genre.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    // ============================
    // FIND ALL (PAGINADO)
    // ============================
    @Test
    void findAllGenres() throws Exception {

        Pageable pageable = Pageable.ofSize(10);
        Page<Genre> genrePage = new PageImpl<>(List.of(genre), pageable, 1);

        Mockito.when(genreService.findAllGenres(any(Pageable.class)))
                .thenReturn(genrePage);

        mockMvc.perform(get("/api/genres")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Acción"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    // ============================
    // FIND BY ID
    // ============================
    @Test
    void findGenreById() throws Exception {
        Mockito.when(genreService.findGenreById(1L)).thenReturn(genre);

        mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acción"));
    }

    @Test
    void findGenreByIdNotFound() throws Exception {
        Mockito.when(genreService.findGenreById(99L))
                .thenThrow(new GenreNotFoundException("No encontrado"));

        mockMvc.perform(get("/api/genres/99"))
                .andExpect(status().isNotFound());
    }

    // ============================
    // FIND BY NAME
    // ============================
    @Test
    void findGenreByName() throws Exception {

        Pageable pageable = Pageable.ofSize(10);
        Page<Genre> genrePage = new PageImpl<>(List.of(genre), pageable, 1);

        Mockito.when(genreService.findGenreByName(eq("acción"), any(Pageable.class)))
                .thenReturn(genrePage);

        mockMvc.perform(get("/api/genres/name/acción")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Acción"));
    }

    @Test
    void findGenreByNameNotFound() throws Exception {

        Mockito.when(genreService.findGenreByName(eq("fantasía"), any(Pageable.class)))
                .thenThrow(new GenreNotFoundException("No encontrado"));

        mockMvc.perform(get("/api/genres/name/fantasía")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    // ============================
    // CREATE
    // ============================
    @Test
    void createGenre() throws Exception {
        Mockito.when(genreService.createGenre(any(Genre.class)))
                .thenReturn(genre);

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

    // ============================
    // UPDATE
    // ============================
    @Test
    void updateGenre() throws Exception {
        Genre updated = Genre.builder()
                .id(1L)
                .name("Aventura")
                .build();

        Mockito.when(genreService.updateGenre(eq(1L), any(Genre.class)))
                .thenReturn(updated);

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

    // ============================
    // DELETE
    // ============================
    @Test
    void deleteGenre() throws Exception {
        Mockito.doNothing().when(genreService).deleteGenre(1L);

        mockMvc.perform(delete("/api/genres/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteGenreNotFound() throws Exception {
        Mockito.doThrow(new GenreNotFoundException("No encontrado"))
                .when(genreService).deleteGenre(99L);

        mockMvc.perform(delete("/api/genres/99"))
                .andExpect(status().isNotFound());
    }
}


