/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.services.movie.MovieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author esteb
 */

@RestController
@RequestMapping("api/movies")
@CrossOrigin
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public Page<Movie> findAllMovies(Pageable pageable) {
        return movieService.findAllMovies(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> findMovieById(@PathVariable Long id) {
        Movie movie = movieService.findMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        Movie updatedMovie = movieService.updateMovie(id, movie);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/title")
    public ResponseEntity<Page<Movie>> findMovieByTitle(@RequestParam String title, HttpServletRequest request, Pageable pageable) throws BadRequestException {
        if (title.isBlank()) {
            throw new BadRequestException("Title parameter is required");
        }
        Page<Movie> movies = movieService.findMovieByTitleContaining(title,pageable);
        return ResponseEntity.ok(movies);
    }

    @PutMapping("/{movieId}/vote/{userId}/{rating}")
    public ResponseEntity<Movie> voteMovie(
            @PathVariable Long movieId,
            @PathVariable Long userId,
            @PathVariable Double rating) {
        Movie voted = movieService.voteMovie(movieId, userId, rating);
        return ResponseEntity.ok(voted);
    }

    @GetMapping("/genre/{name}")
    public ResponseEntity<Page<Movie>> findAllMoviesByGenre(@PathVariable String name, Pageable pageable) {
        Page<Movie> movies = movieService.findAllMoviesByGenre(name, pageable);
        return ResponseEntity.ok(movies);
    }

}
