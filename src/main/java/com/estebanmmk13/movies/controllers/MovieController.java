/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.services.movie.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/movies")
@CrossOrigin
@Tag(name = "Movies", description = "Operations related to movies management")
public class MovieController {

    @Autowired
    private MovieService movieService;

    // --------------------- CRUD ---------------------

    @Operation(
            summary = "Get all movies",
            description = "Retrieve a paginated list of all movies"
    )
    @GetMapping
    public Page<Movie> findAllMovies(
            @Parameter(description = "Pagination information") Pageable pageable) {
        return movieService.findAllMovies(pageable);
    }

    @Operation(summary = "Get movie by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Movie> findMovieById(
            @Parameter(description = "ID of the movie to retrieve") @PathVariable Long id) {
        Movie movie = movieService.findMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @Operation(summary = "Create a new movie")
    @PostMapping
    public ResponseEntity<Movie> createMovie(
            @Parameter(description = "Movie object to create") @Valid @RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @Operation(summary = "Update an existing movie")
    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(
            @Parameter(description = "ID of the movie to update") @PathVariable Long id,
            @Parameter(description = "Movie fields to update") @RequestBody Movie movie) {
        Movie updatedMovie = movieService.updateMovie(id, movie);
        return ResponseEntity.ok(updatedMovie);
    }

    @Operation(summary = "Delete a movie by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "ID of the movie to delete") @PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------- Custom queries ---------------------

    @Operation(summary = "Find movies by title")
    @GetMapping("/title")
    public ResponseEntity<Page<Movie>> findMovieByTitle(
            @Parameter(description = "Title or partial title to search for") @RequestParam String title,
            HttpServletRequest request,
            Pageable pageable) throws BadRequestException {

        if (title.isBlank()) {
            throw new BadRequestException("Title parameter is required");
        }

        Page<Movie> movies = movieService.findMovieByTitleContaining(title, pageable);
        return ResponseEntity.ok(movies);
    }

    @Operation(summary = "Vote a movie")
    @PutMapping("/{movieId}/vote/{userId}/{rating}")
    public ResponseEntity<Movie> voteMovie(
            @Parameter(description = "ID of the movie to vote") @PathVariable Long movieId,
            @Parameter(description = "ID of the user who votes") @PathVariable Long userId,
            @Parameter(description = "Rating value") @PathVariable Double rating) {
        Movie voted = movieService.voteMovie(movieId, userId, rating);
        return ResponseEntity.ok(voted);
    }

    @Operation(summary = "Find movies by genre")
    @GetMapping("/genre/{name}")
    public ResponseEntity<Page<Movie>> findAllMoviesByGenre(
            @Parameter(description = "Name of the genre to filter by") @PathVariable String name,
            Pageable pageable) {
        Page<Movie> movies = movieService.findAllMoviesByGenre(name, pageable);
        return ResponseEntity.ok(movies);
    }
}