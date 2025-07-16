/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.repositories.MovieRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author esteb
 */

@RestController
@RequestMapping("api/movies")
public class MovieController {
    
    @Autowired
    private MovieRepository movieRespository;
    
    @CrossOrigin
    @GetMapping
    public List<Movie> getAllMovies()
    {
        return movieRespository.findAll();
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id){
        Optional<Movie> movie = movieRespository.findById(id);
        return movie.map(ResponseEntity::ok).orElseGet( () -> ResponseEntity.notFound().build());
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie){
        Movie savedMovie = movieRespository.save(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id){
        if(!movieRespository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        movieRespository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id,@RequestBody Movie movie){
        if(!movieRespository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        movie.setId(id);
        movieRespository.save(movie);
        return ResponseEntity.ok().body(movie);
    }

    @CrossOrigin
    @PutMapping("/{id}/{rating}")
    public ResponseEntity<Movie> voteMovie(@PathVariable Long id,@PathVariable Double rating){
        if(!movieRespository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        Movie movie = movieRespository.findById(id).get();

        double newRating = ( (movie.getVotes() * movie.getRating()) + rating) / (movie.getVotes() + 1);

        movie.setVotes(movie.getVotes() + 1);
        movie.setRating(newRating);

        Movie savedMovie = movieRespository.save(movie);
        return ResponseEntity.ok().body(savedMovie);



    }


}
