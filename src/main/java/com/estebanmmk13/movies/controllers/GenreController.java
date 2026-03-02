package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.services.genre.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/genres")
@CrossOrigin
@Tag(name = "Genres", description = "Operations related to genre management")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @Operation(
            summary = "Get all genres",
            description = "Retrieve a paginated list of all available genres"
    )
    @GetMapping
    public Page<Genre> findAllGenre(
            @Parameter(description = "Pagination information") Pageable pageable) {
        return genreService.findAllGenres(pageable);
    }

    @Operation(summary = "Get genre by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Genre> findGenreById(
            @Parameter(description = "ID of the genre to retrieve") @PathVariable Long id) {
        Genre genre = genreService.findGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @Operation(summary = "Create a new genre")
    @PostMapping
    public ResponseEntity<Genre> createGenre(
            @Parameter(description = "Genre object to create") @Valid @RequestBody Genre genre) {
        Genre createdGenre = genreService.createGenre(genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    @Operation(summary = "Update an existing genre")
    @PatchMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(
            @Parameter(description = "ID of the genre to update") @PathVariable Long id,
            @Parameter(description = "Genre fields to update") @Valid @RequestBody Genre genre) {
        Genre updatedGenre = genreService.updateGenre(id, genre);
        return ResponseEntity.ok(updatedGenre);
    }

    @Operation(summary = "Delete a genre by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(
            @Parameter(description = "ID of the genre to delete") @PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Find genres by name",
            description = "Retrieve a paginated list of genres filtered by name"
    )
    @GetMapping("/name/{name}")
    public ResponseEntity<Page<Genre>> findGenreByName(
            @Parameter(description = "Name or partial name of the genre to search for")
            @PathVariable String name,
            @Parameter(description = "Pagination information") Pageable pageable) {

        Page<Genre> genre = genreService.findGenreByName(name, pageable);
        return ResponseEntity.ok(genre);
    }
}