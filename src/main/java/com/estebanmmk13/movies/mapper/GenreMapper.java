package com.estebanmmk13.movies.mapper;

import com.estebanmmk13.movies.dtoModels.response.GenreResponseDTO;
import com.estebanmmk13.movies.models.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreResponseDTO toResponseDTO(Genre genre) {
        if (genre == null) return null;
        return new GenreResponseDTO(genre.getId(), genre.getName());
    }
}