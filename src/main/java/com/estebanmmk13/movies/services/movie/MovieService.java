package com.estebanmmk13.movies.services.movie;

import com.estebanmmk13.movies.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {

    Page<Movie> findAllMovies(Pageable pageable);

    Movie findMovieById(Long id);

    Movie createMovie(Movie movie);

    Movie updateMovie(Long id, Movie movie);

    void  deleteMovie(Long id);

    Movie voteMovie(Long movieId, Long userId, Double rating);

    Page<Movie> findMovieByTitleIgnoreCaseContaining(String title, Pageable pageable);

    List<Movie> findAllMoviesByGenreIgnoreCase(String name);
}

