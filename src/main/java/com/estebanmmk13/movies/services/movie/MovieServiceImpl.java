package com.estebanmmk13.movies.services.movie;

import com.estebanmmk13.movies.dtoModels.request.MovieRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.MovieResponseDTO;
import com.estebanmmk13.movies.error.DuplicateVoteException;
import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.mapper.MovieMapper;
import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.models.Vote;
import com.estebanmmk13.movies.repositories.GenreRepository;
import com.estebanmmk13.movies.repositories.MovieRepository;
import com.estebanmmk13.movies.repositories.UserRepository;
import com.estebanmmk13.movies.repositories.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final MovieMapper movieMapper;

    // Constructor injection
    public MovieServiceImpl(MovieRepository movieRepository,
                            GenreRepository genreRepository,
                            VoteRepository voteRepository,
                            UserRepository userRepository,
                            MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public Page<MovieResponseDTO> findAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(movieMapper::toResponseDTO);
    }

    @Override
    public MovieResponseDTO findMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        return movieMapper.toResponseDTO(movie);
    }

    @Override
    public MovieResponseDTO createMovie(MovieRequestDTO dto) {
        Movie movie = movieMapper.toEntity(dto);
        // Asignar géneros si vienen IDs
        if (dto.getGenreIds() != null && !dto.getGenreIds().isEmpty()) {
            List<Genre> genres = genreRepository.findAllById(dto.getGenreIds());
            movie.setGenres(genres);
        } else {
            movie.setGenres(new ArrayList<>());
        }
        Movie saved = movieRepository.save(movie);
        return movieMapper.toResponseDTO(saved);
    }

    @Override
    public MovieResponseDTO updateMovie(Long id, MovieRequestDTO dto) {
        Movie existing = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        movieMapper.updateEntity(existing, dto);
        // Actualizar géneros si se proporcionan
        if (dto.getGenreIds() != null) {
            List<Genre> genres = genreRepository.findAllById(dto.getGenreIds());
            existing.setGenres(genres);
        }
        Movie updated = movieRepository.save(existing);
        return movieMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        movieRepository.delete(movie);
    }

    @Override
    public MovieResponseDTO voteMovie(Long movieId, Long userId, Double rating) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + movieId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

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

        Movie updatedMovie = movieRepository.save(movie);
        return movieMapper.toResponseDTO(updatedMovie);
    }

    @Override
    public Page<MovieResponseDTO> findMovieByTitleContaining(String title, Pageable pageable) {
        return movieRepository.findMovieByTitleContaining(title, pageable)
                .map(movieMapper::toResponseDTO);
    }

    @Override
    public Page<MovieResponseDTO> findAllMoviesByGenre(String name, Pageable pageable) {
        return movieRepository.findAllByGenreName(name, pageable)
                .map(movieMapper::toResponseDTO);
    }
}