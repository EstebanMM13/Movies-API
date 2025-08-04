package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.services.genre.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/genres")
@CrossOrigin
public class GenreController {

    @Autowired
    GenreService genreService;

    @GetMapping
    public List<Genre> findAllGenre() {return genreService.findAllGenres();}

    @GetMapping("/{id}")
    public ResponseEntity<Genre> findGenreById(@PathVariable Long id){
        Genre genre = genreService.findGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PostMapping
    public ResponseEntity<Genre> createGenre(@Valid @RequestBody Genre genre){
        Genre createdGenre = genreService.createGenre(genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id,@Valid @RequestBody Genre genre){
        Genre updatedGenre = genreService.updateGenre(id,genre);
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Genre> deleteGenre(@PathVariable Long id){
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Genre> findGenreByName(@PathVariable String name){
        Genre genre = genreService.findGenreByNameIgnoreCase(name);
        return ResponseEntity.ok(genre);
    }

}
