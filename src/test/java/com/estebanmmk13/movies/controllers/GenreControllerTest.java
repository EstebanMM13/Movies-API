package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.dtoModels.GenreResponseDTO;
import com.estebanmmk13.movies.error.notFound.GenreNotFoundException;
import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.services.genre.GenreService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = "USER")
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    private Genre genre;
    private GenreResponseDTO genreResponseDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        genre = Genre.builder()
                .id(1L)
                .name("Acción")
                .build();

        genreResponseDTO = new GenreResponseDTO(1L, "Acción");
    }

    // ========== ENDPOINTS DE LECTURA (devuelven DTO) ==========

    @Test
    @DisplayName("GET /api/genres - Should return paginated list of GenreResponseDTO")
    void findAllGenres_ShouldReturnPageOfGenreResponseDTO() throws Exception {
        Page<GenreResponseDTO> genrePage = new PageImpl<>(List.of(genreResponseDTO), pageable, 1);

        when(genreService.findAllGenres(any(Pageable.class)))
                .thenReturn(genrePage);

        mockMvc.perform(get("/api/genres")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Acción"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/genres/{id} - Should return GenreResponseDTO when exists")
    void findGenreById_WhenExists_ShouldReturnGenreResponseDTO() throws Exception {
        when(genreService.findGenreById(1L)).thenReturn(genreResponseDTO);

        mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Acción"));
    }

    @Test
    @DisplayName("GET /api/genres/{id} - Should return 404 when not found")
    void findGenreById_WhenNotExists_ShouldReturn404() throws Exception {
        when(genreService.findGenreById(99L))
                .thenThrow(new GenreNotFoundException("Genre not found with id: 99"));

        mockMvc.perform(get("/api/genres/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/genres/name/{name} - Should return paginated GenreResponseDTO by name")
    void findGenreByName_ShouldReturnPageOfGenreResponseDTO() throws Exception {
        Page<GenreResponseDTO> genrePage = new PageImpl<>(List.of(genreResponseDTO), pageable, 1);

        when(genreService.findGenreByName(eq("Acción"), any(Pageable.class)))
                .thenReturn(genrePage);

        mockMvc.perform(get("/api/genres/name/Acción")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Acción"));
    }

    @Test
    @DisplayName("GET /api/genres/name/{name} - Should return empty page when not found")
    void findGenreByName_WhenNotFound_ShouldReturnEmptyPage() throws Exception {
        Page<GenreResponseDTO> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(genreService.findGenreByName(eq("Inexistente"), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/genres/name/Inexistente")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    // NOTA: No hay endpoint /search/exact en tu controlador, por eso eliminamos ese test
    // Si quieres probar búsqueda exacta, tendrías que crearlo en el controlador.

    // ========== ENDPOINTS DE ESCRITURA (devuelven entidad Genre) ==========

    @Test
    @DisplayName("POST /api/genres - Should create genre and return 201")
    void createGenre_ShouldReturnCreated() throws Exception {
        when(genreService.createGenre(any(Genre.class))).thenReturn(genre);

        mockMvc.perform(post("/api/genres")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Acción"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Acción"));
    }

    @Test
    @DisplayName("POST /api/genres - Should return 400 when validation fails")
    void createGenre_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/genres")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/genres/{id} - Should update genre and return 200")
    void updateGenre_ShouldReturnUpdatedGenre() throws Exception {
        Genre updatedGenre = Genre.builder()
                .id(1L)
                .name("Aventura")
                .build();

        when(genreService.updateGenre(eq(1L), any(Genre.class))).thenReturn(updatedGenre);

        // Usamos PATCH, no PUT
        mockMvc.perform(patch("/api/genres/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Aventura"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Aventura"));
    }

    @Test
    @DisplayName("PATCH /api/genres/{id} - Should return 404 when genre not found")
    void updateGenre_WhenNotExists_ShouldReturn404() throws Exception {
        when(genreService.updateGenre(eq(99L), any(Genre.class)))
                .thenThrow(new GenreNotFoundException("Genre not found with id: 99"));

        mockMvc.perform(patch("/api/genres/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Inexistente"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/genres/{id} - Should delete and return 204")
    void deleteGenre_ShouldReturnNoContent() throws Exception {
        doNothing().when(genreService).deleteGenre(1L);

        mockMvc.perform(delete("/api/genres/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/genres/{id} - Should return 404 when genre not found")
    void deleteGenre_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new GenreNotFoundException("Genre not found with id: 99"))
                .when(genreService).deleteGenre(99L);

        mockMvc.perform(delete("/api/genres/99")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}