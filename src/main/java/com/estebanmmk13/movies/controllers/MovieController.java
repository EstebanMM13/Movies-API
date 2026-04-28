package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.dtoModels.MovieRequestDTO;
import com.estebanmmk13.movies.dtoModels.MovieResponseDTO;
import com.estebanmmk13.movies.error.dto.ResponseError;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.services.movie.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin
@Tag(name = "Movies", description = "Movie management endpoints")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "Find all movies", description = "Retrieve a paginated list of all movies")
    @ApiResponse(responseCode = "200", description = "Movies retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<MovieResponseDTO>> findAllMovies(Pageable pageable) {
        return ResponseEntity.ok(movieService.findAllMovies(pageable));
    }

    @Operation(summary = "Find movie by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie found"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> findMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.findMovieById(id));
    }

    @Operation(summary = "Create a new movie")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movie created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    @PostMapping
    public ResponseEntity<MovieResponseDTO> createMovie(@Valid @RequestBody MovieRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(dto));
    }

    @Operation(summary = "Update an existing movie (full replacement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PutMapping("/{id}")   // Cambiado de PATCH a PUT
    public ResponseEntity<MovieResponseDTO> updateMovie(@PathVariable Long id,
                                                        @Valid @RequestBody MovieRequestDTO dto) {
        return ResponseEntity.ok(movieService.updateMovie(id, dto));
    }

    @Operation(summary = "Delete a movie by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Find movies by title")
    @GetMapping("/title")
    public ResponseEntity<Page<MovieResponseDTO>> findMovieByTitle(@RequestParam String title,
                                                                   Pageable pageable) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title parameter is required");
        }
        return ResponseEntity.ok(movieService.findMovieByTitleContaining(title, pageable));
    }

    @Operation(summary = "Vote a movie")
    @PutMapping("/{movieId}/vote/{userId}/{rating}")
    public ResponseEntity<MovieResponseDTO> voteMovie(@PathVariable Long movieId,
                                                      @PathVariable Long userId,
                                                      @PathVariable Double rating) {
        if (rating < 1.0 || rating > 10.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
        return ResponseEntity.ok(movieService.voteMovie(movieId, userId, rating));
    }

    @Operation(summary = "Find movies by genre")
    @GetMapping("/genre/{name}")
    public ResponseEntity<Page<MovieResponseDTO>> findAllMoviesByGenre(@PathVariable String name,
                                                                       Pageable pageable) {
        return ResponseEntity.ok(movieService.findAllMoviesByGenre(name, pageable));
    }
}