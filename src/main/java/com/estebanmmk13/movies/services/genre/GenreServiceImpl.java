package com.estebanmmk13.movies.services.genre;

import com.estebanmmk13.movies.error.notFound.GenreNotFoundException;
import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GenreServiceImpl implements GenreService {

    private static final String NOT_FOUND_BY_ID = "Genre not found with id: %d";
    private static final String NOT_FOUND_BY_NAME = "Genre not found with name: %s";

    @Autowired
    private GenreRepository genreRepository; // Corregido el nombre del campo

    @Override
    public Page<Genre> findAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    @Override
    public Genre findGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_ID, id)));
    }

    @Override
    public Genre createGenre(Genre genre) {
        // Podríamos añadir validación para evitar duplicados
        if (genreRepository.existsByNameIgnoreCase(genre.getName())) {
            throw new IllegalArgumentException("Genre already exists with name: " + genre.getName());
        }
        return genreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(Long id, Genre genre) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_ID, id)));

        // Si está cambiando el nombre, verificar que no exista otro con ese nombre
        if (!existingGenre.getName().equalsIgnoreCase(genre.getName()) &&
                genreRepository.existsByNameIgnoreCase(genre.getName())) {
            throw new IllegalArgumentException("Another genre already exists with name: " + genre.getName());
        }

        genre.setId(id);
        return genreRepository.save(genre);
    }

    @Override
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new GenreNotFoundException(String.format(NOT_FOUND_BY_ID, id));
        }
        genreRepository.deleteById(id);
    }

    @Override
    public Page<Genre> findGenreByName(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be empty");
        }
        // Usamos el nuevo método del repositorio
        return genreRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
    }

    // Método adicional útil para búsquedas exactas
    @Override
    public Genre findGenreByExactName(String name) {
        return genreRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_NAME, name)));
    }
}
