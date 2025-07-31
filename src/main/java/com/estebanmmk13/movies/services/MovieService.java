package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.error.MovieNotFoundException;
import com.estebanmmk13.movies.models.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> getAllMovies();

    Optional<Movie> getMovieById(Long id) throws MovieNotFoundException;

    Movie createMovie(Movie movie);

    Optional<Movie> updateMovie(Long id, Movie movie);

    boolean deleteMovie(Long id);

    Optional<Movie> voteMovie(Long id, Double rating);

    Optional<Movie> findByTitleIgnoreCase(String title);

}
