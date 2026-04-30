package com.estebanmmk13.movies.services.genre;

import com.estebanmmk13.movies.dtoModels.response.GenreResponseDTO;
import com.estebanmmk13.movies.error.notFound.GenreNotFoundException;
import com.estebanmmk13.movies.mapper.GenreMapper;
import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.repositories.GenreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GenreServiceImpl implements GenreService {

    private static final String NOT_FOUND_BY_ID = "Genre not found with id: %d";
    private static final String NOT_FOUND_BY_NAME = "Genre not found with name: %s";

    private final GenreRepository genreRepository; // Corregido el nombre del campo
    private final GenreMapper genreMapper;

    // Constructor injection
    public GenreServiceImpl(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Override
    public Page<GenreResponseDTO> findAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable)
                .map(genreMapper::toResponseDTO);
    }

    @Override
    public GenreResponseDTO findGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_ID, id)));
        return genreMapper.toResponseDTO(genre);

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
    public Page<GenreResponseDTO> findGenreByName(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be empty");
        }
        return genreRepository.findByNameContainingIgnoreCase(name.trim(), pageable)
                .map(genreMapper::toResponseDTO);
    }

    // Método adicional útil para búsquedas exactas
    @Override
    public GenreResponseDTO findGenreByExactName(String name) {
        Genre genre = genreRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_NAME, name)));
        return genreMapper.toResponseDTO(genre);
    }
}