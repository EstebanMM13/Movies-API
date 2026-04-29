package com.estebanmmk13.movies.services.movie;

import com.estebanmmk13.movies.dtoModels.MovieRequestDTO;
import com.estebanmmk13.movies.dtoModels.MovieResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieService {

    Page<MovieResponseDTO> findAllMovies(Pageable pageable);

    MovieResponseDTO findMovieById(Long id);

    MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO);

    MovieResponseDTO updateMovie(Long id, MovieRequestDTO movieRequestDTO);

    void deleteMovie(Long id);

    // voteMovie puede seguir devolviendo MovieResponseDTO si quieres, o mantener Movie. Por ahora lo dejamos igual.
    MovieResponseDTO voteMovie(Long movieId, Long userId, Double rating);  // cambia el retorno a DTO

    Page<MovieResponseDTO> findMovieByTitleContaining(String title, Pageable pageable);

    Page<MovieResponseDTO> findAllMoviesByGenre(String name, Pageable pageable);
}