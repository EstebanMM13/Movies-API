package com.estebanmmk13.movies.services.movie;

import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.models.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    List<Movie> findAllMovies();

    Movie findMovieById(Long id);

    Movie createMovie(Movie movie);

    Movie updateMovie(Long id, Movie movie);

    void  deleteMovie(Long id);

    Movie voteMovie(Long movieId, Long userId, Double rating);

    Movie findMovieByTitleIgnoreCase(String title);

    List<Movie> findAllMoviesByGenreIgnoreCase(String name);
}

