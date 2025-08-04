package com.estebanmmk13.movies.services;

import org.junit.jupiter.api.BeforeEach;
import com.estebanmmk13.movies.error.notFound.GenreNotFoundException;
import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.repositories.GenreRepository;
import com.estebanmmk13.movies.services.genre.GenreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class GenreServiceTest {

    @Autowired
    private GenreService genreService;

    @MockitoBean
    private GenreRepository genreRepository;

    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = Genre.builder()
                .id(1L)
                .name("Acción")
                .build();
    }

    @Test
    @DisplayName("Debería devolver todos los géneros")
    void findAllGenres() {
        List<Genre> genres = List.of(genre);
        Mockito.when(genreRepository.findAll()).thenReturn(genres);

        List<Genre> result = genreService.findAllGenres();
        assertEquals(1, result.size());
        assertEquals("Acción", result.get(0).getName());
    }

    @Test
    @DisplayName("Debería devolver un género por ID")
    void findGenreById() {
        Mockito.when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        Genre result = genreService.findGenreById(1L);
        assertEquals("Acción", result.getName());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el género no existe por ID")
    void findGenreByIdNotFound() {
        Mockito.when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.findGenreById(99L));
    }

    @Test
    @DisplayName("Debería crear un género")
    void createGenre() {
        Mockito.when(genreRepository.save(genre)).thenReturn(genre);

        Genre result = genreService.createGenre(genre);
        assertEquals("Acción", result.getName());
    }

    @Test
    @DisplayName("Debería actualizar un género existente")
    void updateGenre() {
        Mockito.when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        Mockito.when(genreRepository.save(Mockito.any(Genre.class))).thenReturn(genre);

        Genre result = genreService.updateGenre(1L, genre);
        assertEquals("Acción", result.getName());
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar un género inexistente")
    void updateGenreNotFound() {
        Mockito.when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.updateGenre(1L, genre));
    }

    @Test
    @DisplayName("Debería eliminar un género existente")
    void deleteGenre() {
        Mockito.when(genreRepository.existsById(1L)).thenReturn(true);

        genreService.deleteGenre(1L);
        Mockito.verify(genreRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar un género inexistente")
    void deleteGenreNotFound() {
        Mockito.when(genreRepository.existsById(1L)).thenReturn(false);

        assertThrows(GenreNotFoundException.class, () -> genreService.deleteGenre(1L));
    }

    @Test
    @DisplayName("Debería encontrar género por nombre ignorando mayúsculas")
    void findGenreByNameIgnoreCase() {
        Mockito.when(genreRepository.findGenreByNameIgnoreCase("acción")).thenReturn(Optional.of(genre));

        Genre result = genreService.findGenreByNameIgnoreCase("acción");
        assertEquals("Acción", result.getName());
    }

    @Test
    @DisplayName("Debería lanzar excepción si no encuentra género por nombre")
    void findGenreByNameIgnoreCaseNotFound() {
        Mockito.when(genreRepository.findGenreByNameIgnoreCase("drama")).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.findGenreByNameIgnoreCase("drama"));
    }
}