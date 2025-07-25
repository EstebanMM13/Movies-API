package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.error.MovieNotFoundException;
import com.estebanmmk13.movies.models.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    public List<Movie> getAllMovies();
    public Optional<Movie> getMovieById(Long id) throws MovieNotFoundException;
    public Movie createMovie(Movie movie);
    public Optional<Movie> updateMovie(Long id, Movie movie);
    public boolean deleteMovie(Long id);
    public Optional<Movie> voteMovie(Long id, Double rating);
    Optional <Movie> findByTitleIgnoreCase(String title);

}
