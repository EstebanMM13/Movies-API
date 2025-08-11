package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.config.ReviewMapper;
import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.ReviewNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.Review;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.modelsRest.ReviewRest;
import com.estebanmmk13.movies.services.movie.MovieService;
import com.estebanmmk13.movies.services.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@ActiveProfiles("test")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    private Review review;

    @MockitoBean
    private ReviewMapper reviewMapper;

    @BeforeEach
    void setUp() {
        review = Review.builder()
                .id(1L)
                .comment("Comentario original")
                .user(User.builder().id(1L).username("Esteban").password("1234").build())
                .movie(Movie.builder().id(1L).title("Película").build())
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(reviewMapper.toRest(Mockito.any(Review.class)))
                .thenAnswer(invocation -> {
                    Review r = invocation.getArgument(0);
                    return new ReviewRest(
                            r.getId(),
                            r.getComment(),
                            r.getUser().getId(),
                            r.getMovie().getId(),
                            r.getCreatedAt()
                    );
                });
    }

    @Test
    void updateReview() throws Exception {
        Review updatedReview = Review.builder()
                .id(1L)
                .comment("Comentario actualizado")
                .user(User.builder().id(1L).username("Esteban").build())
                .movie(Movie.builder().id(1L).title("Película").build())
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(reviewService.updateReview(1L, 1L, "Comentario actualizado"))
                .thenReturn(updatedReview);

        mockMvc.perform(patch("/api/movies/1/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "userId": 1,
                            "comment": "Comentario actualizado"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Comentario actualizado"));
    }

    @Test
    @DisplayName("Debería devolver 404 si la review no existe")
    void updateReviewNotFound() throws Exception {
        Mockito.when(reviewService.updateReview(99L, 1L, "Comentario"))
                .thenThrow(new ReviewNotFoundException("No encontrada"));

        mockMvc.perform(patch("/api/movies/1/reviews/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "userId": 1,
                                "comment": "Comentario"
                            }
                            """))
                .andExpect(status().isNotFound());
    }
}
