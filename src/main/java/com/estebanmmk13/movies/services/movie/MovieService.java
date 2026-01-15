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

    Page<Movie> findMovieByTitleContaining(String title, Pageable pageable);

    Page<Movie> findAllMoviesByGenre(String name, Pageable pageable);
}

