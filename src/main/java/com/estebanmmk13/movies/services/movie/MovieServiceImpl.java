package com.estebanmmk13.movies.services.movie;

import com.estebanmmk13.movies.error.DuplicateVoteException;
import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.models.Vote;
import com.estebanmmk13.movies.repositories.MovieRepository;
import com.estebanmmk13.movies.repositories.UserRepository;
import com.estebanmmk13.movies.repositories.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.estebanmmk13.movies.error.notFound.MovieNotFoundException.*;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie findMovieById(Long id) {

        return movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(String.format(NOT_FOUND_BY_ID, id)));
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie movie) {

        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(String.format(NOT_FOUND_BY_ID, id)));

        movie.setId(id);
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {

        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException(String.format(NOT_FOUND_BY_ID, id));
        }
        movieRepository.deleteById(id);
    }

    public Movie voteMovie(Long movieId, Long userId, Double rating) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(String.format(NOT_FOUND_BY_ID,movieId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(NOT_FOUND_BY_ID,userId)));

        if (voteRepository.existsByUserAndMovie(user, movie)) {
            throw new DuplicateVoteException("You already voted this movie.");
        }

        Vote vote = Vote.builder()
                .movie(movie)
                .user(user)
                .rating(rating)
                .votedAt(LocalDateTime.now())
                .build();

        voteRepository.save(vote);

        // Actualizar rating promedio y número de votos
        double totalRating = movie.getRating() * movie.getVotes() + rating;
        int totalVotes = movie.getVotes() + 1;
        movie.setVotes(totalVotes);
        movie.setRating(totalRating / totalVotes);

        return movieRepository.save(movie);
    }

    @Override
    public Movie findMovieByTitleIgnoreCase(String title) {

        return movieRepository.findMovieByTitleIgnoreCase(title)
                .orElseThrow(() -> new MovieNotFoundException(String.format(NOT_FOUND_BY_TITLE, title)));
    }

    @Override
    public List<Movie> findAllMoviesByGenreIgnoreCase(String name) {
        return movieRepository.findAllByGenreNameIgnoreCase(name);
    }

}
