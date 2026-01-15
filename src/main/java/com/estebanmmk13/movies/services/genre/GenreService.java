package com.estebanmmk13.movies.services.genre;

import com.estebanmmk13.movies.models.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenreService {

    Page<Genre> findAllGenres(Pageable pageable);

    Genre findGenreById(Long id);

    Genre createGenre(Genre genre);

    Genre updateGenre(Long id,Genre genre);

    void deleteGenre(Long id);

    Page<Genre> findGenreByName(String name, Pageable pageable);
}
