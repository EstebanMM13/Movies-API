package com.estebanmmk13.movies.controllers;

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

    // GET ALL MOVIES
    @Operation(
            summary = "Find all movies",
            description = "Retrieve a paginated list of all movies"
    )
    @ApiResponse(responseCode = "200", description = "Movies retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<Movie>> findAllMovies(
            @Parameter(description = "Pagination information") Pageable pageable) {

        return ResponseEntity.ok(movieService.findAllMovies(pageable));
    }


    // GET MOVIE BY ID
    @Operation(summary = "Find movie by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movie found",
                    content = @Content(schema = @Schema(implementation = Movie.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movie not found",
                    content = @Content(schema = @Schema(implementation = ResponseError.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Movie> findMovieById(
            @Parameter(description = "ID of the movie to retrieve", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(movieService.findMovieById(id));
    }


    // CREATE MOVIE
    @Operation(summary = "Create a new movie")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Movie created successfully",
                    content = @Content(schema = @Schema(implementation = Movie.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ResponseError.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not authorized",
                    content = @Content(schema = @Schema(implementation = ResponseError.class))
            )
    })
    @PostMapping
    public ResponseEntity<Movie> createMovie(
            @Parameter(description = "Movie object to create", required = true)
            @Valid @RequestBody Movie movie) {

        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }


    // UPDATE MOVIE
    @Operation(summary = "Update an existing movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(
            @Parameter(description = "ID of the movie to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Fields to update")
            @RequestBody Movie movie) {

        return ResponseEntity.ok(movieService.updateMovie(id, movie));
    }


    // DELETE MOVIE
    @Operation(summary = "Delete a movie by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "ID of the movie to delete", required = true)
            @PathVariable Long id) {

        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }


    // SEARCH BY TITLE
    @Operation(summary = "Find movies by title")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movies found"),
            @ApiResponse(responseCode = "400", description = "Invalid title parameter")
    })
    @GetMapping("/title")
    public ResponseEntity<Page<Movie>> findMovieByTitle(
            @Parameter(description = "Title or partial title to search", required = true)
            @RequestParam String title,
            Pageable pageable) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title parameter is required");
        }

        return ResponseEntity.ok(
                movieService.findMovieByTitleContaining(title, pageable)
        );
    }


    // VOTE MOVIE
    @Operation(summary = "Vote a movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vote registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating"),
            @ApiResponse(responseCode = "404", description = "Movie or user not found")
    })
    @PutMapping("/{movieId}/vote/{userId}/{rating}")
    public ResponseEntity<Movie> voteMovie(
            @Parameter(description = "Movie ID", required = true)
            @PathVariable Long movieId,
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Rating value (e.g., 1-5)", required = true)
            @PathVariable Double rating) {

        // Validar rating
        if (rating < 1.0 || rating > 10.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }

        return ResponseEntity.ok(
                movieService.voteMovie(movieId, userId, rating)
        );
    }


    // FILTER BY GENRE
    @Operation(summary = "Find movies by genre")
    @ApiResponse(responseCode = "200", description = "Movies retrieved successfully")
    @GetMapping("/genre/{name}")
    public ResponseEntity<Page<Movie>> findAllMoviesByGenre(
            @Parameter(description = "Genre name", required = true)
            @PathVariable String name,
            Pageable pageable) {

        return ResponseEntity.ok(
                movieService.findAllMoviesByGenre(name, pageable)
        );
    }
}