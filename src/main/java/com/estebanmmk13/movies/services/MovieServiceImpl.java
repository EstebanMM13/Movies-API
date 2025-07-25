package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.error.MovieNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService{

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long id) throws MovieNotFoundException {
        Optional<Movie> movie = movieRepository.findById(id);
        if (!movie.isPresent()){
            throw  new MovieNotFoundException("Movie is not available");
        }
        return movie;
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Optional<Movie> updateMovie(Long id, Movie movie) {
        if (!movieRepository.existsById(id)) {
            return Optional.empty();
        }
        movie.setId(id);
        return Optional.of(movieRepository.save(movie));
    }

    public boolean deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            return false;
        }
        movieRepository.deleteById(id);
        return true;
    }

    public Optional<Movie> voteMovie(Long id, Double rating) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isEmpty()) {
            return Optional.empty();
        }

        Movie movie = optionalMovie.get();
        double newRating = ((movie.getVotes() * movie.getRating()) + rating) / (movie.getVotes() + 1);
        movie.setVotes(movie.getVotes() + 1);
        movie.setRating(newRating);
        return Optional.of(movieRepository.save(movie));
    }

    @Override
    public Optional<Movie> findByTitleIgnoreCase(String title) {
        return movieRepository.findByTitleIgnoreCase(title);
    }
}
