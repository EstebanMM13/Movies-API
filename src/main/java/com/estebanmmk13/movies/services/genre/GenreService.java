package com.estebanmmk13.movies.services.genre;

import com.estebanmmk13.movies.models.Genre;

import java.util.List;

public interface GenreService {

    List<Genre> findAllGenres();

    Genre findGenreById(Long id);

    Genre createGenre(Genre genre);

    Genre updateGenre(Long id,Genre genre);

    void deleteGenre(Long id);

    Genre findGenreByNameIgnoreCase(String name);
}
