package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.dtoModels.request.ReviewRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.ReviewResponseDTO;
import com.estebanmmk13.movies.error.notFound.ReviewNotFoundException;
import com.estebanmmk13.movies.services.review.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
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
@WithMockUser(username = "esteban", roles = "USER")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    private ReviewResponseDTO reviewResponseDTO;
    private ReviewRequestDTO reviewRequestDTO;
    private Page<ReviewResponseDTO> reviewPage;

    @BeforeEach
    void setUp() {
        reviewResponseDTO = new ReviewResponseDTO(
                1L,
                "Muy buena película",
                LocalDateTime.now(),
                "esteban",
                "Peli épica"
        );

        reviewRequestDTO = new ReviewRequestDTO();
        reviewRequestDTO.setComment("Muy buena película");

        Pageable pageable = PageRequest.of(0, 10);
        reviewPage = new PageImpl<>(List.of(reviewResponseDTO), pageable, 1);
    }

    // ========== GET /api/movies/{movieId}/reviews ==========

    @Test
    @DisplayName("GET /api/movies/{movieId}/reviews - Debería devolver página de reseñas como DTOs")
    void findReviewsByMovie_ShouldReturnPageOfReviewResponseDTO() throws Exception {
        when(reviewService.findReviewsByMovieId(eq(1L), any(Pageable.class)))
                .thenReturn(reviewPage);

        mockMvc.perform(get("/api/movies/1/reviews")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].comment").value("Muy buena película"))
                .andExpect(jsonPath("$.content[0].username").value("esteban"))
                .andExpect(jsonPath("$.content[0].movieTitle").value("Peli épica"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(reviewService).findReviewsByMovieId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/movies/{movieId}/reviews - Debería devolver página vacía si no hay reseñas")
    void findReviewsByMovie_WhenNoReviews_ShouldReturnEmptyPage() throws Exception {
        Page<ReviewResponseDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(reviewService.findReviewsByMovieId(eq(1L), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/movies/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    // ========== GET /api/movies/{movieId}/reviews/{id} ==========

    @Test
    @DisplayName("GET /api/movies/{movieId}/reviews/{id} - Debería devolver reseña por ID")
    void findReviewById_WhenExists_ShouldReturnReviewResponseDTO() throws Exception {
        when(reviewService.findReviewById(1L)).thenReturn(reviewResponseDTO);

        mockMvc.perform(get("/api/movies/1/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comment").value("Muy buena película"))
                .andExpect(jsonPath("$.username").value("esteban"));
    }

    @Test
    @DisplayName("GET /api/movies/{movieId}/reviews/{id} - Debería devolver 404 si no existe")
    void findReviewById_WhenNotExists_ShouldReturn404() throws Exception {
        when(reviewService.findReviewById(99L))
                .thenThrow(new ReviewNotFoundException("Review not found with id: 99"));

        mockMvc.perform(get("/api/movies/1/reviews/99"))
                .andExpect(status().isNotFound());
    }

    // ========== POST /api/movies/{movieId}/reviews ==========

    @Test
    @DisplayName("POST /api/movies/{movieId}/reviews - Debería crear reseña y devolver 201")
    void createReview_ShouldReturnCreated() throws Exception {
        when(reviewService.createReview(eq(1L), eq(1L), any(ReviewRequestDTO.class)))
                .thenReturn(reviewResponseDTO);

        mockMvc.perform(post("/api/movies/1/reviews")
                        .param("userId", "1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comment").value("Muy buena película"));
    }

    @Test
    @DisplayName("POST /api/movies/{movieId}/reviews - Debería devolver 400 si comment está vacío")
    void createReview_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        ReviewRequestDTO invalidDTO = new ReviewRequestDTO();
        invalidDTO.setComment("");

        mockMvc.perform(post("/api/movies/1/reviews")
                        .param("userId", "1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("POST /api/movies/{movieId}/reviews - Debería devolver 404 si la película no existe")
    void createReview_WhenMovieNotFound_ShouldReturn404() throws Exception {
        when(reviewService.createReview(eq(1L), eq(99L), any(ReviewRequestDTO.class)))
                .thenThrow(new ReviewNotFoundException("Movie not found"));

        mockMvc.perform(post("/api/movies/99/reviews")
                        .param("userId", "1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isNotFound());
    }

    // ========== PATCH /api/movies/{movieId}/reviews/{id} ==========

    @Test
    @DisplayName("PATCH /api/movies/{movieId}/reviews/{id} - Debería actualizar reseña")
    void updateReview_ShouldReturnUpdatedReview() throws Exception {
        ReviewResponseDTO updatedDTO = new ReviewResponseDTO(
                1L, "Comentario actualizado", LocalDateTime.now(), "esteban", "Peli épica"
        );
        when(reviewService.updateReview(eq(1L), eq(1L), any(ReviewRequestDTO.class)))
                .thenReturn(updatedDTO);

        ReviewRequestDTO updateRequest = new ReviewRequestDTO();
        updateRequest.setComment("Comentario actualizado");

        mockMvc.perform(patch("/api/movies/1/reviews/1")
                        .param("userId", "1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Comentario actualizado"));
    }

    @Test
    @DisplayName("PATCH /api/movies/{movieId}/reviews/{id} - Debería devolver 404 si la reseña no existe")
    void updateReview_WhenNotFound_ShouldReturn404() throws Exception {
        when(reviewService.updateReview(eq(99L), eq(1L), any(ReviewRequestDTO.class)))
                .thenThrow(new ReviewNotFoundException("Review not found"));

        ReviewRequestDTO updateRequest = new ReviewRequestDTO();
        updateRequest.setComment("Nuevo comentario");

        mockMvc.perform(patch("/api/movies/1/reviews/99")
                        .param("userId", "1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE /api/movies/{movieId}/reviews/{id} ==========

    @Test
    @DisplayName("DELETE /api/movies/{movieId}/reviews/{id} - Debería eliminar y devolver 204")
    void deleteReview_ShouldReturnNoContent() throws Exception {
        doNothing().when(reviewService).deleteReview(1L, 1L);

        mockMvc.perform(delete("/api/movies/1/reviews/1")
                        .param("userId", "1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/movies/{movieId}/reviews/{id} - Debería devolver 404 si la reseña no existe")
    void deleteReview_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new ReviewNotFoundException("Review not found"))
                .when(reviewService).deleteReview(99L, 1L);

        mockMvc.perform(delete("/api/movies/1/reviews/99")
                        .param("userId", "1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ========== PAGINACIÓN ==========

    @Test
    @DisplayName("Debería pasar correctamente los parámetros de paginación al servicio")
    void findReviewsByMovie_ShouldPassPaginationParameters() throws Exception {
        when(reviewService.findReviewsByMovieId(eq(1L), any(Pageable.class)))
                .thenReturn(reviewPage);

        mockMvc.perform(get("/api/movies/1/reviews")
                        .param("page", "2")
                        .param("size", "5")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());

        verify(reviewService).findReviewsByMovieId(eq(1L), argThat(pageable ->
                pageable.getPageNumber() == 2 &&
                        pageable.getPageSize() == 5 &&
                        pageable.getSort().toString().contains("createdAt: DESC")
        ));
    }
}