package com.estebanmmk13.movies.services.genre;

import com.estebanmmk13.movies.error.notFound.GenreNotFoundException;
import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.estebanmmk13.movies.error.notFound.GenreNotFoundException.*;

@Service
public class GenreServiceImpl implements GenreService{

    @Autowired
    GenreRepository genreRespository;

    @Override
    public List<Genre> findAllGenres() {return genreRespository.findAll();}

    @Override
    public Genre findGenreById(Long id) {
        return genreRespository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_ID, id)));
    }

    @Override
    public Genre createGenre(Genre genre) {return genreRespository.save(genre);}

    @Override
    public Genre updateGenre(Long id,Genre genre) {

        Genre existingGenre = genreRespository.findById(id)
                .orElseThrow(()-> new GenreNotFoundException(String.format(NOT_FOUND_BY_ID,id)));

        genre.setId(id);
        return genreRespository.save(genre);
    }

    @Override
    public void deleteGenre(Long id) {

        if (!genreRespository.existsById(id)){
            throw new GenreNotFoundException(String.format(NOT_FOUND_BY_ID,id));
        }
        genreRespository.deleteById(id);
    }

    @Override
    public Genre findGenreByNameIgnoreCase(String name) {
        return genreRespository.findGenreByNameIgnoreCase(name)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_NAME,name)));
    }
}
