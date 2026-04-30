package com.estebanmmk13.movies.mapper;


import com.estebanmmk13.movies.dtoModels.response.GenreResponseDTO;
import com.estebanmmk13.movies.dtoModels.request.MovieRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.MovieResponseDTO;
import com.estebanmmk13.movies.models.Movie;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    // Convierte entidad Movie a MovieResponseDTO
    public MovieResponseDTO toResponseDTO(Movie movie) {
        if (movie == null) return null;

        List<GenreResponseDTO> genreDTOs = movie.getGenres() == null ? new ArrayList<>() :
                movie.getGenres().stream()
                        .map(genre -> new GenreResponseDTO(genre.getId(), genre.getName()))
                        .collect(Collectors.toList());

        return new MovieResponseDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getMovieYear(),
                movie.getVotes(),
                movie.getRating(),
                movie.getImageUrl(),
                genreDTOs
        );
    }

    // Convierte MovieRequestDTO a entidad Movie (para creación)
    public Movie toEntity(MovieRequestDTO dto) {
        if (dto == null) return null;

        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setMovieYear(dto.getMovieYear());
        movie.setImageUrl(dto.getImageUrl());
        movie.setVotes(0);
        movie.setRating(0.0);
        movie.setGenres(new ArrayList<>());  // se asignarán después en el servicio
        return movie;
    }

    // Actualiza una entidad existente con los datos del DTO (para PUT)
    public void updateEntity(Movie existing, MovieRequestDTO dto) {
        if (dto == null) return;

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setMovieYear(dto.getMovieYear());
        existing.setImageUrl(dto.getImageUrl());
        // Los géneros NO se actualizan aquí, se manejan aparte en el servicio
        // votes y rating no se tocan
    }
}