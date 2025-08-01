package com.estebanmmk13.movies.services.movie;

import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie findMovieById(Long id) {

        return movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie is not available"));
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie movie) {

        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with ID " + id + " not found"));

        movie.setId(id);
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {

        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie with ID " + id + " not found");
        }
        movieRepository.deleteById(id);
    }

    public Movie voteMovie(Long id, Double rating) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with ID " + id + " not found"));

        double newRating = ((movie.getVotes() * movie.getRating()) + rating) / (movie.getVotes() + 1);
        movie.setVotes(movie.getVotes() + 1);
        movie.setRating(newRating);
        return movieRepository.save(movie);
    }

    @Override
    public Movie findMovieByTitleIgnoreCase(String title) {

        return movieRepository.findMovieByTitleIgnoreCase(title)
                .orElseThrow(() -> new MovieNotFoundException("Movie with title '" + title + "' not found"));
    }
}
